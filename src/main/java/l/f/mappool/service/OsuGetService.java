package l.f.mappool.service;

import com.fasterxml.jackson.databind.JsonNode;
import l.f.mappool.entity.BindUser;
import l.f.mappool.repository.BindUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

public class OsuGetService {
    long time = System.currentTimeMillis();
    String accessToken;
    RestTemplate template;
    BindUserRepository bindUserRepository;
    private final String redirectUrl;
    private int oauthId;
    private String oauthToken;

    @Autowired
    public OsuGetService(RestTemplate template, BindUserRepository bindUserRepository) {
        this.redirectUrl = "redirectUrl";
        this.template = template;
        this.bindUserRepository = bindUserRepository;
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
        MultiValueMap body = new LinkedMultiValueMap();
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

    /***
     * 刷新令牌
     * @param bindUser
     * @return
     */
    public JsonNode refreshToken(BindUser bindUser) {
        String url = "https://osu.ppy.sh/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED));
        MultiValueMap body = new LinkedMultiValueMap();
        body.add("client_id", oauthId);
        body.add("client_secret", oauthToken);
        body.add("refresh_token", bindUser.getRefreshToken());
        body.add("grant_type", "refresh_token");
        body.add("redirect_uri", redirectUrl);

        HttpEntity<?> httpEntity = new HttpEntity<>(body, headers);
        JsonNode s = template.postForObject(url, httpEntity, JsonNode.class);
        bindUserRepository.updateToken(bindUser.getOsuId(),
                s.get("access_token").asText(),
                s.get("refresh_token").asText(),
                bindUser.nextTime(s.get("expires_in").asLong()));
        return s;
    }
}
