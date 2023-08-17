package l.f.mappool.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

/***
 * 类别, NM1/NM2 ...
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@Getter
@Setter
@DynamicUpdate
@Table(name = "pool_category")
public class MapCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne()
    @JoinColumn(name = "group_id")
    MapCategoryGroup group;

    @Column(name = "name", columnDefinition = "text")
    String name;

    /**
     * 未选择敲定是NULL,已经确定就是对应的 bid
     */
    Long chosed;

    @JsonIgnoreProperties({"category"})
    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER)
    List<MapCategoryItem> items;

    public Integer getGroupId() {
        return group.getId();
    }

    public void setGroupId(Integer groupId) {
        var g = new MapCategoryGroup();
        g.setId(groupId);
        this.group = g;
    }
}
