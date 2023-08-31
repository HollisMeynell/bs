package l.f.mappool.controller;

import jakarta.annotation.Resource;
import l.f.mappool.dto.map.QueryMapPoolDto;
import l.f.mappool.entity.BeatMap;
import l.f.mappool.service.MapPoolService;
import l.f.mappool.service.OsuGetService;
import l.f.mappool.util.ContextUtil;
import l.f.mappool.vo.DataListVo;
import l.f.mappool.vo.DataVo;
import l.f.mappool.vo.FavoritesLiteVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@CrossOrigin
@ResponseBody
@RequestMapping(value = "/api/map", produces = "application/json;charset=UTF-8")
public class MapApi {
    @Resource
    protected OsuGetService osuService;
    @Resource
    protected MapPoolService mapPoolService;


    @GetMapping("/getMapInfo")
    DataListVo<FavoritesLiteVo> getMapInfo(@Validated @Nullable QueryMapPoolDto m) {
        var user = ContextUtil.getContextUser();
        return mapPoolService.getMapInfo();
    }
    @GetMapping("/favorite")
    DataListVo<FavoritesLiteVo> getFavorite() {
        var user = ContextUtil.getContextUser();
        return mapPoolService.getMapInfo();
    }

    @GetMapping("/getBeatMapInfo/{bid}")
    DataVo<BeatMap> getBeatmap(@PathVariable("bid") long bid) {
        return new DataVo<>(osuService.getMapInfoByDB(bid));
    }
}
