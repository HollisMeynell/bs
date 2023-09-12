package l.f.mappool.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import l.f.mappool.enums.PoolPermission;
import l.f.mappool.repository.MapPoolRepository;
import l.f.mappool.service.MapPoolService;
import l.f.mappool.util.JsonUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MapPoolDaoTest {
    private static final Logger log = LoggerFactory.getLogger(MapPoolDaoTest.class);
    @Autowired
    MapPoolDao mapPoolDao;
    @Autowired
    MapPoolService mapPoolService;
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
        var c1 = mapPoolDao.queryByName("u", 1, 1, 1);
        var count = mapPoolDao.countByName("u", 1);
        log.info("count: {}, c: {}", count, c1);
    }

    long uid = 115533L;
    long uid1 = 115534L;

    @Test
    void testAll() {
        var p = mapPoolDao.getAllPool(uid).get(PoolPermission.CREATE).get(0);
        var g = mapPoolDao.createCategoryGroup(uid, p.getId(), "e name", "# e info", 0x000);
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

    @Test
    void testGroup() throws JsonProcessingException {
        var g = mapPoolDao.getAllCategotys(1);
        ObjectMapper m = JsonMapper.builder().build();
        var j = m.writerWithDefaultPrettyPrinter().writeValueAsString(g);
        log.info(j);
    }

    @Test
    void createALl() {
        var uid = 17064371L;
        var pool = mapPoolDao.createPool(uid, "testCreat", "banner", "info");
        var group1 = mapPoolDao.createCategoryGroup(uid, pool.getId(), "testCreatGroup", "cf", 0);
        var group2 = mapPoolDao.createCategoryGroup(uid, pool.getId(), "testCreatGroup x", "cf2", 0);
        group2 = mapPoolService.updateCategoryGroup(uid, group2.getId(), "testCreatGroup 2", "cfx", 2, 6);

        var c = mapPoolService.createCategory(uid, group1.getId(), "G1");
        var item = mapPoolService.createCategoryItem(uid, c.getId(), 16115, "ccc");

        mapPoolDao.createFeedback(uid, item.getId(), null, "aaa");
        var f1 = mapPoolDao.createFeedback(uid, item.getId(), false, "bbb");
        mapPoolDao.createFeedback(uid, item.getId(), true, "ccc");
        mapPoolDao.handleFeedback(f1, true);

        c = mapPoolService.choseCategory(uid, c.getId(), 16115L);

        var listd = mapPoolService.getCategoryGroup(pool.getId());
        log.info(JsonUtil.objectToJsonPretty(listd));
        log.info(JsonUtil.objectToJsonPretty(mapPoolService.getPublicFeedbackFromItem(item.getId())));
        log.info(JsonUtil.objectToJsonPretty(mapPoolService.getFeedbackFromItem(uid, item.getId())));
    }

    @Test
    void testJsonOut(){
        var uid = 17064371L;
        var listd = mapPoolService.getCategoryGroup(3);

        var item = mapPoolDao.getMapCategoryItemById(1).get();
        log.info(JsonUtil.objectToJsonPretty(listd));
        log.info(JsonUtil.objectToJsonPretty(mapPoolService.getPublicFeedbackFromItem(item.getId())));
        log.info(JsonUtil.objectToJsonPretty(mapPoolService.getFeedbackFromItem(uid, item.getId())));
    }
}