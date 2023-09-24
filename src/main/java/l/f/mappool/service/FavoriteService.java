package l.f.mappool.service;

import l.f.mappool.dao.FavoriteDao;
import l.f.mappool.dto.FavoriteDto;
import l.f.mappool.entity.Favorite;
import l.f.mappool.entity.User;
import l.f.mappool.exception.HttpError;
import l.f.mappool.vo.DataListVo;
import l.f.mappool.vo.DataVo;
import l.f.mappool.vo.FavoritesVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {
    OsuApiService osuApiService;
    FavoriteDao   favoriteDao;

    @Autowired
    public FavoriteService(
            OsuApiService apiService,
            FavoriteDao favoriteDao
    ) {
        this.osuApiService = apiService;
        this.favoriteDao = favoriteDao;
    }

    public FavoritesVo addFavorite(User user, FavoriteDto data) {
        var favorite = favoriteDao.addFavorite(user.getOsuId(), data.getBid(), data.getInfo(), data.getTags());
        return new FavoritesVo(favorite).parseBeatMap(osuApiService);
    }

    public FavoritesVo setFavorite(User user, FavoriteDto data) {
        var favorite = favoriteDao.findFavorite(user.getOsuId(), (int) data.getId());
        favorite = favoriteDao.setFavorite(favorite, data.getInfo(), data.getTags());
        return new FavoritesVo(favorite).parseBeatMap(osuApiService);
    }

    public DataVo<?> deleteFavorite(User user, FavoriteDto data){
        var favorite = favoriteDao.findFavorite(user.getOsuId(), (int)data.getId());
        favoriteDao.deleteFavorite(favorite);
        return new DataVo<>("删除成功");
    }

    public DataVo<List<Favorite>> getAllFavorite(User user) {
        var data = favoriteDao.getFavorites(user.getOsuId());
        return new DataVo<>(data);
    }

    public DataVo<String[]> getAllTags(User user) {
        var tagList = favoriteDao.getTags(user.getOsuId());
        return new DataVo<>(tagList.toArray(new String[tagList.size()]));
    }

    public DataVo<List<Favorite>> getByTags(User user, FavoriteDto data) {
        var favorites = favoriteDao.getFavoritesByTag(user.getOsuId(), data.getTags());
        return new DataVo<>(favorites);
    }

    /**************************  tag ********************************/
    public FavoritesVo addTag(User user, FavoriteDto data) {
        var favorite = favoriteDao.findFavorite(user.getOsuId(), (int)data.getId());
        return new FavoritesVo(favoriteDao.addTag(favorite, data.getTag()));
    }
    public FavoritesVo deleteTag(User user, FavoriteDto data) {
        var favorite = favoriteDao.findFavorite(user.getOsuId(), (int)data.getId());
        return new FavoritesVo(favoriteDao.delTag(favorite, data.getTag()));
    }


    public FavoritesVo replaceTag(User user, FavoriteDto data) throws HttpError {
        var favorite = favoriteDao.findFavorite(user.getOsuId(), (int)data.getId());
        if (data.getTags().length != 2) throw new HttpError(403, "参数无效");
        return new FavoritesVo(favoriteDao.replaceTag(favorite, data.getTags()[0], data.getTags()[1]));
    }
}
