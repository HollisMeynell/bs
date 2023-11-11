package l.f.mappool.entity.osu;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MatchesSearch {
    List<MatchMetaInfo> matches;
    Params params;
    @JsonProperty("cursor_string")
    String cursorString;

    public record Params(Integer limit, String sort) {
    }
}
