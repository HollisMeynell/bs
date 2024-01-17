package l.f.mappool.entity.osu;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true, allowSetters = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MatchMetaInfo {
    Long id;

    @JsonProperty("start_time")
    OffsetDateTime startTime;

    @JsonProperty("end_time")
    OffsetDateTime endTime;

    String name;
}
