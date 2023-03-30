package l.f.mappool.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.persistence.*;
import l.f.mappool.enums.PoolPermission;
import org.hibernate.annotations.DynamicUpdate;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@DynamicUpdate
@Table(name = "pool_user" , indexes = {
        @Index(name = "upid", columnList = "pool_id,user_id")
})
public class MapPoolUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "pool_id")
    Integer poolId;

    @Column(name = "user_id")
    Long userId;

    PoolPermission permission;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPoolId() {
        return poolId;
    }

    public void setPoolId(Integer poolId) {
        this.poolId = poolId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public PoolPermission getPermission() {
        return permission;
    }

    public void setPermission(PoolPermission permission) {
        this.permission = permission;
    }
}
