package l.f.mappool.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import l.f.mappool.util.JsonUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Configuration
@SuppressWarnings({"unused", "SameReturnValue"})
public class ApplicationConfig {

    @Bean
    @Primary
    ObjectMapper jacksonObjectMapper() {
        return JsonUtil.DEFAULT_MAPPER;
    }
}
