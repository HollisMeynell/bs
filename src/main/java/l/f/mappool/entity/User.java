package l.f.mappool.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.DynamicUpdate;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@DynamicUpdate
@Table(name = "o_user")
public class User {
    @JsonProperty("id")
    @Id
    Long id;

    @JsonProperty("username")
    @Column(name = "name", columnDefinition = "text")
    String name;

    @JsonProperty("avatar_url")
    @Column(name = "avatar", columnDefinition = "text")
    String avatar;
}
