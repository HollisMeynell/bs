package l.f.mappool.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.DynamicUpdate;
//import org.hibernate.type.TextType;

//import javax.persistence.Convert;



@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@DynamicUpdate
@Table(name = "beatmapset")
public class BeatMapSet {
    @JsonProperty("id")
    @Id
    Long id;

    @JsonProperty("user_id")
    @Column(name = "mapper_id")
    Long mapperId;

    @JsonProperty("creator")
    @Column(name = "mapper_name",columnDefinition = "text")
//    @Convert(converter = TextType.class)
    String mapperName;

    @JsonProperty("artist")
    @Column(name = "artist",columnDefinition = "text")
//    @Convert(converter = TextType.class)
    String artist;

    @JsonProperty("artist_unicode")
    @Column(name = "artist_unicode",columnDefinition = "text")
//    @Convert(converter = TextType.class)
    String artistUTF8;

    @JsonProperty("title")
    @Column(name = "title",columnDefinition = "text")
//    @Convert(converter = TextType.class)
    String title;

    @JsonProperty("title_unicode")
    @Column(name = "title_unicode",columnDefinition = "text")
//    @Convert(converter = TextType.class)
    String titleUTF8;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMapperId() {
        return mapperId;
    }

    public void setMapperId(Long mapperId) {
        this.mapperId = mapperId;
    }

    public String getMapperName() {
        return mapperName;
    }

    public void setMapperName(String mapperName) {
        this.mapperName = mapperName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtistUTF8() {
        return artistUTF8;
    }

    public void setArtistUTF8(String artistUTF8) {
        this.artistUTF8 = artistUTF8;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleUTF8() {
        return titleUTF8;
    }

    public void setTitleUTF8(String titleUTF8) {
        this.titleUTF8 = titleUTF8;
    }
}
