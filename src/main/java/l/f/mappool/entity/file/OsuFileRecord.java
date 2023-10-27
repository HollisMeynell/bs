package l.f.mappool.entity.file;

import jakarta.persistence.*;
import l.f.mappool.service.OsuApiService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
public class OsuFileRecord {
    @Id
    Long bid;
    Long sid;
    @Column(name = "file_name")
    String file;
    String background;
    String audio;
    Integer mode;

    public static OsuFileRecord parse(BufferedReader read, OsuApiService osuApiService) throws IOException {
        OsuFileRecord obj = new OsuFileRecord();
        try (read) {
            var versionStr = read.readLine();
            if (versionStr == null || !versionStr.startsWith("osu file format v")) {
                throw new RuntimeException("解析错误,文件无效");
            }
            int version = Integer.parseInt(versionStr.substring(17));
            if (version < 5) {
                parse(read, obj, "AudioFilename");
                // mode
                // bid sid
            } else if (version < 11) {
                // bid sid
                parse(read, obj, "AudioFilename", "Mode");
            } else {
                parse(read, obj, "AudioFilename", "Mode", "BeatmapID", "BeatmapSetID");
            }
        }
        return obj;
    }

    private static void parse(BufferedReader read, OsuFileRecord obj, String... keys) throws IOException {
        String line;
        Map<String, String> result = new HashMap<>(keys.length);
        result.put("AudioFilename", null);
        for (String key : keys) {
            result.put(key, null);
        }
        while ((line = read.readLine()) != null) {
            if (line.startsWith("[Events]")) {
                obj.background = parseBackground(read);
                break;
            }
            if (line.startsWith("[") || line.isBlank()) continue;
            parseKeyValue(line, result);
        }
        if (result.containsKey("AudioFilename") && result.get("AudioFilename") != null) {
            obj.audio = result.get("AudioFilename");
        }
        if (result.containsKey("Mode") && result.get("Mode") != null) {
            obj.mode = Integer.parseInt(result.get("Mode"));
        }
        if (result.containsKey("BeatmapID") && result.get("BeatmapID") != null) {
            obj.bid = Long.parseLong(result.get("BeatmapID"));
        }
        if (result.containsKey("BeatmapSetID") && result.get("BeatmapSetID") != null) {
            obj.sid = Long.parseLong(result.get("BeatmapSetID"));
        }
    }

    private static String parseBackground(BufferedReader read) throws IOException {
        String line;
        while ((line = read.readLine()) != null) {
            if (line.startsWith("0,0,\"")) {
                int end = line.lastIndexOf('"');
                return line.substring(6, end);
            }
            if (line.startsWith("[")) break;
        }
        return null;
    }

    private static void parseKeyValue(String line, Map<String, String> map) {
        var entity = line.split(":");
        if (entity.length != 2) {
            return;
        }
        var key = entity[0].trim();
        var val = entity[1].trim();
        if (map.containsKey(key)) {
            map.put(key, val);
        }
    }
}
