package l.f.mappool.controller.pool;

import jakarta.annotation.Resource;
import l.f.mappool.controller.MapApi;
import l.f.mappool.controller.PoolApi;
import l.f.mappool.dao.MapPoolDao;
import l.f.mappool.dto.map.FeedbackDto;
import l.f.mappool.dto.validator.mapPool.CreateFeedback;
import l.f.mappool.dto.validator.mapPool.DeleteFeedback;
import l.f.mappool.dto.validator.mapPool.HandleFeedback;
import l.f.mappool.dto.validator.mapPool.SetFeedback;
import l.f.mappool.entity.MapFeedback;
import l.f.mappool.util.ContextUtil;
import l.f.mappool.vo.DataVo;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class MapFeedbackApi extends PoolApi {
    @Resource
    MapPoolDao mapPoolDao;

    @PutMapping("feedback")
    DataVo<MapFeedback> createFeedback(@RequestBody @Validated(CreateFeedback.class) FeedbackDto create) {
        var u = ContextUtil.getContextUser();
        var feedback = mapPoolDao.createFeedback(u.getOsuId(), create.getItemId(), create.getAgree(), create.getFeedback());
        return new DataVo<>("创建成功", feedback);
    }

    @PatchMapping("feedback")
    DataVo<MapFeedback> setFeedback(@RequestBody @Validated(SetFeedback.class) FeedbackDto create) {
        var u = ContextUtil.getContextUser();
        var feedback = mapPoolDao.updateFeedback(u.getOsuId(), create.getId(), create.getAgree(), create.getFeedback());
        return new DataVo<>("修改成功", feedback);
    }

    @DeleteMapping("feedback")
    DataVo<String> deleteFeedback(@Validated(DeleteFeedback.class) FeedbackDto create){
        var u = ContextUtil.getContextUser();
        mapPoolDao.deleteFeedback(u.getOsuId(), create.getItemId());
        return new DataVo<>("删除成功", null);
    }

    /**
     * 修改是否隐藏
     * @return value 为当前状态
     */
    @PatchMapping("feedback/handle")
    DataVo<Boolean> handleFeedback(@RequestBody @Validated(HandleFeedback.class) FeedbackDto create) {
        var u = ContextUtil.getContextUser();
        var feedback = mapPoolDao.handleFeedback(u.getOsuId(), create.getId(), create.getHandle());
        return new DataVo<>("修改状态成功", feedback);
    }
}
