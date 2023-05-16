package l.f.mappool.controller;

import jakarta.annotation.Resource;
import l.f.mappool.service.FileService;
import l.f.mappool.dto.map.QueryMapPoolDto;
import l.f.mappool.entity.BeatMap;
import l.f.mappool.service.MapPoolService;
import l.f.mappool.service.OsuApiService;
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
@SuppressWarnings("unused")
@RequestMapping(value = "/api/map", produces = "application/json;charset=UTF-8")
public class MapApi {
    @Resource
    protected OsuApiService osuService;
    @Resource
    protected MapPoolService mapPoolService;
    @Resource
    protected FileService fileService;


    @GetMapping("/getMapInfo")
    public DataListVo<FavoritesLiteVo> getMapInfo(@Validated @Nullable QueryMapPoolDto m) {
        var user = ContextUtil.getContextUser();
        // TODO
        return mapPoolService.getMapInfo();
    }
    @GetMapping("/favorite")
    public DataListVo<FavoritesLiteVo> getFavorite() {
        var user = ContextUtil.getContextUser();
        // TODO
        return mapPoolService.getMapInfo();
    }

    @GetMapping("/getBeatMapInfo/{bid}")
    public DataVo<BeatMap> getBeatmap(@PathVariable("bid") long bid) {
        return new DataVo<>(osuService.getMapInfoByDB(bid));
    }
}
