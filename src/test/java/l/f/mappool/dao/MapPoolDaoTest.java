package l.f.mappool.dao;

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

    @Test
    void testCreatePoolAndGetPool() {
        long u1 = 114514;
        long u2 = 1919810;
//        var p1 = mapPoolDao.createPool(u1, "u1", "xxxx");
//        mapPoolDao.addAdminUser(u1, u2, p1.getId());
//        var p2 = mapPoolDao.createPool(u2, "u2", "xxxx");
        var l = mapPoolDao.getAllPool(u2);
        log.info(String.valueOf(l.size()));
        log.info("ok");
    }

    @Test
    void testCreateCategoryAndItem(){

    }
}