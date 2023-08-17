package l.f.mappool.entity;

import com.fasterxml.jackson.annotation.*;
import l.f.mappool.enums.PoolStatus;
import jakarta.persistence.*;
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
public class MapPool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Exclude
    Integer id;

    @Column(name = "info", columnDefinition = "text")
    String info;

    @Column(name = "name", columnDefinition = "text")
    String name;
    /*
        @Type(IntArrayType.class)
        @Column(name = "category", columnDefinition = "integer[]")
        Integer[] category;

     */
    String banner;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    PoolStatus status = PoolStatus.OPEN;

    @JsonIgnoreProperties({"pool"})
    @ToString.Exclude
    @OneToMany(mappedBy = "pool", fetch = FetchType.EAGER)
    List<MapPoolUser> users;

    @JsonIgnoreProperties({"pool"})
    @ToString.Exclude
    @OneToMany(mappedBy = "pool", fetch = FetchType.EAGER)
    List<MapCategoryGroup> groups;


}