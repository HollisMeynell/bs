package l.f.mappool.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import l.f.mappool.entity.osu.BeatMap;
import l.f.mappool.entity.pool.Pool;
import l.f.mappool.entity.pool.PoolCategoryGroup;
import l.f.mappool.service.OsuApiService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("unused")
public class PoolVo extends Pool {
    public PoolVo(){}
    public PoolVo(Pool p) {
        BeanUtils.copyProperties(p, this, "users", "groups");
        if (CollectionUtils.isEmpty(p.getGroups())) return;
        categoryList = p.getGroups()
                .stream()
                .sorted(Comparator.comparing(PoolCategoryGroup::getSort))
                .map(CategoryGroupVo::new)
                .toList();
    }
    List<CategoryGroupVo> categoryList;
    List<BeatMap> mapinfo;

    public void parseMapInfo(OsuApiService apiService) {
        mapinfo = categoryList.stream()
                .flatMap(c -> c.getCategory().stream())
                .map(CategoryGroupVo.Category::bid)
                .map(apiService::getMapInfoByDB)
                .toList();
    }
}
