package l.f.mappool.entity.pool;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/***
 * 回复表
 */
@Setter
@Getter
@Embeddable
public class FeedbackId implements Serializable {
    @Column(name = "beatmap_id")
    Integer mapId;
    @Column(name = "creater_id")
    Integer createrId;

}
