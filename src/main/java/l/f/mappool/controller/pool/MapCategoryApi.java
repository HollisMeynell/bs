package l.f.mappool.controller.pool;

import l.f.mappool.controller.PoolApi;
import l.f.mappool.dto.map.ChoseCategory;
import l.f.mappool.dto.map.MapPoolDto;
import l.f.mappool.dto.validator.mapPool.CreateCategory;
import l.f.mappool.dto.validator.mapPool.DeleteCategory;
import l.f.mappool.dto.validator.mapPool.SetCategory;
import l.f.mappool.entity.pool.PoolCategory;
import l.f.mappool.util.ContextUtil;
import l.f.mappool.vo.DataVo;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class MapCategoryApi extends PoolApi {

    @PutMapping("category")
    DataVo<PoolCategory> createCategory(@RequestBody @Validated(CreateCategory.class) MapPoolDto create) {
        var u = ContextUtil.getContextUser();
        var category = mapPoolService.createCategory(u.getOsuId(), create.getGroupId(), create.getName());
        return new DataVo<>("创建成功", category);
    }

    @PatchMapping("category")
    DataVo<PoolCategory> setCategory(@RequestBody @Validated(SetCategory.class) MapPoolDto create) {
        var u = ContextUtil.getContextUser();
        var category = mapPoolService.updateCategory(u.getOsuId(), create.getCategoryId(), create.getName());
        return new DataVo<>("修改成功", category);
    }

    @DeleteMapping("category")
    DataVo<String> getCategory(@Validated(DeleteCategory.class) MapPoolDto create) {
        var u = ContextUtil.getContextUser();
        mapPoolService.deleteCategory(u.getOsuId(), create.getCategoryId());
        return new DataVo<>("删除成功", null);
    }

    @PatchMapping("category/chose")
    DataVo<PoolCategory> choseMap(@RequestBody @Validated ChoseCategory choseCategory){
        var u = ContextUtil.getContextUser();
        var category = mapPoolService.choseCategory(u.getOsuId(), choseCategory.getCategoryId(), choseCategory.getBid());
        return new DataVo<>("修改成功", category);
    }
}
