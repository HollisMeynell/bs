package l.f.mappool.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import l.f.mappool.enums.PoolPermission;
import org.hibernate.annotations.DynamicUpdate;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@DynamicUpdate
@Table(name = "pool_user" , indexes = {
        @Index(name = "upid", columnList = "user_id")
})
public class MapPoolUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "pool_id")
    @JsonManagedReference
    MapPool pool;

    /**
     * OSU id
     */
    @Column(name = "user_id")
    Long userId;

    @Enumerated(EnumType.STRING)
    PoolPermission permission;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPoolId() {
        return pool.getId();
    }

    public void setPoolId(Integer poolId) {
        var p = new MapPool();
        p.setId(poolId);
        this.pool = p;
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

    public MapPool getPool() {
        return pool;
    }

    public void setPool(MapPool pool) {
        this.pool = pool;
    }
}
