package l.f.mappool.controller.pool;

import l.f.mappool.controller.PoolApi;
import l.f.mappool.dto.map.CategoryItemDto;
import l.f.mappool.entity.MapCategoryItem;
import l.f.mappool.util.ContextUtil;
import l.f.mappool.vo.DataVo;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class MapCategoryItemApi extends PoolApi {

    @PutMapping("createCategoryItem")
    DataVo<MapCategoryItem> createItem(@RequestBody @Validated() CategoryItemDto create) {
        var u = ContextUtil.getContextUser();
        var group = mapPoolService.createCategoryItem(u.getOsuId(), create.getCategoryId(), create.getBeatmapId(), create.getInfo());
        return new DataVo<>("创建成功", group);
    }
}
