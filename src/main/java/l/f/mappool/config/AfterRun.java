package l.f.mappool.config;

import l.f.mappool.properties.BeatmapSelectionProperties;
import l.f.mappool.repository.file.OsuFileLogRepository;
import l.f.mappool.service.OsuApiService;
import l.f.mappool.service.OsuFileService;
import l.f.mappool.util.TokenBucketUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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
        Runnable run = ()->{
            TokenBucketUtil.closeTask();
            log.info("error count: [{}], shutdown!", ERROR_COUNT);
        };
        Runtime.getRuntime().addShutdownHook(new Thread(run,"endThread"));
    }
}
