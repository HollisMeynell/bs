package l.f.mappool.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

/***
 * 类别, NM1/NM2 ...
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@DynamicUpdate
@Table(name = "pool_category")
public class MapCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne()
    @JsonManagedReference
    @JoinColumn(name = "group_id")
    MapCategoryGroup group;

    @Column(name = "name", columnDefinition = "text")
    String name;

    /**
     * 未选择敲定是NULL,已经确定就是对应的 bid
     */
    Long chosed;

    @JsonBackReference
    @OneToMany(mappedBy = "category")
    List<MapCategoryItem> items;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGroupId() {
        return group.getId();
    }

    public void setGroupId(Integer groupId) {
        var g = new MapCategoryGroup();
        g.setId(groupId);
        this.group = g;
    }

    public MapCategoryGroup getGroup() {
        return group;
    }

    public void setGroup(MapCategoryGroup group) {
        this.group = group;
    }

    public Long getChosed() {
        return chosed;
    }

    public void setChosed(Long chosed) {
        this.chosed = chosed;
    }

    public List<MapCategoryItem> getItems() {
        return items;
    }

    public void setItems(List<MapCategoryItem> items) {
        this.items = items;
    }
}
