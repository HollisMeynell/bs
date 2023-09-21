package l.f.mappool.repository;

import jakarta.annotation.Resource;
import l.f.mappool.repository.pool.PoolUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PoolUserRepositoryTest {
    @Resource
    PoolUserRepository poolUserRepository;

    @Test
    void getMapPoolUserByPoolIdAndUserId() {
    }

    @Test
    void searchAllByUserId() {
    }

    @Test
    void getUserCreatedSize() {
        poolUserRepository.getUserCreatedSize(43);
    }
}