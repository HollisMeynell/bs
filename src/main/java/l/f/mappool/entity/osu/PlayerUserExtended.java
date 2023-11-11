package l.f.mappool.entity.osu;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public class PlayerUserExtended {
    /**
     * url of profile cover. Deprecated, use cover.url instead.
     */
    @JsonProperty("cover_url")
    String coverUrl;

    /**
     * nullable
     */
    @JsonProperty("discord")
    String discord;

    /**
     * whether or not ever being a supporter in the past
     */
    @JsonProperty("has_supported")
    Boolean hasSupported;

    /**
     * nullable
     */
    @JsonProperty("interests")
    String interests;

    /**
     *
     */
    @JsonProperty("join_date")
    OffsetDateTime joinDate;

    /**
     * nullable
     */
    @JsonProperty("location")
    String location;

    /**
     * maximum number of users allowed to be blocked
     */
    @JsonProperty("max_blocks")
    Integer maxBlocks;

    /**
     * maximum number of friends allowed to be added
     */
    @JsonProperty("max_friends")
    Integer maxFriends;

    /**
     * nullable
     */
    @JsonProperty("occupation")
    String occupation;

    /**
     *
     */
    @JsonProperty("playmode")
    String playmode;

    /**
     * Device choices of the user.
     */
    @JsonProperty("playstyle")
    String[] playstyle;

    /**
     * Number of forum posts
     */
    @JsonProperty("post_count")
    Integer postCount;

    /**
     * ordered array of sections in user profile page
     */
    @JsonProperty("profile_order")
    String[] profileOrder;

    /**
     * user-specific title
     */
    @JsonProperty("title")
    String title;

    /**
     *
     */
    @JsonProperty("title_url")
    String titleUrl;

    /**
     *
     */
    @JsonProperty("twitter")
    String twitter;

    /**
     *
     */
    @JsonProperty("website")
    String website;

}
