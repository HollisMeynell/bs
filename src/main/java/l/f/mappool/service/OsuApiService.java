package l.f.mappool.service;

import com.fasterxml.jackson.databind.JsonNode;
import l.f.mappool.entity.osu.BeatMap;
import l.f.mappool.entity.osu.BeatMapSet;
import l.f.mappool.entity.osu.MatchesSearch;
import l.f.mappool.entity.osu.OsuOauthUser;
import l.f.mappool.properties.BeatmapSelectionProperties;
import l.f.mappool.properties.OsuProperties;
import l.f.mappool.repository.osu.BeatMapRepository;
import l.f.mappool.repository.osu.BeatMapSetRepository;
import l.f.mappool.repository.osu.OsuUserRepository;
import l.f.mappool.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OsuApiService {
    /*
     * https://osu.ppy.sh/users/[uid]/card
     * https://osu.ppy.sh/users/[uid]/extra-pages/historical?mode=osu
     * https://osu.ppy.sh/users/[uid]/scores/best?mode=osu&limit=100&offset=0 bp成绩
     * https://osu.ppy.sh/users/18443135/recent_activity?limit=51&offset=0 近期活动
     * https://osu.ppy.sh/users/17064371/scores/recent?mode=osu&limit=51&offset=0 24h打图
     */
    long time = System.currentTimeMillis();
    private String accessToken;
    private WebClient webClient;
    private OsuUserRepository osuUserRepository;
    private BeatMapRepository beatMapRepository;
    private BeatMapSetRepository beatMapSetRepository;
    private final String redirectUrl;
    private final int oauthId;
    private final String oauthToken;

    @Autowired
    public OsuApiService(
            WebClient osuApiWebClient,
            OsuUserRepository osuUserRepository,
            BeatmapSelectionProperties properties,
            OsuProperties osuProperties,
            BeatMapRepository beatMapRepository,
            BeatMapSetRepository beatMapSetRepository
    ) {
        this.redirectUrl = String.format("http%s://%s%s",
                Boolean.TRUE.equals(properties.getSsl()) ? "s" : "",
                properties.getLocalUrl(),
                osuProperties.getCallbackUrl()
        );
        this.webClient = osuApiWebClient;
        this.osuUserRepository = osuUserRepository;
        this.oauthId = osuProperties.getOauth().getId();
        this.oauthToken = osuProperties.getOauth().getToken();
        this.beatMapRepository = beatMapRepository;
        this.beatMapSetRepository = beatMapSetRepository;
    }

    /**
     * 获取用于用户绑定的 Oauth 授权链接
     */
    public String getOauthUrl(String state) {
        return UriComponentsBuilder.fromHttpUrl("https://osu.ppy.sh/oauth/authorize").queryParam("client_id", oauthId).queryParam("redirect_uri", redirectUrl).queryParam("response_type", "code").queryParam("scope", "friends.read identify public").queryParam("state", state).build().encode().toUriString();
    }

    /**
     * bot 的 Oauth2 token 是否超时
     */
    private boolean isPassed() {
        return System.currentTimeMillis() > time;
    }

    /**
     * 获取 bot 的 Oauth2 token
     */
    public String getToken() {
        if (!isPassed()) {
            return accessToken;
        }
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", String.valueOf(oauthId));
        body.add("client_secret", oauthToken);
        body.add("grant_type", "client_credentials");
        body.add("scope", "public");

        var s = webClient.post()
                .uri("https://osu.ppy.sh/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
        if (s != null) {
            accessToken = s.get("access_token").asText();
            time = System.currentTimeMillis() + s.get("expires_in").asLong() * 1000;
        } else {
            throw new RuntimeException("更新 Oauth 令牌 请求失败");
        }
        return accessToken;
    }

    /**
     * 获取用户的 Oauth2 token
     */
    public OsuOauthUser getToken(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", String.valueOf(oauthId));
        body.add("client_secret", oauthToken);
        body.add("code", code);
        body.add("grant_type", "authorization_code");
        body.add("redirect_uri", redirectUrl);

        var s = webClient.post()
                .uri("https://osu.ppy.sh/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
        if (s == null) {
            throw new RuntimeException("");
        }
        String accessToken = s.get("access_token").asText();
        String refreshToken = s.get("refresh_token").asText();
        // 计算过期时间毫秒数
        time = System.currentTimeMillis() + s.get("expires_in").asLong() * 1000;
        var user = new OsuOauthUser();
        user.setAccessToken(accessToken);
        user.setRefreshToken(refreshToken);
        user.setTime(time);
        return user;
    }

    /***
     * 刷新令牌
     * @param osuOauthUser user
     * @return 令牌请求的原始类型
     */
    public JsonNode refreshToken(OsuOauthUser osuOauthUser) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", String.valueOf(oauthId));
        body.add("client_secret", oauthToken);
        body.add("refresh_token", osuOauthUser.getRefreshToken());
        body.add("grant_type", "refresh_token");
        body.add("redirect_uri", redirectUrl);

        JsonNode s = webClient.post()
                .uri("https://osu.ppy.sh/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (s == null) throw new RuntimeException("refresh token error");
        osuOauthUser.setRefreshToken(s.get("access_token").asText());
        osuOauthUser.setAccessToken(s.get("access_token").asText());
        osuOauthUser.nextTime(s.get("expires_in").asLong());
        osuUserRepository.updateToken(osuOauthUser);
        return s;
    }

    public OsuOauthUser getMeInfo(OsuOauthUser user) {
        var data = webClient.get()
                .uri("/me")
                .header("Authorization", "Bearer " + user.getAccessToken(this))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
        if (data != null) {
            user.setName(data.get("username").asText("unknown"));
            user.setOsuId(data.get("id").asLong(0));
        }
        osuUserRepository.saveAndFlush(user);
        return user;
    }

    public BeatMapSet getMapsetInfo(long sid) {
        return webClient.get()
                .uri("/beatmapsets/{sid}", sid)
                .headers(this::insertHeader)
                .retrieve()
                .bodyToMono(BeatMapSet.class)
                .block();
    }

    public BeatMap getMapInfo(long bid) {
        return webClient.get()
                .uri("/beatmaps/{bid}", bid)
                .headers(this::insertHeader)
                .retrieve()
                .bodyToMono(BeatMap.class)
                .block();
    }

    public BeatMap getMapInfoByDB(long bid) {
        var map = beatMapRepository.findById(bid);
        return map.orElseGet(()->{
            var get = getMapInfo(bid);
            if (get.getStatus().equals("ranked")) {
                saveBeatMap(get);
            }
            return get;
        });
    }

    public List<JsonNode> getAllUser(Collection<? extends Number> usersId) {
        return webClient.get()
                .uri(b -> b.pathSegment("users").queryParam("ids[]", usersId).build())
                .headers(this::insertHeader)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(result -> JsonUtil.parseObjectList(result.get("users"), JsonNode.class))
                .block();
    }

    private void saveBeatMap(BeatMap map){
        var mapSet = beatMapSetRepository.findById(map.getMapsetId());
        if (mapSet.isEmpty()) {
            beatMapSetRepository.save(map.getBeatMapSet());
        }
        beatMapRepository.save(map);
    }

    public MatchesSearch getMatches() {
        return webClient.get()
                .uri("/matches")
                .headers(this::insertHeader)
                .retrieve()
                .bodyToMono(MatchesSearch.class)
                .block();
    }


    public JsonNode getMatchesInfo(long mid, Optional<Long> before, Optional<Long> after, int limit) {
        return webClient.get()
                .uri(p -> p
                        .pathSegment("matches", "{mid}")
                        .queryParam("limit", limit)
                        .queryParamIfPresent("before", before)
                        .queryParamIfPresent("after", after)
                        .build(mid))
                .headers(this::insertHeader)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    private void insertHeader(HttpHeaders headers) {
        headers.set("Authorization", "Bearer " + getToken());
    }
}
