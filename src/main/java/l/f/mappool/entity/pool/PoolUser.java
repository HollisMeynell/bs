package l.f.mappool.entity.pool;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import l.f.mappool.enums.PoolPermission;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate
@Getter
@Setter
@Table(name = "pool_user" , indexes = {
        @Index(name = "upid", columnList = "user_id")
})
public class PoolUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "pool_id")
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id"
    )
    Pool pool;

    /**
     * OSU id
     */
    @Column(name = "user_id")
    Long userId;

    @Enumerated(EnumType.STRING)
    PoolPermission permission;

    public void setPoolId(Integer poolId) {
        var p = new Pool();
        p.setId(poolId);
        this.pool = p;
    }
}
