package l.f.mappool.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "pool_category_group")
public class MapCategoryGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne()
    @JoinColumn(name = "pool_id")
    MapPool pool;
    @Column(name = "name", columnDefinition = "text")
    String name;
    Integer color;
    @Column(name = "info", columnDefinition = "text")
    String info;

    @JsonIgnoreProperties({"group"})
    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER)
    List<MapCategory> categories;
    int sort = 0;

    public Integer getPoolId() {
        return pool.getId();
    }

    public void setPoolId(Integer poolId) {
        var p = new MapPool();
        p.setId(poolId);
        this.pool = p;
    }
}