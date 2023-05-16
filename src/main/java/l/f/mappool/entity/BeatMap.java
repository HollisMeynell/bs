package l.f.mappool.entity;


import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import l.f.mappool.enums.ReankStatus;
import org.hibernate.annotations.DynamicUpdate;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@DynamicUpdate
@Entity
@Table(name = "beatmap")
public class BeatMap {
    @JsonProperty("id")
    @Id
    Long id;

    @JsonProperty("beatmapset_id")
    Long mapsetId;

    /***
     * maybe it is different from beatmapset.mapperId
     */
    @JsonProperty("user_id")
    @Column(name = "mapperID")
    Long mapperId;

    @JsonProperty("version")
    @Column(name = "version",columnDefinition = "text")
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

    @ManyToOne()
    @JsonManagedReference
    @JsonProperty("beatmapset")
    @JoinColumn(name = "beatmapset_id_set")
    BeatMapSet beatMapSet;

    public BeatMapSet getBeatMapSet() {
        return beatMapSet;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMapsetId() {
        return mapsetId;
    }

    public void setMapsetId(Long mapsetId) {
        this.mapsetId = mapsetId;
    }

    public Long getMapperId() {
        return mapperId;
    }

    public void setMapperId(Long mapperId) {
        this.mapperId = mapperId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @JsonGetter("status")
    public String getStatus() {
        return ReankStatus.fromInteger(this.status).name();
    }

    @JsonSetter("status")
    public void setStatus(Integer status) {
        this.status = status;
    }

    public Float getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Float difficulty) {
        this.difficulty = difficulty;
    }

    public Short getModeInt() {
        return modeInt;
    }

    public String getMode() {
        return switch (this.modeInt){
            case 0 -> "osu";
            default -> "?";
        };
    }

    public void setModeInt(Short modeInt) {
        this.modeInt = modeInt;
    }

    public Float getAr() {
        return ar;
    }

    public void setAr(Float ar) {
        this.ar = ar;
    }

    public Float getCs() {
        return cs;
    }

    public void setCs(Float cs) {
        this.cs = cs;
    }

    public Float getOd() {
        return od;
    }

    public void setOd(Float od) {
        this.od = od;
    }

    public Float getHp() {
        return hp;
    }

    public void setHp(Float hp) {
        this.hp = hp;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(Integer totalLength) {
        this.totalLength = totalLength;
    }

    public Integer getCircles() {
        return circles;
    }

    public void setCircles(Integer circles) {
        this.circles = circles;
    }

    public Integer getSliders() {
        return sliders;
    }

    public void setSliders(Integer sliders) {
        this.sliders = sliders;
    }

    public Integer getSpinners() {
        return spinners;
    }

    public void setSpinners(Integer spinners) {
        this.spinners = spinners;
    }
}
