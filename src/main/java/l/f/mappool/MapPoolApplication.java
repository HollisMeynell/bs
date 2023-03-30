package l.f.mappool;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MapPoolApplication {
    public static org.slf4j.Logger log = LoggerFactory.getLogger("Main-log");
    public static void main(String[] args) {
        SpringApplication.run(MapPoolApplication.class, args);
    }
}
