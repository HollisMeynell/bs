package l.f.mappool.properties;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Component
@ConfigurationProperties(prefix = "beatmap-selection")
public class BeatmapSelectionProperties {
    public static String URL = "http://localhost:8080";
    /**
     * 经过上传的文件保存路径
     */
    String filePath = "/";
    /**
     * 本机的访问链接
     */
    String localUrl = "localhost:8080";

    /**
     * 是否为https
     */
    Boolean ssl = false;

    Optional<String> localOsuDirectory = Optional.empty();
    /**
     * 管理员
     */
    List<Long>       adminUsers        = new ArrayList<>();

    @PostConstruct
    void setStaticUrl() {
        URL = (ssl ? "https://" : "http://") + localUrl;
    }
}
