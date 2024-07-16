package l.f.mappool.vo.yasunaori;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import l.f.mappool.controller.YasunaoriApi;
import l.f.mappool.entity.osu.OsuUserOptional;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
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
        this.avatarUrl = info.getAvatarUrl()
                .replace(YasunaoriApi.OSU_AVATAR_PREFIX, YasunaoriApi.AVATAR_URL_PREFIX);
        this.countryCode = info.getCountryCode();
        this.globalRank = info.getStatistics().getGlobalRank();
        this.countryRank = info.getStatistics().getCountryRank();
    }
}
