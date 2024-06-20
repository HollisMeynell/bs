package l.f.mappool.dao;

import l.f.mappool.entity.Favorite;
import l.f.mappool.exception.HttpError;
import l.f.mappool.exception.NotFoundException;
import l.f.mappool.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FavoriteDao {
    private final FavoriteRepository favoriteRepository;

    @Autowired
    public FavoriteDao(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    public Favorite addFavorite(long uid, long bid, String info, String... tags) {
        var favorite = new Favorite().setUserId(uid).setBid(bid).setInfo(info).setTags(tags);
        return favoriteRepository.save(favorite);
    }

    public Optional<Favorite> getFavoriteById(int favoriteId) {
        return favoriteRepository.findById(favoriteId);
    }

    public void deleteFavorite(Favorite favorite) {
        favoriteRepository.delete(favorite);
    }

    public Favorite setFavorite(Favorite favorite, String info, String... tags) {
        favorite.setInfo(info);
        favorite.setTags(tags);
        return favoriteRepository.save(favorite);
    }

    public List<Favorite> getFavorites(long uid) {
        return favoriteRepository.getAllByUserId(uid);
    }
    public List<Favorite> getFavoritesByTag(long uid, String... tag) {
        return favoriteRepository.searchUserAllByTags(uid, tag);
    }

    public List<String> getTags(long uid) {
        return favoriteRepository.allUserTags(uid);
    }

    public Favorite addTag(Favorite favorite, String... tag) {
        // 添加标签
//        favorite.setTags(setTags(favorite.getTags(), true, tag));
        favoriteRepository.addTags(favorite.getId(), tag);
        return favorite;
    }

    public Favorite delTag(Favorite favorite, String tag) {
//        favorite.setTags(setTags(favorite.getTags(), tag, false));
        favoriteRepository.deleteTags(favorite.getId(), tag);
        return favorite;
    }

    public Favorite replaceTag(Favorite favorite, String oldTag, String newTag) {
        favoriteRepository.replaceTags(favorite.getId(), oldTag, newTag);
        return favorite;
    }

    public Favorite delTags(Favorite favorite, String... tag) {
        // 删除标签
        favorite.setTags(setTags(favorite.getTags(), false, tag));
        return favoriteRepository.save(favorite);
    }

    public Favorite findFavorite(long uid, long bid) throws HttpError {
        var favoriteOptional = favoriteRepository.findByBidAndUserId(bid, uid);
        if (favoriteOptional.isEmpty()) {
            throw new HttpError(404, "Not found");
        }
        return favoriteOptional.get();
    }

    public Favorite findFavorite(Long uid, int id) {
        var favoriteOpt = getFavoriteById(id);
        if (favoriteOpt.isEmpty()) {
            throw new NotFoundException();
        }
        var favorite = favoriteOpt.get();
        if (!uid.equals(favorite.getUserId())) {
            throw new NotFoundException();
        }
        return favorite;
    }


    private String[] setTags(String[] tags, String tag, boolean add) {
        var tagSet = new HashSet<>(Arrays.asList(tags));
        if (add) {
            tagSet.add(tag);
        } else {
            tagSet.remove(tag);
        }
        return tagSet.toArray(new String[0]);
    }

    private String[] setTags(String[] tags, boolean add, String... newTags) {
        var tagSet = new HashSet<>(Arrays.asList(tags));

        if (add) {
            Collections.addAll(tagSet, newTags);
        } else {
            for (var tag : newTags) {
                tagSet.remove(tag);
            }
        }

        return tagSet.toArray(new String[0]);
    }
}
