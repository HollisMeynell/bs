package l.f.mappool.config;

import l.f.mappool.util.TokenBucketUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static l.f.mappool.config.AspectConfig.ERROR_COUNT;

@Slf4j
@Component
public class AfterRun implements CommandLineRunner {
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
