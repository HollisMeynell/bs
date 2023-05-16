package l.f.mappool.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "pool_category_group")
public class MapCategoryGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne()
    @JsonManagedReference
    @JoinColumn(name = "pool_id")
    MapPool pool;
    @Column(name = "name", columnDefinition = "text")
    String name;
    Integer color;
    @Column(name = "info", columnDefinition = "text")
    String info;

    @JsonBackReference
    @JsonIgnore
    @OneToMany(mappedBy = "group")
    List<MapCategory> categories;
    int sort = 0;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public MapPool getPool() {
        return pool;
    }

    public void setPool(MapPool pool) {
        this.pool = pool;
    }

    public List<MapCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<MapCategory> categories) {
        this.categories = categories;
    }
}