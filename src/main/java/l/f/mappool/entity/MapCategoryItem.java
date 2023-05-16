package l.f.mappool.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@DynamicUpdate
@Table(name = "pool_category_item")
public class MapCategoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne()
    @JsonManagedReference
    @JoinColumn(name = "category_id")
    MapCategory category;

    /***
     * 推荐者 id
     */
    @Column(name = "creater_id")
    Integer createrId;

    @Column(name = "info", columnDefinition = "text")
    String info;

    Integer sort;

    Long chous;

    @JsonBackReference
    @OneToMany(mappedBy = "item")
    List<MapFeedback> feedbacks;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategoryId() {
        return category.id;
    }

    public void setCategoryId(Integer categoryId) {
        var g = new MapCategory();
        g.setId(categoryId);
        this.category = g;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Long getChous() {
        return chous;
    }

    public void setChous(Long chous) {
        this.chous = chous;
    }

    public MapCategory getCategory() {
        return category;
    }

    public void setCategory(MapCategory category) {
        this.category = category;
    }

    public Integer getCreaterId() {
        return createrId;
    }

    public void setCreaterId(Integer createrId) {
        this.createrId = createrId;
    }
}
