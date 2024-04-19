package l.f.mappool.repository.file;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class OsuFileLogRepositoryTest {
    @Resource
    OsuFileLogRepository osuFileLogRepository;

    @Test
    void testCount() {
        int a = osuFileLogRepository.countBySid();
        int b = osuFileLogRepository.countAll();

        log.info("a:{} b:{}", a, b);
    }
}