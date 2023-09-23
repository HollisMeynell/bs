package l.f.mappool.controller;

import l.f.mappool.entity.Favorite;
import l.f.mappool.entity.User;
import l.f.mappool.service.FavoriteService;
import l.f.mappool.util.ContextUtil;
import l.f.mappool.vo.DataVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteApi(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }
    @GetMapping
    public DataVo<List<Favorite>> getFavorite() {
        User user = ContextUtil.getContextUser();
        return favoriteService.getAllFavorite(user);
    }
}
