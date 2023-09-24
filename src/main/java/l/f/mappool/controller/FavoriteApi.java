package l.f.mappool.controller;

import l.f.mappool.dto.FavoriteDto;
import l.f.mappool.dto.validator.favorite.*;
import l.f.mappool.entity.Favorite;
import l.f.mappool.entity.User;
import l.f.mappool.exception.HttpError;
import l.f.mappool.service.FavoriteService;
import l.f.mappool.util.ContextUtil;
import l.f.mappool.vo.DataVo;
import l.f.mappool.vo.FavoritesVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("allTags")
    public DataVo<String[]> getFavoriteTags() {
        User user = ContextUtil.getContextUser();
        return favoriteService.getAllTags(user);
    }

    @GetMapping("byTags")
    public DataVo<List<Favorite>> getByTags(@Validated(GetByTags.class) FavoriteDto favorite) {
        User user = ContextUtil.getContextUser();
        return favoriteService.getByTags(user, favorite);
    }

    @PutMapping
    public FavoritesVo addFavorite(@RequestBody @Validated(CreateFavorite.class) FavoriteDto favorite) {
        User user = ContextUtil.getContextUser();
        return favoriteService.addFavorite(user, favorite);
    }

    @DeleteMapping
    public DataVo<?> deleteFavorite(@Validated(DeleteFavorite.class) FavoriteDto favorite) {
        User user = ContextUtil.getContextUser();
        return favoriteService.deleteFavorite(user, favorite);
    }

    @PatchMapping
    public FavoritesVo updateFavorite(@RequestBody @Validated(UpdateFavorite.class) FavoriteDto favorite) throws HttpError {
        User user = ContextUtil.getContextUser();
        return favoriteService.setFavorite(user, favorite);
    }

    @PutMapping("tag")
    public FavoritesVo addTag(@RequestBody @Validated(AddTag.class) FavoriteDto tag) {
        User user = ContextUtil.getContextUser();
        return favoriteService.addTag(user, tag);
    }

    @PatchMapping("tag")
    public FavoritesVo updateTag(@RequestBody @Validated(ReplaceTag.class) FavoriteDto tag) throws HttpError {
        User user = ContextUtil.getContextUser();
        return favoriteService.replaceTag(user, tag);
    }

    @DeleteMapping ("tag")
    public FavoritesVo deleteTag(@Validated(DeleteTag.class) FavoriteDto tag) {
        User user = ContextUtil.getContextUser();
        return favoriteService.deleteTag(user, tag);
    }
}
