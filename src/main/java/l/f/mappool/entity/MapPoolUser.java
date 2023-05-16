package l.f.mappool.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import l.f.mappool.enums.PoolPermission;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@DynamicUpdate
@Getter
@Setter
@Table(name = "pool_user" , indexes = {
        @Index(name = "upid", columnList = "user_id")
})
public class MapPoolUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "pool_id")
    @JsonIgnoreProperties("users")
    MapPool pool;

    /**
     * OSU id
     */
    @Column(name = "user_id")
    Long userId;

    @Enumerated(EnumType.STRING)
    PoolPermission permission;

    public void setPoolId(Integer poolId) {
        var p = new MapPool();
        p.setId(poolId);
        this.pool = p;
    }
}
