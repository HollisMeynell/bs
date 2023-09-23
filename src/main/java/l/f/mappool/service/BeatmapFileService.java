package l.f.mappool.service;

import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import l.f.mappool.entity.osu.OsuAccountUser;
import l.f.mappool.exception.LogException;
import l.f.mappool.repository.osu.OsuAccountUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.io.*;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

/**
 * 用于下载 .osz 文件的工具类, 文件来源于 ppy
 */
@Slf4j
@Service
@SuppressWarnings("unused")
public class BeatmapFileService {
    public static enum Type {
        BACKGROUND, AUDIO, FILE,
    }

    private static final HttpClient httpClient = HttpClient.create()
            .baseUrl("https://osu.ppy.sh/")
            .proxy(proxy ->
                    proxy.type(ProxyProvider.Proxy.SOCKS5)
                            .host("127.0.0.1")
                            .port(7890)
            )
            .followRedirect(true)
            .responseTimeout(Duration.ofSeconds(60));
    private static final ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
    private static final WebClient webClient = WebClient.builder()
            .clientConnector(connector)
            .defaultHeaders(headers -> headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED))
            .build();
    private static final String HOME_PAGE_URL = "/home";
    private static final String LOGIN_URL        = "/session";
    private static final String DOWNLOAD_OSZ_URL = "/beatmapsets/{bid}/download";
    private static final String DOWNLOAD_OSR_URL = "/scores/{mode}/{bid}/download";

    @Resource
    OsuAccountUserRepository accountUserRepository;

    public OsuAccountUser getRandomAccount() {
        long count = accountUserRepository.count();
        if (count == 0) throw new LogException("未获取到任何账号信息");
        return accountUserRepository.getByIndex(ThreadLocalRandom.current().nextLong(count));
    }

    private void initAccount(OsuAccountUser accountUser) {
        visitHomePage(accountUser);
        login(accountUser);
    }

    public InputStream downloadOsz(long sid, OsuAccountUser account) throws IOException {
        return doDownload(sid, account, null);
    }

    public InputStream downloadOsr(long scoreId, String mode,  OsuAccountUser account) throws IOException {
        return doDownload(scoreId, account, mode);
    }

    private InputStream doDownload(long sid,  OsuAccountUser account,@Nullable String mode) throws IOException {
        if (account.getSession() == null) initAccount(account);

        PipedOutputStream outputStream = new PipedOutputStream();
        PipedInputStream inputStream = new PipedInputStream(1024 * 10);
        inputStream.connect(outputStream);
        java.util.function.Consumer<Void> consumer;
        if (mode == null) {
            consumer = i -> downloadBody(account, outputStream, DOWNLOAD_OSZ_URL, sid);
        } else {
            consumer = i -> downloadBody(account, outputStream, DOWNLOAD_OSR_URL,  mode, sid);
        }
        try {
            consumer.accept(null);
        } catch (Exception e) {
            log.error("下载文件失败, 重试中", e);
            initAccount(account);
            consumer.accept(null);
        }
        return inputStream;
    }
    @SuppressWarnings("all")
    private void downloadBody(OsuAccountUser account, OutputStream out, String url, Object ... params) {
        var body = webClient.get()
                .uri(url, params)
                .headers(h -> {
                    setHeaders(h, account);
                    h.set("referer", "https://osu.ppy.sh/home");
                })
                .exchangeToFlux(clientResponse -> {
                    parseCookie(clientResponse.headers().asHttpHeaders(), account);
                    return clientResponse.body((e,r) -> e.getBody());
                })
                .doOnCancel(() -> log.error("download cancelled"))
                .doFinally((s) -> {
                    try {
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        DataBufferUtils.write(body, out)
                .subscribe();
    }

    private void login(OsuAccountUser accountUser) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.set("_token", accountUser.getToken());
        body.set("username", accountUser.getUsername());
        body.set("password", accountUser.getPassword());
        try {
            ResponseEntity<String> client = webClient.post()
                    .uri(LOGIN_URL)
                    .headers(h -> {
                        setHeaders(h, accountUser);
                        h.set("referer", "https://osu.ppy.sh" + LOGIN_URL);
                    })
                    .bodyValue(body)
                    .retrieve()
                    .toEntity(String.class)
                    .block();
            if (client == null || !client.getStatusCode().is2xxSuccessful()) throw new LogException("登录失败");
            log.info("login result: {}", client.getStatusCode().is2xxSuccessful());
            parseCookie(client.getHeaders(), accountUser);
        } catch (Exception e) {
            accountUser.setSession(null);
            accountUser.setToken(null);
            throw e;
        }

    }

    private void visitHomePage(OsuAccountUser accountUser) {
        var client = webClient.get()
                .uri(HOME_PAGE_URL)
                .headers(h -> setHeaders(h, accountUser))
                .retrieve()
                .toEntity(String.class)
                .block();
        if (client == null) throw new LogException("访问页面错误!");
        parseCookie(client.getHeaders(), accountUser);
    }

    private void setHeaders(HttpHeaders headers, OsuAccountUser user) {
        if (user.getToken() != null && user.getSession() != null) {
            headers.set("cookie", "XSRF-TOKEN=" + user.getToken() + "; osu_session=" + user.getSession());
        }
    }

    private void parseCookie(HttpHeaders headers, OsuAccountUser account) {
        String token = "";
        String session = "";
        var setCookie = headers.get("set-cookie");
        if (setCookie != null) {
            var pattern = Pattern.compile("(^XSRF-TOKEN=(?<token>\\w+);)|(^osu_session=(?<session>[\\w%]+);)");
            for (var str : setCookie) {
                var matcher = pattern.matcher(str);
                if (matcher.find()) {
                    if (matcher.group("token") != null && !matcher.group("token").equalsIgnoreCase("deleted"))
                        token = matcher.group("token");
                    else if (matcher.group("session") != null) session = matcher.group("session");
                }
                if (!token.equals("") && !session.equals("")) break;
            }
        }
        if (!token.isBlank()) {
            account.setToken(token);
        }
        if (!session.isBlank()) {
            account.setSession(session);
        }
        accountUserRepository.save(account);
    }
}
