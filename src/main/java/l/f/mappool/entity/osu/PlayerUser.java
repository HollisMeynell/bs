package l.f.mappool.entity.osu;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PlayerUser {
    /**
     * url of user's avatar
     */
    @JsonProperty("avatar_url")
    String avatarUrl;

    /**
     * two-letter code representing user's country
     */
    @JsonProperty("country_code")
    String countryCode;

    /**
     * Identifier of the default Group the user belongs to.
     * nullable
     */
    @JsonProperty("default_group")
    String defaultGroup;

    /**
     * unique identifier for user
     */
    @JsonProperty("id")
    Integer id;

    /**
     * has this account been active in the last x months?
     */
    @JsonProperty("is_active")
    Boolean isActive;

    /**
     * is this a bot account?
     */
    @JsonProperty("is_bot")
    Boolean isBot;

    /**
     *
     */
    @JsonProperty("is_deleted")
    Boolean isDeleted;

    /**
     * is the user currently online? (either on lazer or the new website)
     */
    @JsonProperty("is_online")
    Boolean isOnline;

    /**
     * does this user have supporter?
     */
    @JsonProperty("is_supporter")
    Boolean isSupporter;

    /**
     * last access time. null if the user hides online presence
     * nullable
     */
    @JsonProperty("last_visit")
    OffsetDateTime lastVisit;

    /**
     * whether or not the user allows PM from other than friends
     */
    @JsonProperty("pm_friends_only")
    Boolean pmFriendsOnly;

    /**
     * colour of username/profile highlight, hex code (e.g. #333333)
     * nullable
     */
    @JsonProperty("profile_colour")
    String profileColour;

    /**
     * user's display name
     */
    @JsonProperty("username")
    String username;

    /*****************************************Optional attributes**************************************************/
/*
    @JsonProperty("account_history")
    @JsonProperty("active_tournament_banner")
    @JsonProperty("badges")
    @JsonProperty("beatmap_playcounts_count")
    @JsonProperty("blocks")
    @JsonProperty("country")
    @JsonProperty("cover")
    @JsonProperty("favourite_beatmapset_count")
    @JsonProperty("follow_user_mapping")
    @JsonProperty("follower_count")
    @JsonProperty("friends")
    @JsonProperty("graveyard_beatmapset_count")
    @JsonProperty("groups")
    @JsonProperty("guest_beatmapset_count")
    @JsonProperty("is_restricted")
    @JsonProperty("kudosu")
    @JsonProperty("loved_beatmapset_count")
    @JsonProperty("mapping_follower_count")
    @JsonProperty("monthly_playcounts")
    @JsonProperty("page")
    @JsonProperty("pending_beatmapset_count")
    @JsonProperty("previous_usernames")
    @JsonProperty("rank_highest")
    @JsonProperty("rank_history")
    @JsonProperty("ranked_beatmapset_count")
    @JsonProperty("replays_watched_counts")
    @JsonProperty("scores_best_count")
    @JsonProperty("scores_first_count")
    @JsonProperty("scores_recent_count")
    @JsonProperty("statistics")
    @JsonProperty("statistics_rulesets")
    @JsonProperty("support_level")
    @JsonProperty("unread_pm_count")
    @JsonProperty("user_achievements")
    @JsonProperty("user_preferences")

 */
}
