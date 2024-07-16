package l.f.mappool.config;

import jakarta.annotation.Resource;
import l.f.mappool.entity.osu.BeatMapSet;
import l.f.mappool.properties.BeatmapSelectionProperties;
import l.f.mappool.repository.file.OsuFileLogRepository;
import l.f.mappool.service.OsuApiService;
import l.f.mappool.service.OsuFileService;
import l.f.mappool.util.TokenBucketUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;

import static l.f.mappool.config.AspectConfig.ERROR_COUNT;

@Slf4j
@Component
@AllArgsConstructor
public class AfterRun implements CommandLineRunner {
    BeatmapSelectionProperties properties;
    OsuFileService service;
    OsuFileLogRepository repository;
    OsuApiService osuService;

    @Override
    public void run(String... args) {
        log.info("ok!");
        // 用于临时解决mapSet 的json反序列化问题, 触发原因未知
        // 必须启动时调用一次
        Thread.startVirtualThread(() -> {
            BeatMapSet data = null;
            try {
                Thread.sleep(Duration.ofSeconds(3));
                data = osuService.getMapsetInfo(725853);
            } catch (Exception ignore) {
            }
            if (Objects.isNull(data)) {
                log.error("get mapset service error");
            }
        });

        Runnable run = ()->{
            TokenBucketUtil.closeTask();
            log.info("error count: [{}], shutdown!", ERROR_COUNT);
        };
        Runtime.getRuntime().addShutdownHook(new Thread(run,"endThread"));
    }
}
