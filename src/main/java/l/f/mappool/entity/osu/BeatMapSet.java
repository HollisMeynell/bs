package l.f.mappool.entity.osu;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;
//import org.hibernate.type.TextType;

//import javax.persistence.Convert;


@Entity
@Getter
@Setter
@DynamicUpdate
@Table(name = "beatmapset")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
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

    @JsonProperty("beatmaps")
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id"
    )
    @OneToMany(mappedBy = "beatMapSet",cascade = {CascadeType.REFRESH}, orphanRemoval = true)
    List<BeatMap> beatMaps;
//
//    @JsonSetter("beatmaps")
//    public void setBeatMaps(List<BeatMap> beatMaps) {
//        this.beatMaps = beatMaps;
//    }
}
