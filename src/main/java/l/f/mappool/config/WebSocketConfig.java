package l.f.mappool.config;

import l.f.mappool.controller.WebSocketController;
import l.f.mappool.entity.LoginUser;
import l.f.mappool.service.UserService;
import l.f.mappool.util.ContextUtil;
import l.f.mappool.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Component
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketInterceptor webSocketInterceptor;

    @Autowired
    public WebSocketConfig(WebSocketInterceptor interceptor){
        webSocketInterceptor = interceptor;
    }
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketController(), "ws").addInterceptors(webSocketInterceptor);
        log.info("ws ok");
    }
}

@Slf4j
class WebSocketInterceptor implements HandshakeInterceptor {
    private final UserService userService;
    public WebSocketInterceptor (UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String header = request.getHeaders().getFirst("Authorization");
        if (ObjectUtils.isEmpty(header) || !header.startsWith("Bearer ")) {
            response.setStatusCode(HttpStatusCode.valueOf(401));
            return false;
        }
        String token = header.substring(7);
        LoginUser loginUser = JwtUtil.verifyToken(token);
        if (ObjectUtils.isEmpty(loginUser) || !userService.loginCheck(loginUser)) {
            response.setStatusCode(HttpStatusCode.valueOf(401));
            return false;
        }
        ContextUtil.setContextUser(loginUser);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}