package l.f.mappool.config;

import io.undertow.Undertow;
import jakarta.annotation.Resource;
import l.f.mappool.config.interceptor.AuthenticationInterceptor;
import l.f.mappool.properties.BeatmapSelectionProperties;
import l.f.mappool.service.UserService;
import l.f.mappool.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServer;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.embedded.undertow.UndertowWebServer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.xnio.Xnio;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.concurrent.Executor;

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

    UndertowServletWebServerFactory undertowServletWebServerFactory = new UndertowServletWebServerFactory() {
        @Override
        public WebServer getWebServer(ServletContextInitializer... initializers) {
            var server = (UndertowServletWebServer) super.getWebServer(initializers);
            var xnio = Xnio.getInstance(UndertowServletWebServerFactory.class.getClassLoader());
            var worker = xnio.createWorkerBuilder()
                    .setExternalExecutorService(ThreadPoolConfig.getExecutorService())
                    .build();
            Field builderField = ReflectionUtils.findField(UndertowWebServer.class, "builder");
            String errMessage = "not find UndertowServletWebServer's builder";
            ReflectionUtils.makeAccessible(Objects.requireNonNull(builderField, errMessage));
            var build = (Undertow.Builder) ReflectionUtils.getField(Objects.requireNonNull(builderField, errMessage), server);
            Objects.requireNonNull(build, errMessage);
            build.setWorker(worker);
            build.setIoThreads(Integer.MAX_VALUE);
            build.setWorkerThreads(Integer.MAX_VALUE);
            return server;
        }
    };

    @Bean
    public ServletWebServerFactory servletWebServerFactory(Executor threadPoolTaskExecutor) {
        undertowServletWebServerFactory.addDeploymentInfoCustomizers(
                deploymentInfo -> deploymentInfo
                        .setAsyncExecutor(threadPoolTaskExecutor)
                        .setExecutor(threadPoolTaskExecutor)
        );
        return undertowServletWebServerFactory;
    }

}
