package l.f.mappool.dao;

import l.f.mappool.enums.PoolPermission;
import l.f.mappool.enums.PoolStatus;
import l.f.mappool.repository.MapPoolRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MapPoolDaoTest {
    private static final Logger log = LoggerFactory.getLogger(MapPoolDaoTest.class);
    @Autowired
    MapPoolDao mapPoolDao;
    @Autowired
    MapPoolRepository poolRepository;

    @Test
    void testCreatePoolAndGetPool() {
        long u1 = 114514;
        long u2 = 1919810;
        var p1 = mapPoolDao.createPool(u1, "u1", "xxxx", "xxx");
        mapPoolDao.addAdminUser(u1, u2, p1.getId());
        var p2 = mapPoolDao.createPool(u2, "u2", "xxxx", "xxx");
        var l = mapPoolDao.getAllPool(u2);
        log.info(String.valueOf(l.size()));
        log.info("ok");
    }

    @Test
    void testPageQuery() {
        var c1 = mapPoolDao.queryByName("u", 1, 1);
        var count = mapPoolDao.countByName("u");
        log.info("count: {}, c: {}", count, c1);
    }

    long uid = 115533L;
    long uid1 = 115534L;

    @Test
    void testAll() {
        var p = mapPoolDao.getAllPool(uid).get(PoolPermission.CREATE).get(0);
        var g = mapPoolDao.createCategoryGroup(uid, p.getId(), "e name", "# e info", 000);
        var c = mapPoolDao.createCategory(uid, g.getId(), "eeeffff");
        var map0 = mapPoolDao.createCategoryItem(uid, c.getId(), 1155, "dsa");
        mapPoolDao.addUser(uid1, p.getId(), PoolPermission.CHOOSER);
        var map1 = mapPoolDao.createCategoryItem(uid1, c.getId(), 6644, "dsa");
        var f0 = mapPoolDao.createFeedback(uid1, map0.getId(), null, "fffff");
        var f1 = mapPoolDao.createFeedback(uid, map1.getId(), false, "xxxxxx");

        log.info("ok");
    }

    @Test
    void testGetAll() {
        var p1 = mapPoolDao.getAllPool(uid1);
        var p2 = mapPoolDao.getAllPool(uid);
        var p = p2.get(PoolPermission.CREATE).get(0);
        var gs = mapPoolDao.getAllCategotys(p.getId());
        log.info(gs.get(0).getName());
        log.info(gs.get(0).getCategories().get(0).getName());
        log.info("ok");
    }
}