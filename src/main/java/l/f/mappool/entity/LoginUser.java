package l.f.mappool.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@JsonIgnoreProperties(ignoreUnknown = true, allowSetters = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@Getter
@Setter
@DynamicUpdate
@Table(name = "login_user", indexes = {
        @Index(name = "ouid", columnList = "osu_id, code")
})
public class LoginUser {
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

    @Transient
    boolean admin = false;
}
