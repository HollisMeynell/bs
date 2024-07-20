package l.f.mappool.vo.yasunaori;

import l.f.mappool.controller.YasunaoriApi;
import l.f.mappool.entity.osu.OsuUserOptional;
import lombok.*;

import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YasunaoriUserInfoVo {
    private String  error;
    private Long    id;
    private String  username;
    private String  avatarUrl;
    private String  countryCode;
    private Integer globalRank;
    private Integer countryRank;

    public YasunaoriUserInfoVo(String error) {
        this.error = error;
    }

    public YasunaoriUserInfoVo(OsuUserOptional info) {
        this.id = info.getId();
        this.username = info.getUserName();
        this.avatarUrl = YasunaoriApi.getAvatarUrl(Objects.requireNonNullElse(info.getId(), 0L));
        this.countryCode = info.getCountryCode();
        this.globalRank = info.getStatistics().getGlobalRank();
        this.countryRank = info.getStatistics().getCountryRank();
    }
}
