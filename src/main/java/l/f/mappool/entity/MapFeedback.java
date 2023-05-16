package l.f.mappool.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;


@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@DynamicUpdate
@Table(name = "pool_feedback")
public class MapFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "creater_id")
    Long createrId;

    @ManyToOne()
    @JsonManagedReference
    @JoinColumn(name = "category_item_id")
    MapCategoryItem item;

    @Column(name = "deleted", columnDefinition = "boolean default false")
    boolean deleted;

    @Column(name = "feedback", columnDefinition = "text")
    String feedback;

    Boolean agree;


    public MapCategoryItem getItem() {
        return item;
    }

    public void setItem(MapCategoryItem item) {
        this.item = item;
    }

    public int getItemId() {
        return item.id;
    }

    public void setItemId(int itemId) {
        var i = new MapCategoryItem();
        i.setId(itemId);
        this.item = i;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Boolean getAgree() {
        return agree;
    }

    public void setAgree(Boolean agree) {
        this.agree = agree;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getCreaterId() {
        return createrId;
    }

    public void setCreaterId(Long createrId) {
        this.createrId = createrId;
    }
}

