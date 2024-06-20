package l.f.mappool.controller.admin;

import jakarta.annotation.Resource;
import l.f.mappool.config.interceptor.Open;
import l.f.mappool.service.MapPoolService;
import l.f.mappool.service.OsuApiService;
import l.f.mappool.service.OsuFileService;
import l.f.mappool.util.ContextUtil;
import l.f.mappool.vo.DataVo;
import l.f.mappool.vo.PoolVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@ResponseBody
@SuppressWarnings("unused")
@RequestMapping(value = "/api/admin", produces = "application/json;charset=UTF-8")
@Open(admin = true)
public class AdminApi {
    @Resource
    protected OsuApiService  osuService;
    @Resource
    protected MapPoolService mapPoolService;
    @Resource
    private OsuFileService   osuFileService;

    @GetMapping("countOsuFile")
    public DataVo<OsuFileService. BeatmapSetCount> countBeatMap() {
        var result = osuFileService.getCount();
        return new DataVo<>(result);
    }

    @GetMapping("queryOsuFile")


    @PutMapping("createPool")
    public PoolVo createForce(@RequestBody PoolVo pool) {
        var u = ContextUtil.getContextUser();
        return mapPoolService.exportPoolAdmin(u.getOsuId(), pool);
    }

    @DeleteMapping("deletePool")
    public DataVo<Void> deletePool(@RequestParam Integer poolId) {
        mapPoolService.deletePoolAdmin(poolId);
        return new DataVo<>();
    }
}
