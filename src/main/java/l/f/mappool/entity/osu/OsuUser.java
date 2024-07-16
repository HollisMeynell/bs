package l.f.mappool.entity.osu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OsuUser {

    Long id;

    @JsonProperty("avatar_url")
    String avatarUrl;

    @JsonProperty("country_code")
    String countryCode;

    @JsonProperty("default_group")
    String group;

    @JsonProperty("is_active")
    Boolean active;

    @JsonProperty("is_bot")
    Boolean isBot;

    @JsonProperty("is_deleted")
    Boolean isDeleted;

    @JsonProperty("is_online")
    Boolean isOnline;

    @JsonProperty("is_supporter")
    Boolean isSupporter;

    @JsonProperty("last_visit")
    OffsetDateTime lastTime;

    @JsonProperty("pm_friends_only")
    Boolean pmFriendsOnly;

    @JsonProperty("username")
    String userName;

    OsuUserStatistics statistics;
}
