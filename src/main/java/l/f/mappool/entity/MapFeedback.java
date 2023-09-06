package l.f.mappool.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 评论表
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@Getter
@Setter
@DynamicUpdate
@Table(name = "pool_feedback")
public class MapFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "creater_id")
    Long createrId;

    @ManyToOne()
    @JsonIgnore
    @JoinColumn(name = "category_item_id")
    MapCategoryItem item;

    @Column(name = "deleted", columnDefinition = "boolean default false")
    boolean handle;

    @Column(name = "feedback", columnDefinition = "text")
    String feedback;

    Boolean agree;

    @SuppressWarnings("unused")
    public int getItemId() {
        return item.id;
    }

    public void setItemId(int itemId) {
        var i = new MapCategoryItem();
        i.setId(itemId);
        this.item = i;
    }
}

