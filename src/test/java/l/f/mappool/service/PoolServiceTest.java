package l.f.mappool.service;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PoolServiceTest {
    @Resource
    MapPoolService mapPoolService;

    @Test
    void testMark() {
        // 增
        mapPoolService.addMarkPool(17064371L, 1);
        // 查
        var data = mapPoolService.getAllMarkPool(17064371L);
        assertEquals(1, data.getPageSize());
        assertEquals(1, data.getData().get(0).getId());
        assertEquals("name1", data.getData().get(0).getName());
        // 删
        var i = mapPoolService.deleteMarkPool(17064371L, 1);
        assertEquals(1, i);
        data = mapPoolService.getAllMarkPool(17064371L);
        assertEquals(0, data.getPageSize());
    }
}