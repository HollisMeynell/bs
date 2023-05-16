package l.f.mappool.repository;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MapPoolUserRepositoryTest {
    @Resource
    MapPoolUserRepository mapPoolUserRepository;

    @Test
    void getMapPoolUserByPoolIdAndUserId() {
    }

    @Test
    void searchAllByUserId() {
    }

    @Test
    void getUserCreatedSize() {
        mapPoolUserRepository.getUserCreatedSize(43);
    }
}