package l.f.mappool.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@DynamicUpdate
@Table(name = "pool_map_item")
public class MapItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "creater_id")
    Integer createrId;

    @Column(name = "category_item_id")
    Integer categoryItemId;

    @Column(name = "beatmap_id")
    Long mapId;

    @Column(name = "name", columnDefinition = "text")
    String name;

    @Column(name = "info", columnDefinition = "text")
    String info;
}
