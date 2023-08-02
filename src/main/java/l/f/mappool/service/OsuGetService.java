package l.f.mappool.service;

import com.fasterxml.jackson.databind.JsonNode;
import l.f.mappool.entity.BeatMap;
import l.f.mappool.entity.OsuUser;
import l.f.mappool.properties.BeatmapSelectionProperties;
import l.f.mappool.properties.OsuProperties;
import l.f.mappool.repository.OsuUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

@Service
public class OsuGetService {
    /***
     * https://osu.ppy.sh/users/[uid]/card
     * https://osu.ppy.sh/users/[uid]/extra-pages/historical?mode=osu
     * https://osu.ppy.sh/users/[uid]/scores/best?mode=osu&limit=100&offset=0 bp成绩
     * https://osu.ppy.sh/users/18443135/recent_activity?limit=51&offset=0 近期活动
     * https://osu.ppy.sh/users/17064371/scores/recent?mode=osu&limit=51&offset=0 24h打图
     */
    long time = System.currentTimeMillis();
    String accessToken;
    RestTemplate template;
    OsuUserRepository osuUserRepository;
    private final String redirectUrl;
    private final int oauthId;
    private final String oauthToken;

    @Autowired
    public OsuGetService(RestTemplate template, OsuUserRepository osuUserRepository, BeatmapSelectionProperties properties, OsuProperties osuProperties) {
        this.redirectUrl = String.format("http%s://%s%s",
                Boolean.TRUE.equals(properties.getSsl()) ? "s" : "",
                properties.getLocalUrl(),
                osuProperties.getCallbackUrl()
        );
        this.template = template;
        this.osuUserRepository = osuUserRepository;
        this.oauthId = osuProperties.getOauth().getId();
        this.oauthToken = osuProperties.getOauth().getToken();
    }

    public String getOauthUrl(String state) {
        return UriComponentsBuilder.fromHttpUrl("https://osu.ppy.sh/oauth/authorize").queryParam("client_id", oauthId).queryParam("redirect_uri", redirectUrl).queryParam("response_type", "code").queryParam("scope", "friends.read identify public").queryParam("state", state).build().encode().toUriString();
    }


    private boolean isPassed() {
        return System.currentTimeMillis() > time;
    }

    public String getToken() {
        if (!isPassed()) {
            return accessToken;
        }
        String url = "https://osu.ppy.sh/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED));
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("client_id", oauthId);
        body.add("client_secret", oauthToken);
        body.add("grant_type", "client_credentials");
        body.add("scope", "public");

        HttpEntity<?> httpEntity = new HttpEntity<>(body, headers);
        var s = template.postForObject(url, httpEntity, JsonNode.class);
        accessToken = s.get("access_token").asText();
        time = System.currentTimeMillis() + s.get("expires_in").asLong() * 1000;
        return accessToken;
    }

    public OsuUser getToken(String code) {
        String url = "https://osu.ppy.sh/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED));
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("client_id", oauthId);
        body.add("client_secret", oauthToken);
        body.add("code", code);
        body.add("grant_type", "authorization_code");
        body.add("redirect_uri", redirectUrl);

        HttpEntity<?> httpEntity = new HttpEntity<>(body, headers);
        var s = template.postForObject(url, httpEntity, JsonNode.class);
        if (s == null) {
            throw new RuntimeException("");
        }
        String accessToken = s.get("access_token").asText();
        String refreshToken = s.get("refresh_token").asText();
        time = System.currentTimeMillis() + s.get("expires_in").asLong() * 1000;
        var user = new OsuUser();
        user.setAccessToken(accessToken);
        user.setRefreshToken(refreshToken);
        user.setTime(time);
        return user;
    }

    /***
     * 刷新令牌
     * @param osuUser
     * @return
     */
    public JsonNode refreshToken(OsuUser osuUser) {
        String url = "https://osu.ppy.sh/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED));
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("client_id", oauthId);
        body.add("client_secret", oauthToken);
        body.add("refresh_token", osuUser.getRefreshToken());
        body.add("grant_type", "refresh_token");
        body.add("redirect_uri", redirectUrl);

        HttpEntity<?> httpEntity = new HttpEntity<>(body, headers);
        JsonNode s = template.postForObject(url, httpEntity, JsonNode.class);
        if (s == null) throw new RuntimeException("refresh token error");
        osuUser.setRefreshToken(s.get("access_token").asText());
        osuUser.setAccessToken(s.get("access_token").asText());
        osuUser.nextTime(s.get("expires_in").asLong());
        osuUserRepository.updateToken(osuUser);
        return s;
    }

    public OsuUser getMeInfo(OsuUser user) {
        String url = "https://osu.ppy.sh/api/v2/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + user.getAccessToken(this));
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<JsonNode> c = template.exchange(url, HttpMethod.GET, httpEntity, JsonNode.class);
        var data = c.getBody();
        if (data != null) {
            user.setName(data.get("username").asText("unknown"));
            user.setOsuId(data.get("id").asLong(0));
        }
        osuUserRepository.saveAndFlush(user);
        return user;
    }

    public BeatMap getMapInfo(long bid) {
        String url = "https://osu.ppy.sh/api/v2/beatmaps/" + bid;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + getToken());

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<BeatMap> c = template.exchange(url, HttpMethod.GET, httpEntity, BeatMap.class);
        return c.getBody();
    }
}
