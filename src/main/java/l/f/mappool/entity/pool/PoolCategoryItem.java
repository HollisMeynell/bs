package l.f.mappool.entity.pool;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Getter
@Setter
@Entity
@DynamicUpdate
@Table(name = "pool_category_item")
public class PoolCategoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne()
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id"
    )
    @JoinColumn(name = "category_id")
    PoolCategory category;

    /***
     * 推荐者 id
     */
    @Column(name = "creater_id")
    Long createrId;

    @Column(name = "info", columnDefinition = "text")
    String info;

    Integer sort;

    /**
     * 选择的图
     */
    Long chous;

    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id"
    )
    @OneToMany(mappedBy = "item", fetch = FetchType.EAGER)
    List<PoolFeedback> feedbacks;

    public Integer getCategoryId() {
        return category.id;
    }

    public void setCategoryId(Integer categoryId) {
        var g = new PoolCategory();
        g.setId(categoryId);
        this.category = g;
    }
}
