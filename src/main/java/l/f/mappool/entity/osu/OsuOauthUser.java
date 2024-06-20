package l.f.mappool.entity.osu;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import l.f.mappool.service.OsuApiService;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Setter
@DynamicUpdate
@Table(name = "osu_oauth")
public class OsuOauthUser {
    @Id
    @Column(name = "osu_id")
    private Long osuId;

    @JsonProperty("username")
    @Column(name = "name", columnDefinition = "text")
    String name;

    @JsonProperty("max_size")
    @Column(name = "max_poolsize")
    Integer maxPoolSize = 10;

    @Column(name = "access_token", columnDefinition = "text")
    String accessToken;

    @Column(name = "refresh_token", columnDefinition = "text")
    String refreshToken;

    private Long time;

    public String getAccessToken(OsuApiService service) {
        if (accessToken == null) {
            return service.getToken();
        } else if (isPassed()) {
            accessToken = service.refreshToken(this).findValue("access_token").asText();
        }

        return accessToken;
    }

    public void nextTime(Long addTime) {
        time = System.currentTimeMillis() + addTime * 1000;
    }

    public boolean isPassed() {
        return System.currentTimeMillis() > time;
    }
}
