package l.f.mappool.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import l.f.mappool.service.OsuGetService;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate
@Table(name = "bind_user")
public class BindUser {
    @Id
    @Column(name = "osu_id")
    private Long osuId;

    @Column(name = "access_token")
    String accessToken;

    @Column(name = "refresh_token")
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
}
