package l.f.mappool.entity.pool;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 组别
 */
@Getter
@Setter
@Entity
@Table(name = "pool_category_group")
public class PoolCategoryGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne()
    @JoinColumn(name = "pool_id")
    @JsonIgnoreProperties({"groups", "users"})
    Pool pool;
    @Column(name = "name", columnDefinition = "text")
    String name;
    Integer color;
    @Column(name = "info", columnDefinition = "text")
    String info;

    @JsonIgnoreProperties({"group"})
    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER)
    List<PoolCategory> categories;
    int sort = 0;

    public Integer getPoolId() {
        if (pool == null) return null;
        return pool.getId();
    }

    public void setPoolId(Integer poolId) {
        var p = new Pool();
        p.setId(poolId);
        this.pool = p;
    }
}