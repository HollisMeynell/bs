package l.f.mappool.repository.file;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

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

    @Test
    void testQueryLike() {
        var p = osuFileLogRepository.queryByFileContainingIgnoreCase("", PageRequest.ofSize(10));
        log.info("p: {}", p.getTotalElements());
    }
}