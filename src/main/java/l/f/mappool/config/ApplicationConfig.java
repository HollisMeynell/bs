package l.f.mappool.config;

import l.f.mappool.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Configuration
@SuppressWarnings("unused")
public class ApplicationConfig {

    UserService userService;

    @Autowired
    @Lazy
    void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public WebSocketInterceptor webSocketInterceptor() {
        return new WebSocketInterceptor(userService);
    }

}
