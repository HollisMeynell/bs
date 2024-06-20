package l.f.mappool.config.interceptor;

import l.f.mappool.entity.LoginUser;
import l.f.mappool.service.UserService;
import l.f.mappool.util.ContextUtil;
import l.f.mappool.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class WebSocketInterceptor implements HandshakeInterceptor {
    private final UserService userService;

    public WebSocketInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, @NotNull ServerHttpResponse response, @NotNull WebSocketHandler wsHandler, @NotNull Map<String, Object> attributes) {
        String header;
        if (ObjectUtils.isEmpty((header = request.getHeaders().getFirst("Authorization"))) || !header.startsWith("Bearer ")) {
            return true;
        }

        String token = header.substring(7);
        LoginUser loginUser = JwtUtil.verifyToken(token);
        if (Objects.nonNull(loginUser) && !userService.loginCheck(loginUser)) {
//            response.setStatusCode(HttpStatusCode.valueOf(401));
            ContextUtil.setContextUser(null);
        }
        return true;
    }

    @Override
    public void afterHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response, @NotNull WebSocketHandler wsHandler, Exception exception) {

    }
}