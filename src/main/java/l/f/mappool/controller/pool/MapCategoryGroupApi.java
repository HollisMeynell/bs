package l.f.mappool.controller.pool;

import l.f.mappool.controller.PoolApi;
import l.f.mappool.dto.map.MapPoolDto;
import l.f.mappool.dto.validator.mapPool.CreateCategoryGroup;
import l.f.mappool.dto.validator.mapPool.DeleteCategoryGroup;
import l.f.mappool.dto.validator.mapPool.SetCategoryGroup;
import l.f.mappool.entity.pool.PoolCategoryGroup;
import l.f.mappool.util.ContextUtil;
import l.f.mappool.vo.DataListVo;
import l.f.mappool.vo.DataVo;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class MapCategoryGroupApi extends PoolApi {
    /**
     * 通过 poolId 获取组别的详细信息
     *
     * @param id poolId
     * @return 组别信息
     */
    @GetMapping("categoryGroupByPool")
    DataListVo<PoolCategoryGroup> getCategoryGroup(@RequestParam int id) {
        var list = mapPoolService.getCategoryGroup(id);
        return new DataListVo<PoolCategoryGroup>().setData(list).setTotalItems(list.size());
    }

    @PutMapping("categoryGroup")
    DataVo<PoolCategoryGroup> createCategoryGroup(@RequestBody @Validated(CreateCategoryGroup.class) MapPoolDto create) {
        var u = ContextUtil.getContextUser();
        var group = mapPoolService.createCategoryGroup(
                u.getOsuId(),
                create.getPoolId(),
                create.getName(),
                create.getInfo(),
                create.getColor(),
                Optional.ofNullable(create.getModRequired()),
                Optional.ofNullable(create.getModOptional())
        );
        return new DataVo<>("创建成功", group);
    }

    @PatchMapping("categoryGroup")
    DataVo<PoolCategoryGroup> setCategoryGroup(@RequestBody @Validated(SetCategoryGroup.class) MapPoolDto group) {
        var u = ContextUtil.getContextUser();
        var categoryGroup = mapPoolService.updateCategoryGroup(
                u.getOsuId(),
                group.getGroupId(),
                group.getName(),
                group.getInfo(),
                group.getColor(),
                group.getSort(),
                Optional.ofNullable(group.getModRequired()),
                Optional.ofNullable(group.getModOptional())
        );
        return new DataVo<>("修改成功", categoryGroup);
    }

    @DeleteMapping("categoryGroup")
    DataVo<String> deleteCategoryGroup(@Validated(DeleteCategoryGroup.class) MapPoolDto group) {
        var u = ContextUtil.getContextUser();
        mapPoolService.deleteCategoryGroup(u.getOsuId(), group.getGroupId());
        return new DataVo<>("删除成功",null);
    }

}
