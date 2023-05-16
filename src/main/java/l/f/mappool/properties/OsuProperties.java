package l.f.mappool.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "beatmap-selection.osu")
public class OsuProperties {
    /**
     * 监听回调,务必与osu设置页配置保持一致
     */
    String callbackUrl = "/bind";

    /**
     * osu api信息
     */
    Oauth oauth;

    @Data
    @AllArgsConstructor
    public static class Oauth {
        /**
         * 应用id
         */
        Integer id;

        /**
         * 应用 token
         */
        String token;
    }
}
