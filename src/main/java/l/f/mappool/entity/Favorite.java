package l.f.mappool.entity;

import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

/**
 * 收藏谱面
 */
@Entity
@Getter
@Setter
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

    LocalDateTime created =  LocalDateTime.now();

    @Type(StringArrayType.class)
    @Column(name = "tags",columnDefinition = "text[]")
    String[] tags;
}
