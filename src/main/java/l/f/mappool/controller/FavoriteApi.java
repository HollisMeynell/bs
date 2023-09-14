package l.f.mappool.controller;

import l.f.mappool.entity.BeatMap;
import l.f.mappool.vo.DataListVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@Controller
@ResponseBody
@RequestMapping("/api/favorite")
public class FavoriteApi {
    @GetMapping
    public DataListVo<BeatMap> getFavorite() {

        return new DataListVo<>();
    }
}
