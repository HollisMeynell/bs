package l.f.mappool.dto.osu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OsuUserStatistics {

    @JsonProperty("count_100")
    Integer count100;

    @JsonProperty("count_300")
    Integer count300;

    @JsonProperty("count_50")
    Integer count50;

    @JsonProperty("count_miss")
    Integer countMiss;

    @JsonProperty("country_rank")
    Integer countryRank;

    @JsonProperty("grade_counts")
    GradeCounts gradeCounts;

    @JsonProperty("hit_accuracy")
    Float hitAccuracy;

    @JsonProperty("is_ranked")
    Boolean isRanked;

    @JsonProperty("level")
    Level level;

    @JsonProperty("maximum_combo")
    Integer maxCombo;

    @JsonProperty("play_count")
    Integer playCount;

    @JsonProperty("play_time")
    Integer playTime;

    @JsonProperty("pp")
    Float pp;

    // Experimental (lazer) performance points
    @JsonProperty("pp_exp")
    Float ppExp;

    @JsonProperty("global_rank")
    Integer globalRank;

    // Current rank according to experimental (lazer) pp.
    @JsonProperty("global_rank_exp")
    Integer globalRankExp;

    @JsonProperty("ranked_score")
    Long rankedScore;

    @JsonProperty("replays_watched_by_others")
    Integer replaysWatchedByOthers;

    @JsonProperty("total_hits")
    Long totalHits;

    @JsonProperty("total_score")
    Long totalScore;


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GradeCounts {
        @JsonProperty("a")
        Integer a;

        @JsonProperty("s")
        Integer s;

        @JsonProperty("sh")
        Integer sh;

        @JsonProperty("ss")
        Integer ss;

        @JsonProperty("ssh")
        Integer ssh;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Level {
        @JsonProperty("current")
        Integer current;

        @JsonProperty("progress")
        Float progress;
    }
}
