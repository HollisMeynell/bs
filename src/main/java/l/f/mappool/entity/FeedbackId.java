package l.f.mappool.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

/***
 * 回复表
 */
@Embeddable
public class FeedbackId implements Serializable {
    @Column(name = "beatmap_id")
    Integer mapId;
    @Column(name = "creater_id")
    Integer createrId;

    public Integer getMapId() {
        return mapId;
    }

    public void setMapId(Integer mapId) {
        this.mapId = mapId;
    }

    public Integer getCreaterId() {
        return createrId;
    }

    public void setCreaterId(Integer createrId) {
        this.createrId = createrId;
    }
}
