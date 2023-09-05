package l.f.mappool.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;

import java.io.BufferedReader;
import java.io.IOException;

@Entity
@Getter
@Setter
@DynamicUpdate
@Table(name = "beatmap_files", indexes = {
        @Index(name = "local_dir", columnList = "sid")
})
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class OsuFileLog {
    @Id
    Long bid;
    Long sid;
    @Column(name = "file_name")
    String file;
    String background;
    String audio;
    Integer mode;

    public static OsuFileLog parse(BufferedReader read) throws IOException {
        OsuFileLog obj = new OsuFileLog();
        try (read) {
            var versionStr = read.readLine();
            if (versionStr == null || !versionStr.startsWith("osu file format v")) {
                throw new RuntimeException("解析错误,文件无效");
            }

            String line;
            while ((line = read.readLine()) != null) {
                if (line.startsWith("[Events]")) {
                    read.readLine();
                    line = read.readLine();

                    int start = line.indexOf('"');
                    int end = line.lastIndexOf('"');
                    obj.background = line.substring(start + 1, end);
                    break;
                }
                if (line.startsWith("[") || line.isBlank()) continue;
                var entity = line.split(":");
                if (entity.length < 2) {
                    continue;
                }
                var key = entity[0].trim();
                var val = entity[1].trim();

                if (key.equals("AudioFilename")) obj.audio = val;
                if (key.equals("Mode")) obj.mode = Integer.parseInt(val);
                if (key.equals("BeatmapID")) obj.bid = Long.parseLong(val);
                if (key.equals("BeatmapSetID")) obj.sid = Long.parseLong(val);
            }
        }
        return obj;
    }
}
