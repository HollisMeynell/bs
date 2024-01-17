package l.f.mappool;

import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {WebFluxAutoConfiguration.class})
public class MapPoolApplication {
    public static org.slf4j.Logger log = LoggerFactory.getLogger("Main-log");

    public static void main(String[] args) {
        var application = new SpringApplication(MapPoolApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }
}
