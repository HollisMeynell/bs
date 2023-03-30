package l.f.mappool.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import l.f.mappool.enums.PoolStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@DynamicUpdate
@Table(name = "pool")
public class MapPool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "status")
    PoolStatus status = PoolStatus.OPEN;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public PoolStatus getStatus() {
        return status;
    }

    public void setStatus(PoolStatus status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}