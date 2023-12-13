package l.f.mappool.entity.pool;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import l.f.mappool.enums.PoolStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

@Setter
@Getter
@ToString
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@DynamicUpdate
@Table(name = "pool")
public class Pool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Exclude
    Integer id;

    @Column(name = "info", columnDefinition = "text")
    String info;

    @Column(name = "name", columnDefinition = "text")
    String name;

    Integer mode = 0;
    /*
        @Type(IntArrayType.class)
        @Column(name = "category", columnDefinition = "integer[]")
        Integer[] category;

     */
    String banner;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    PoolStatus status = PoolStatus.OPEN;

    @ToString.Exclude
    @OneToMany(mappedBy = "pool", fetch = FetchType.EAGER)
    List<PoolUser> users;

    @ToString.Exclude
    @OneToMany(mappedBy = "pool", fetch = FetchType.EAGER)
    List<PoolCategoryGroup> groups;


}