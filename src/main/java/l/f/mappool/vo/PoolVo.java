package l.f.mappool.vo;

import l.f.mappool.entity.pool.Pool;
import org.springframework.beans.BeanUtils;

import java.util.List;

@SuppressWarnings("unused")
public class PoolVo extends Pool {
    public PoolVo(){}
    public PoolVo(Pool p){
        BeanUtils.copyProperties(p, this, "users", "groups");
    }
    List<MapCategoryVo> categoryList;

}
