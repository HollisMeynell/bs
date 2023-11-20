package l.f.mappool.entity.osu;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;
//import org.hibernate.type.TextType;

//import javax.persistence.Convert;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Getter
@Setter
@DynamicUpdate
@Table(name = "beatmapset")
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
    @JsonIgnoreProperties({"beatmapset"})
    @OneToMany(mappedBy = "beatMapSet",cascade = {CascadeType.REFRESH}, orphanRemoval = true)
    List<BeatMap> beatMaps;
}
