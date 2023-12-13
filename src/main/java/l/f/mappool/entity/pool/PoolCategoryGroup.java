package l.f.mappool.entity.pool;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id"
    )
    Pool pool;
    @Column(name = "name", columnDefinition = "text")
    String name;
    Integer color;
    @Column(name = "mod_optional")
    Integer modsOptional = 0;
    @Column(name = "mod_required")
    Integer modsRequired = 0;
    @Column(name = "info", columnDefinition = "text")
    String info;

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