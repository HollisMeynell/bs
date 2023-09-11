package l.f.mappool.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AfterRun implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(AfterRun.class);
    @Override
    public void run(String... args) {
        log.info("ok!");
        Runnable run = ()->{
            log.info("shutdown!");
        };
        Runtime.getRuntime().addShutdownHook(new Thread(run,"endThread"));
    }
}
