package l.f.mappool.controller;

import jakarta.annotation.Resource;
import l.f.mappool.config.interceptor.Open;
import l.f.mappool.dao.MapPoolDao;
import l.f.mappool.service.OsuGetService;
import l.f.mappool.vo.DataVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Open
@Controller
@ResponseBody
@RequestMapping(value = "/api/public", produces = "application/json;charset=UTF-8")
public class PublicApi {
    @Resource
    OsuGetService osuService;
    @Resource
    MapPoolDao mapPoolDao;

    @PostMapping("getAllPool")
    Object getAllPool() {
        return new DataVo(mapPoolDao.getPublicPool());
    }

    @GetMapping("getOauthUrl")
    DataVo<String> getOauthUrl(){
        return new DataVo<>(osuService.getOauthUrl("test"));
    }
}
