package l.f.mappool.controller.pool;

import l.f.mappool.controller.PoolApi;
import l.f.mappool.dto.map.CategoryItemDto;
import l.f.mappool.dto.validator.mapPool.DeleteCategoryItem;
import l.f.mappool.dto.validator.mapPool.SetCategoryItem;
import l.f.mappool.entity.MapCategoryItem;
import l.f.mappool.entity.MapFeedback;
import l.f.mappool.util.ContextUtil;
import l.f.mappool.vo.DataListVo;
import l.f.mappool.vo.DataVo;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@Controller
public class MapCategoryItemApi extends PoolApi {

    @PutMapping("categoryItem")
    DataVo<MapCategoryItem> createItem(@RequestBody @Validated(CategoryItemDto.class) CategoryItemDto create) {
        var u = ContextUtil.getContextUser();
        var item = mapPoolService.createCategoryItem(u.getOsuId(), create.getCategoryId(), create.getBeatmapId(), create.getInfo());
        return new DataVo<>("创建成功", item);
    }

    @PatchMapping("categoryItem")
    DataVo<MapCategoryItem> setItem(@RequestBody @Validated(SetCategoryItem.class) CategoryItemDto create) {
        var u = ContextUtil.getContextUser();
        var item = mapPoolService.updateCategoryItem(u.getOsuId(), create.getItemId(), create.getBeatmapId(), create.getInfo(), create.getSort());
        return new DataVo<>("修改成功", item);
    }

    @DeleteMapping("categoryItem")
    DataVo<String> deleteItem(@Validated(DeleteCategoryItem.class) CategoryItemDto create){
        var u = ContextUtil.getContextUser();
        mapPoolService.deleteCategoryItem(u.getOsuId(), create.getItemId());
        return new DataVo<>("删除成功", null);
    }

    @GetMapping("feedback")
    DataListVo<MapFeedback> getFeedback(@NotNull(message = "id 不能为空") @RequestParam("id") int itemId){
        var u = ContextUtil.getContextUser();
        var feedbacks = mapPoolService.getFeedbackFromItem(u.getOsuId(), itemId);
        return new DataListVo<MapFeedback>()
                .setTotalItems(feedbacks.size())
                .setPageSize(feedbacks.size())
                .setData(feedbacks);
    }
}
