package l.f.mappool.entity.osu;


import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import l.f.mappool.enums.ReankStatus;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;


@Entity
@Getter
@Setter
@DynamicUpdate
@Table(name = "beatmap")
@JsonIgnoreProperties(ignoreUnknown = true, allowSetters = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BeatMap {
    @JsonProperty("id")
    @Id
    Long id;

    @JsonIgnore
    @Column(name = "beatmapset_id", insertable=false, updatable=false)
    String mapsetId;

    /***
     * maybe it is different from beatmapset.mapperId
     */
    @JsonProperty("user_id")
    @Column(name = "mapperID")
    Long mapperId;

    @JsonProperty("version")
    @Column(name = "version", columnDefinition = "text")
//    @Convert(converter = TextType.class)
    String version;

    @JsonProperty("ranked")
    @Column(name = "status")
    Integer status;

    @JsonProperty("difficulty_rating")
    @Column(name = "difficulty")
    Float difficulty;

    @JsonProperty("mode_int")
    @Column(name = "mode")
    Short modeInt;

    @JsonProperty("ar")
    Float ar;
    @JsonProperty("cs")
    Float cs;
    @JsonProperty("od")
    @JsonAlias("accuracy")
    Float od;
    @JsonProperty("hp")
    @JsonAlias("drain")
    Float hp;

    @JsonProperty("hit_length")
    Integer length;

    @JsonProperty("total_length")
    @Column(name = "total_length")
    Integer totalLength;

    @JsonProperty("count_circles")
    Integer circles;
    @JsonProperty("count_sliders")
    Integer sliders;
    @JsonProperty("count_spinners")
    Integer spinners;

    @Transient
    String checksum;

    @ManyToOne()
    @JsonProperty("beatmapset")
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id"
    )
    @JoinColumn(name = "beatmapset_id")
    BeatMapSet beatMapSet;

    @JsonGetter("beatmapset_id")
    public Long getMapsetId() {
        if (mapsetId == null) {
            return beatMapSet.getId();
        }
        return Long.parseLong(mapsetId);
    }

    @JsonGetter("status")
    public String getStatus() {
        return ReankStatus.fromInteger(this.status).name();
    }

    @JsonSetter("status")
    public void setStatus(String status) {
        this.status = ReankStatus.valueOf(status).getStatusInt();
    }

    @JsonGetter("mode")
    public String getMode() {
        return switch (this.modeInt) {
            case 0 -> "osu";
            case 1 -> "taiko";
            case 2 -> "fruits";
            case 3 -> "mania";
            default -> "unknown";
        };
    }

    @JsonSetter("checksum")
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
}
