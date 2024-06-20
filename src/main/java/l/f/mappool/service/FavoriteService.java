package l.f.mappool.service;

import l.f.mappool.dao.FavoriteDao;
import l.f.mappool.dto.FavoriteDto;
import l.f.mappool.entity.Favorite;
import l.f.mappool.entity.LoginUser;
import l.f.mappool.exception.HttpError;
import l.f.mappool.vo.DataVo;
import l.f.mappool.vo.FavoritesVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {
    final OsuApiService osuApiService;
    final FavoriteDao   favoriteDao;

    @Autowired
    public FavoriteService(
            OsuApiService apiService,
            FavoriteDao favoriteDao
    ) {
        this.osuApiService = apiService;
        this.favoriteDao = favoriteDao;
    }

    public FavoritesVo addFavorite(LoginUser loginUser, FavoriteDto data) {
        var favorite = favoriteDao.addFavorite(loginUser.getOsuId(), data.getBid(), data.getInfo(), data.getTags());
        return new FavoritesVo(favorite).parseBeatMap(osuApiService);
    }

    public FavoritesVo setFavorite(LoginUser loginUser, FavoriteDto data) {
        var favorite = favoriteDao.findFavorite(loginUser.getOsuId(), (int) data.getId());
        favorite = favoriteDao.setFavorite(favorite, data.getInfo(), data.getTags());
        return new FavoritesVo(favorite).parseBeatMap(osuApiService);
    }

    public DataVo<?> deleteFavorite(LoginUser loginUser, FavoriteDto data) {
        var favorite = favoriteDao.findFavorite(loginUser.getOsuId(), (int) data.getId());
        favoriteDao.deleteFavorite(favorite);
        return new DataVo<>("删除成功");
    }

    public DataVo<List<Favorite>> getAllFavorite(LoginUser loginUser) {
        var data = favoriteDao.getFavorites(loginUser.getOsuId());
        return new DataVo<>(data);
    }

    public DataVo<String[]> getAllTags(LoginUser loginUser) {
        var tagList = favoriteDao.getTags(loginUser.getOsuId());
        return new DataVo<>(tagList.toArray(new String[0]));
    }

    public DataVo<List<Favorite>> getByTags(LoginUser loginUser, FavoriteDto data) {
        var favorites = favoriteDao.getFavoritesByTag(loginUser.getOsuId(), data.getTags());
        return new DataVo<>(favorites);
    }

    /**************************  tag ********************************/
    public FavoritesVo addTag(LoginUser loginUser, FavoriteDto data) {
        var favorite = favoriteDao.findFavorite(loginUser.getOsuId(), (int) data.getId());
        return new FavoritesVo(favoriteDao.addTag(favorite, data.getTag()));
    }

    public FavoritesVo deleteTag(LoginUser loginUser, FavoriteDto data) {
        var favorite = favoriteDao.findFavorite(loginUser.getOsuId(), (int) data.getId());
        return new FavoritesVo(favoriteDao.delTag(favorite, data.getTag()));
    }

    public FavoritesVo updateAllTags(LoginUser loginUser, FavoriteDto data) {
        var favorite = favoriteDao.findFavorite(loginUser.getOsuId(), (int) data.getId());
        return new FavoritesVo(
                favoriteDao.setFavorite(favorite, favorite.getInfo(), data.getTags())
        );
    }

    public FavoritesVo replaceTag(LoginUser loginUser, FavoriteDto data) throws HttpError {
        var favorite = favoriteDao.findFavorite(loginUser.getOsuId(), (int) data.getId());
        if (data.getTags().length != 2) throw new HttpError(403, "参数无效");
        return new FavoritesVo(favoriteDao.replaceTag(favorite, data.getTags()[0], data.getTags()[1]));
    }
}
