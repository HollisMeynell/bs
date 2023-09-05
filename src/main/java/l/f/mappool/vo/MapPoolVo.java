package l.f.mappool.vo;

import l.f.mappool.entity.MapPool;
import org.springframework.beans.BeanUtils;

import java.util.List;

@SuppressWarnings("unused")
public class MapPoolVo extends MapPool {
    public MapPoolVo(){};
    public MapPoolVo(MapPool p){
        BeanUtils.copyProperties(p, this, "users", "groups");
    };
    List<MapCategoryVo> categoryList;

}
