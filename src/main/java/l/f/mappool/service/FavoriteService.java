package l.f.mappool.service;

import l.f.mappool.dao.FavoriteDao;
import l.f.mappool.dto.FavoriteDto;
import l.f.mappool.entity.Favorite;
import l.f.mappool.entity.User;
import l.f.mappool.exception.HttpError;
import l.f.mappool.vo.DataListVo;
import l.f.mappool.vo.DataVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {
    OsuApiService osuApiService;
    FavoriteDao favoriteDao;
    @Autowired
    public FavoriteService (
            OsuApiService apiService,
            FavoriteDao favoriteDao
    ){
        this.osuApiService = apiService;
        this.favoriteDao = favoriteDao;
    }

    public DataVo<Favorite> addFavorite(User user, FavoriteDto data) {
        var favorite = favoriteDao.addFavorite(user.getOsuId(), data.getBid(), data.getInfo(), data.getTags());
        return new DataVo<>(favorite);
    }

    public DataVo<Favorite> setFavorite(User user, FavoriteDto data) throws HttpError {
        var favorite = favoriteDao.findFavorite(user.getOsuId(), data.getBid());
        favorite = favoriteDao.setFavorite(favorite, data.getInfo(), data.getTags());
        return new DataVo<>(favorite);
    }

    public DataVo<?> deleteFavorite(User user, FavoriteDto data) {
        Favorite favorite;
        try {
            favorite = favoriteDao.findFavorite(user.getOsuId(), data.getBid());
        } catch (HttpError e){
            return new DataVo<>("已被删除或者不存在");
        }
        favoriteDao.deleteFavorite(favorite);
        return new DataVo<>("删除成功");
    }

    public DataVo<List<Favorite>> getAllFavorite(User user) {
        var data = favoriteDao.getFavorites(user.getOsuId());
        return new DataVo<>(data);
    }

}
