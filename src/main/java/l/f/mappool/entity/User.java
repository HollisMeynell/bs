package l.f.mappool.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@DynamicUpdate
@Table(name = "o_user", indexes = {
        @Index(name = "ouid", columnList = "osu_id, code")
})
public class User {
    @Id
    @JsonProperty("id" )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "addr", columnDefinition = "text")
    String addr;
    @Column(name = "osu_id")
    Long osuId;

    @Column(name = "code", columnDefinition = "text")
    String code;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOsuId() {
        return osuId;
    }

    public void setOsuId(Long osuId) {
        this.osuId = osuId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
}
