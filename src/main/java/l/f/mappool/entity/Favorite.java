package l.f.mappool.entity;

import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

@Entity
@Accessors(chain = true)
@DynamicUpdate
@Table(name = "favorite", indexes = {
        @Index(name = "obid", columnList = "user_id,bid")
})
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "user_id")
    Long userId;

    Long bid;

    @Column(name = "note",columnDefinition = "text")
    String info;

    LocalDateTime created;

    @Type(StringArrayType.class)
    @Column(name = "tags",columnDefinition = "text[]")
    String[] tags;

    public Favorite(){
        created = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBid() {
        return bid;
    }

    public void setBid(Long bid) {
        this.bid = bid;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }
}
