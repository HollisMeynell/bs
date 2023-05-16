package l.f.mappool.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import l.f.mappool.service.OsuGetService;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate
@Table(name = "osu_user")
public class OsuUser {
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

    public Long getOsuId() {
        return osuId;
    }

    public void setOsuId(Long osuId) {
        this.osuId = osuId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getAccessToken(OsuGetService service) {
        if (accessToken == null) {
            return service.getToken();
        } else if (isPassed()) {
            accessToken = service.refreshToken(this).findValue("access_token").asText();
        }

        return accessToken;
    }


    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long nextTime(Long addTime) {
        time = System.currentTimeMillis() + addTime * 1000;
        return time;
    }

    public boolean isPassed() {
        return System.currentTimeMillis() > time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }
}
