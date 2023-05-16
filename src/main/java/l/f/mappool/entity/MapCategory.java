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
    @JsonIgnoreProperties({"categories"})
    MapCategoryGroup group;

    @Column(name = "name", columnDefinition = "text")
    String name;

    /**
     * 未选择敲定是NULL,已经确定就是对应的 bid
     */
    Long chosed;

    /**
     * 防止加载到评论, 评论需要单独加载, 用于排除隐藏评论
     */
    @JsonIgnoreProperties({"category", "feedbacks"})
    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER)
    List<MapCategoryItem> items;

    public Integer getGroupId() {
        if (group == null) return null;
        return group.getId();
    }

    public void setGroupId(Integer groupId) {
        var g = new MapCategoryGroup();
        g.setId(groupId);
        this.group = g;
    }
}
