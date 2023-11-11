package l.f.mappool.entity.osu;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Match {
    @JsonProperty("match")
    MatchMetaInfo match;

    List<Object> events;
    List<PlayerUser> users;

    @JsonProperty("first_event_id")
    Long firstEvent;
    @JsonProperty("latest_event_id")
    Long latestEvent;
    @JsonProperty("current_game_id")
    Long currentEvent;
}
