package l.f.mappool.dao;

import jakarta.annotation.Resource;
import l.f.mappool.repository.FavoriteRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class FavoriteDaoTest {
    @Resource
    FavoriteDao favoriteDao;
    @Resource
    FavoriteRepository favoriteRepository;


    long uid = 17064371L;
    long bid = 15463L;
    @Test
    void testAdd() {
        var f = favoriteDao.addFavorite(uid, bid+6, "", "k", "k", "p");
    }

    @Test
    void testTag() {
        favoriteRepository.addTags(1, "r", "g","t");
    }

    @Test
    void getTag() {
        favoriteDao.getFavoritesByTag(uid, "t").forEach(e->log.info(e.toString()));
    }
}