package l.f.mappool.controller.pool;

import l.f.mappool.controller.PoolApi;
import l.f.mappool.dto.map.MapPoolDto;
import l.f.mappool.dto.validator.mapPool.CreateCategoryGroup;
import l.f.mappool.dto.validator.mapPool.SetCategoryGroup;
import l.f.mappool.entity.MapCategoryGroup;
import l.f.mappool.util.ContextUtil;
import l.f.mappool.vo.DataListVo;
import l.f.mappool.vo.DataVo;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MapCategoryGroupApi extends PoolApi {
    /**
     * 获取组别的详细信息
     *
     * @param id poolId
     * @return 组别信息
     */
    @GetMapping("getGroup")
    DataListVo<MapCategoryGroup> getGroup(@RequestParam int id) {
        var list = mapPoolService.getCategoryGroup(id);
        return new DataListVo<MapCategoryGroup>().setData(list).setTotalItems(list.size());
    }

    @PutMapping("createCategoryGroup")
    DataVo<MapCategoryGroup> createCategoryGroup(@RequestBody @Validated(CreateCategoryGroup.class) MapPoolDto create) {
        var u = ContextUtil.getContextUser();
        var group = mapPoolService.createCategoryGroup(u.getOsuId(), create.getPoolId(), create.getName(), create.getInfo(), create.getColor());
        return new DataVo<>("创建成功", group);
    }

    @PutMapping("setCategoryGroup")
    DataVo<MapCategoryGroup> setCategoryGroup(@RequestBody @Validated(SetCategoryGroup.class) MapPoolDto group) {
        var u = ContextUtil.getContextUser();
        var categoryGroup = mapPoolService.updateCategoryGroup(u.getOsuId(), group.getGroupId(), group.getName(), group.getInfo(), group.getColor(), group.getSort());
        return new DataVo<>(categoryGroup);
    }
}
