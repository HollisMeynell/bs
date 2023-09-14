package l.f.mappool.controller;

import jakarta.annotation.Resource;
import l.f.mappool.entity.BeatMap;
import l.f.mappool.service.FileService;
import l.f.mappool.service.MapPoolService;
import l.f.mappool.service.OsuApiService;
import l.f.mappool.vo.DataVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
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

    @GetMapping("/getBeatMapInfo/{bid}")
    public DataVo<BeatMap> getBeatmap(@PathVariable("bid") long bid) {
        return new DataVo<>(osuService.getMapInfoByDB(bid));
    }
}
