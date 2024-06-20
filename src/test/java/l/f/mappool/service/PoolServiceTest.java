package l.f.mappool.service;

import jakarta.annotation.Resource;
import l.f.mappool.exception.HttpError;
import l.f.mappool.util.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PoolServiceTest {
    @Resource
    MapPoolService mapPoolService;
    @Resource
    OsuApiService osuApiService;

    @Test
    void testMark() {
        // 增
        mapPoolService.addMarkPool(17064371L, 1);
        // 查
        var data = mapPoolService.getAllMarkPool(17064371L);
        assertEquals(1, data.getPageSize());
        assertEquals(1, data.getData().getFirst().getId());
        assertEquals("name1", data.getData().getFirst().getName());
        // 删
        var i = mapPoolService.deleteMarkPool(17064371L, 1);
        assertEquals(1, i);
        data = mapPoolService.getAllMarkPool(17064371L);
        assertEquals(0, data.getPageSize());
    }

    @Test
    void testExport() {
        var uid = 17064371L;
        /*
        var pool = mapPoolService.createMapPool(uid, "name",  "banner-uuid", "info");
        var g1 = mapPoolService.createCategoryGroup(uid , pool.getId(), "NM", "这是NM", -3080247);
        var g2 = mapPoolService.createCategoryGroup(uid , pool.getId(), "HD", "这是HD", -282);
        var c1_1 = mapPoolService.createCategory(uid, g1.getId(), "NM1");
        var c1_2 = mapPoolService.createCategory(uid, g1.getId(), "NM2");
        var c2_2 = mapPoolService.createCategory(uid, g2.getId(), "HD1");

        c1_1 = mapPoolService.choseCategory(uid, 2, 114514L);
        c1_2 = mapPoolService.choseCategory(uid, 3, 1919810L);
        c2_2 = mapPoolService.choseCategory(uid, 4, 1611L);
        */

        try {
            var p = mapPoolService.getExportPool(4);
            p.parseMapInfo(osuApiService);
            System.out.println(JsonUtil.objectToJsonPretty(p));
        } catch (HttpError e) {
            throw new RuntimeException(e);
        }
    }
}