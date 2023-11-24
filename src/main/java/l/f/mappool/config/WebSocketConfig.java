package l.f.mappool.config;

import l.f.mappool.config.interceptor.WebSocketInterceptor;
import l.f.mappool.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Slf4j
@Component
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketService webSocketService;
    private final WebSocketInterceptor webSocketInterceptor;

    public WebSocketConfig(WebSocketService webSocketService, WebSocketInterceptor webSocketInterceptor) {
        this.webSocketService = webSocketService;
        this.webSocketInterceptor = webSocketInterceptor;
    }
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketService, WebSocketService.PATH)
                .addInterceptors(webSocketInterceptor)
                .setAllowedOrigins("*");
    }
}