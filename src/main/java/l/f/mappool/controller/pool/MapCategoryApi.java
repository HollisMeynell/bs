package l.f.mappool.controller.pool;

import l.f.mappool.controller.PoolApi;
import l.f.mappool.dto.map.MapPoolDto;
import l.f.mappool.dto.validator.mapPool.CreateCategory;
import l.f.mappool.entity.MapCategory;
import l.f.mappool.util.ContextUtil;
import l.f.mappool.vo.DataVo;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class MapCategoryApi extends PoolApi {

    @PutMapping("createCategory")
    DataVo<MapCategory> createCategory(@RequestBody @Validated(CreateCategory.class) MapPoolDto create) {
        var u = ContextUtil.getContextUser();
        var category = mapPoolService.createCategory(u.getOsuId(), create.getGroupId(), create.getName());
        return new DataVo<>("创建成功", category);
    }

}
