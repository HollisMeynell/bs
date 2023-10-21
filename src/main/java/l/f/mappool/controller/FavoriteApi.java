package l.f.mappool.controller;

import l.f.mappool.dto.FavoriteDto;
import l.f.mappool.dto.validator.favorite.*;
import l.f.mappool.entity.Favorite;
import l.f.mappool.entity.LoginUser;
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
        LoginUser loginUser = ContextUtil.getContextUser();
        return favoriteService.getAllFavorite(loginUser);
    }

    @GetMapping("allTags")
    public DataVo<String[]> getFavoriteTags() {
        LoginUser loginUser = ContextUtil.getContextUser();
        return favoriteService.getAllTags(loginUser);
    }

    @GetMapping("byTags")
    public DataVo<List<Favorite>> getByTags(@Validated(GetByTags.class) FavoriteDto favorite) {
        LoginUser loginUser = ContextUtil.getContextUser();
        return favoriteService.getByTags(loginUser, favorite);
    }

    @PutMapping
    public FavoritesVo addFavorite(@RequestBody @Validated(CreateFavorite.class) FavoriteDto favorite) {
        LoginUser loginUser = ContextUtil.getContextUser();
        return favoriteService.addFavorite(loginUser, favorite);
    }

    @DeleteMapping
    public DataVo<?> deleteFavorite(@Validated(DeleteFavorite.class) FavoriteDto favorite) {
        LoginUser loginUser = ContextUtil.getContextUser();
        return favoriteService.deleteFavorite(loginUser, favorite);
    }

    @PatchMapping
    public FavoritesVo updateFavorite(@RequestBody @Validated(UpdateFavorite.class) FavoriteDto favorite) throws HttpError {
        LoginUser loginUser = ContextUtil.getContextUser();
        return favoriteService.setFavorite(loginUser, favorite);
    }

    @PutMapping("tag")
    public FavoritesVo addTag(@RequestBody @Validated(AddTag.class) FavoriteDto tag) {
        LoginUser loginUser = ContextUtil.getContextUser();
        return favoriteService.addTag(loginUser, tag);
    }

    @PatchMapping("tag")
    public FavoritesVo updateTag(@RequestBody @Validated(ReplaceTag.class) FavoriteDto tag) throws HttpError {
        LoginUser loginUser = ContextUtil.getContextUser();
        return favoriteService.replaceTag(loginUser, tag);
    }

    @DeleteMapping ("tag")
    public FavoritesVo deleteTag(@Validated(DeleteTag.class) FavoriteDto tag) {
        LoginUser loginUser = ContextUtil.getContextUser();
        return favoriteService.deleteTag(loginUser, tag);
    }

    @PatchMapping("updateTags")
    public FavoritesVo updateAllTags(@Validated(UpdateTags.class) FavoriteDto tag) {
        LoginUser loginUser = ContextUtil.getContextUser();
        return favoriteService.updateAllTags(loginUser, tag);
    }
}
