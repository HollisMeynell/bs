package l.f.mappool.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "beatmap-selection")
public class BeatmapSelectionProperties {
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
    /**
     * 管理员
     */
    List<Long> adminUsers;
}
