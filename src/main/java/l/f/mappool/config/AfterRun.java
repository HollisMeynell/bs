package l.f.mappool.config;

import l.f.mappool.util.TokenBucketUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static l.f.mappool.config.AspectConfig.ERROR_COUNT;

@Component
public class AfterRun implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(AfterRun.class);
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
