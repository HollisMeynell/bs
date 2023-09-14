package l.f.mappool.vo;

import l.f.mappool.entity.BeatMap;
import l.f.mappool.entity.Favorite;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class FavoritesVo {
    private Long bid;
    private String info;
    private LocalDateTime createTime;
    private String[] tags;
    private BeatMap beatMap;

    public FavoritesVo(Favorite favorite) {
        this.bid = favorite.getBid();
        this.info = favorite.getInfo();
        this.createTime = favorite.getCreated();
        this.tags = favorite.getTags();
    }
}
