package l.f.mappool.controller;

import jakarta.annotation.Resource;
import l.f.mappool.service.MapPoolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@ResponseBody
@SuppressWarnings("unused")
@RequestMapping(value = "/api/pool", produces = "application/json;charset=UTF-8")
public class PoolApi {
    @Resource
    protected MapPoolService mapPoolService;
}
