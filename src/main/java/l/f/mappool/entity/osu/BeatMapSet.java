package l.f.mappool.entity.osu;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import l.f.mappool.util.JsonUtil;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.util.CollectionUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
//import org.hibernate.type.TextType;
//import javax.persistence.Convert;


@Entity
@Getter
@Setter
@DynamicUpdate
@Table(name = "beatmapset")
@JsonIgnoreProperties(ignoreUnknown = true, allowSetters = true)
public class BeatMapSet {
    @JsonProperty("id")
    @Id
    Long id;

    @JsonProperty("user_id")
    @Column(name = "mapper_id")
    Long mapperId;

    @JsonProperty("ranked")
    @Column(name = "status_int")
    Integer status;

    @JsonProperty("creator")
    @Column(name = "mapper_name", columnDefinition = "text")
    String mapperName;

    @JsonProperty("bpm")
    @Column(name = "bpm")
    Float bpm;

    @JsonProperty("artist")
    @Column(name = "artist", columnDefinition = "text")
    String artist;

    @JsonProperty("artist_unicode")
    @Column(name = "artist_unicode", columnDefinition = "text")
    String artistUTF8;

    @JsonProperty("title")
    @Column(name = "title", columnDefinition = "text")
    String title;

    @JsonProperty("title_unicode")
    @Column(name = "title_unicode", columnDefinition = "text")
    String titleUTF8;

    @JsonIgnore
    @OneToMany(mappedBy = "beatMapSet",cascade = {CascadeType.REFRESH}, orphanRemoval = true)
    List<BeatMap> beatMaps;

    @JsonSetter("beatmaps")
    public void jsonSetBeatMaps(List<JsonNode> beatMaps) {
        if (CollectionUtils.isEmpty(beatMaps)) return;
        this.beatMaps = beatMaps.stream().map(m -> JsonUtil.parseObject(m, BeatMap.class)).collect(Collectors.toList());
    }

    @JsonGetter("beatmaps")
    public List<BeatMap> jsonGetBeatMaps(List<BeatMap> beatMaps) {
        beatMaps.forEach(m -> m.setBeatMapSet(null));
        return beatMaps;
    }

    @JsonProperty("last_updated")
    @Column(name = "last_updated", columnDefinition = "TIMESTAMP")
    OffsetDateTime lastUpdated;
}
