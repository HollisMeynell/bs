package l.f.mappool.vo.yasunaori;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import l.f.mappool.controller.YasunaoriApi;
import l.f.mappool.entity.osu.BeatMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class YasunaoriBeatmapInfoVo {
    private String error;
    private Long id;
    private String title;
    private String titleUnicode;
    private String artist;
    private String artistUnicode;
    private String creator;
    private String coverUrl;
    private String status;
    private String mode;
    private Stats stats;
    private Difficulty difficulty;

    public YasunaoriBeatmapInfoVo(String error) {
        this.error = error;
    }

    public YasunaoriBeatmapInfoVo(BeatMap beatMap) {
        this.id = beatMap.getId();
        this.title = beatMap.getBeatMapSet().getTitle();
        this.titleUnicode = beatMap.getBeatMapSet().getTitleUTF8();
        this.artist = beatMap.getBeatMapSet().getArtist();
        this.artistUnicode = beatMap.getBeatMapSet().getArtistUTF8();
        this.creator = beatMap.getBeatMapSet().getMapperName();
        this.coverUrl = YasunaoriApi.BACKGROUND_URL_PREFIX + this.id;
        this.status = beatMap.getStatus();
        this.mode = beatMap.getMode();

            this.stats = new Stats(beatMap.getTotalLength(), beatMap.getBeatMapSet().getBpm(), beatMap.getCs(), beatMap.getAr(), beatMap.getOd(), beatMap.getHp());
        this.difficulty = new Difficulty(beatMap.getDifficulty(), beatMap.getVersion());
    }

    @Data
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Stats {
        private Integer length;
        private Float bpm;
        private Float cs;
        private Float ar;
        private Float od;
        private Float hp;
    }

    @Data
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Difficulty {
        private Float star;
        private String name;
    }
}
