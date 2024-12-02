package l.f.mappool.config;

import jakarta.annotation.Resource;
import l.f.mappool.config.interceptor.AuthenticationInterceptor;
import l.f.mappool.properties.BeatmapSelectionProperties;
import l.f.mappool.service.UserService;
import l.f.mappool.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public WebConfig(BeatmapSelectionProperties properties) {
        // 初始化管理员列表
        JwtUtil.setAdminUsers(properties.getAdminUsers());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthenticationInterceptor(userService));
    }

}
