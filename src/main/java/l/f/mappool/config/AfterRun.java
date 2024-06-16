package l.f.mappool.config;

import jakarta.annotation.Resource;
import l.f.mappool.properties.BeatmapSelectionProperties;
import l.f.mappool.repository.file.OsuFileLogRepository;
import l.f.mappool.service.OsuFileService;
import l.f.mappool.util.DataUtil;
import l.f.mappool.util.TokenBucketUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static l.f.mappool.config.AspectConfig.ERROR_COUNT;

@Slf4j
@Component
public class AfterRun implements CommandLineRunner {
    @Resource
    OsuFileLogRepository       repository;
    @Resource
    OsuFileService             osuFileService;
    @Resource
    BeatmapSelectionProperties properties;
    @Override
    public void run(String... args) throws InterruptedException {
        log.info("ok!");
        updateOsuFileDb();
        Runnable run = ()->{
            TokenBucketUtil.closeTask();
            log.info("error count: [{}], shutdown!", ERROR_COUNT);
        };
        Runtime.getRuntime().addShutdownHook(new Thread(run,"endThread"));
    }

    private void updateOsuFileDb() {
        var OSU_FILE_PATH = properties.getFilePath() + "/osu";
        try {
            osuFileService.removeTemp();
        } catch (IOException ignore) {
        }

        int maxSize = 500;
        var p = repository.queryByFileContainingIgnoreCase("", PageRequest.ofSize(maxSize));
        do  {
            p.forEach(osuFileRecord -> {
                var path = Path.of(OSU_FILE_PATH, String.valueOf(osuFileRecord.getSid())).resolve(osuFileRecord.getFile());
                osuFileRecord.setCheck(DataUtil.getFileMd5(path));
                osuFileRecord.setLast(LocalDateTime.now());
            });
            repository.saveAll(p);
            p = repository.queryByFileContainingIgnoreCase("", PageRequest.of(p.getNumber() + 1,maxSize));
        } while (p.hasNext());
    }
}
