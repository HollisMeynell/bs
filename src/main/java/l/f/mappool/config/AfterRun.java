package l.f.mappool.config;

import jakarta.annotation.Resource;
import l.f.mappool.properties.BeatmapSelectionProperties;
import l.f.mappool.repository.file.OsuFileLogRepository;
import l.f.mappool.service.OsuFileService;
import l.f.mappool.util.TokenBucketUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import static l.f.mappool.config.AspectConfig.ERROR_COUNT;

@Slf4j
@Component
public class AfterRun implements CommandLineRunner {
    @Resource
    BeatmapSelectionProperties properties;
    @Resource
    OsuFileService service;
    @Resource
    OsuFileLogRepository repository;
    @Override
    public void run(String... args) {
        log.info("ok!");
        Runnable run = ()->{
            TokenBucketUtil.closeTask();
            log.info("error count: [{}], shutdown!", ERROR_COUNT);
        };
        delete();
        Runtime.getRuntime().addShutdownHook(new Thread(run,"endThread"));
    }

    void delete() {
        var p = Path.of(properties.getFilePath(), "copy");
        try {
            Files.deleteIfExists(p);
        } catch (IOException e) {
            log.error("-", e);
        }
    }

    @SuppressWarnings("unused")
    void initCopyDir() throws IOException {
        var p = Path.of(properties.getFilePath(), "copy");
        var directory = Path.of(properties.getFilePath(), "osu");
        if (Files.isRegularFile(p)) return;
        Files.createFile(p);

        if (properties.getLocalOsuDirectory().isEmpty()) return;

        p = Path.of(properties.getLocalOsuDirectory().get());

        var page = repository.queryByFileContainingIgnoreCase("", PageRequest.ofSize(200));
        while (true) {
            page.forEach(s -> service.copyLink(s.getBid(), directory.resolve(String.valueOf(s.getSid())).resolve(s.getFile())));
            if (!page.hasNext()) break;
            page = repository.queryByFileContainingIgnoreCase("", page.nextPageable());
        }
        var x = Files.newDirectoryStream(p, (s -> Files.isRegularFile(s, LinkOption.NOFOLLOW_LINKS)));
        x.forEach(path -> {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        x.close();
    }
}
