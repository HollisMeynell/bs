package l.f.mappool.config;

import l.f.mappool.util.TokenBucketUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AfterRun implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(AfterRun.class);
    @Override
    public void run(String... args) {
        log.info("ok!");
        Runnable run = ()->{
            TokenBucketUtil.closeTask();
            log.info("shutdown!");
        };
        var lib = new ClassPathResource("/lib");
        try {
            log.info("lib:{}",lib.getFile());
        } catch (IOException e) {
            log.error("", e);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(run,"endThread"));
    }
}
