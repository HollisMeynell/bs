package l.f.mappool.controller;

import jakarta.annotation.Resource;
import l.f.mappool.service.MapPoolService;
import l.f.mappool.service.OsuGetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@CrossOrigin
@ResponseBody
@RequestMapping(value = "/api/pool", produces = "application/json;charset=UTF-8")
public class PoolApi {
    @Resource
    protected OsuGetService osuService;
    @Resource
    protected MapPoolService mapPoolService;
}
