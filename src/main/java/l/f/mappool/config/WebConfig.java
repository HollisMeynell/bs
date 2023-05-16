package l.f.mappool.config;

import jakarta.annotation.Resource;
import l.f.mappool.config.interceptor.AuthenticationInterceptor;
import l.f.mappool.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Lazy
    @Resource
    UserService userService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthenticationInterceptor(userService));
    }
}
