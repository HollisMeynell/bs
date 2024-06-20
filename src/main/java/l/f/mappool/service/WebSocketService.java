package l.f.mappool.service;

import l.f.mappool.entity.LoginUser;
import l.f.mappool.util.ContextUtil;
import l.f.mappool.util.JsonUtil;
import l.f.mappool.util.JwtUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class WebSocketService extends TextWebSocketHandler {
    private static final String USER_KEY = "user";
    private static final ConcurrentHashMap<Long, List<WebSocketSession>> SESSIONS_MAP = new ConcurrentHashMap<>();
    public static final  String                                          PATH         = "/api/websocket";
    private final        UserService                                     userService;

    public WebSocketService(UserService userService) {
        this.userService = userService;
    }

    @SneakyThrows
    private static void check(WebSocketSession session) {
        try {
            Thread.sleep(Duration.ofSeconds(3));
        } catch (InterruptedException ignored) {
        }
        if (!session.getAttributes().containsKey(USER_KEY)) {
            session.sendMessage(new TextMessage("认证超时"));
            session.close(CloseStatus.SESSION_NOT_RELIABLE);
        }
//        while (true) {
//            Thread.sleep(Duration.ofSeconds(10));
//            session.getAttributes().get("");
//        }
    }

    public static boolean userOnline(long uid) {
        return SESSIONS_MAP.containsKey(uid);
    }

    public static void sendUser(long uid, String text) {
        var webSocketSessions = SESSIONS_MAP.get(uid);
        if (!CollectionUtils.isEmpty(webSocketSessions)) {
            var message = new TextMessage(text);
            webSocketSessions.forEach(session -> {
                try {
                    session.sendMessage(message);
                } catch (IOException ignore) {
                }
            });
        }
    }

    public static void sendAll(String text) {
        var message = new TextMessage(text);
        SESSIONS_MAP.values().stream()
                .flatMap(Collection::stream)
                .forEach(session -> {
                    try {
                        session.sendMessage(message);
                    } catch (IOException ignore) {
                    }
                });
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info(JsonUtil.objectToJsonPretty(session.getHandshakeHeaders()));
        LoginUser user;
        if (Objects.nonNull(user = ContextUtil.getContextUser())) {
            session.getAttributes().put(USER_KEY, user);
        } else {
            Thread.startVirtualThread(() -> check(session));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, @NotNull TextMessage message) {
        session.getAttributes().computeIfAbsent(USER_KEY, (s) -> {
            String jwt = message.getPayload();
            if (ObjectUtils.isEmpty(jwt) || !jwt.startsWith("Bearer ")) {
                return null;
            }
            String token = jwt.substring(7);
            var loginUser = JwtUtil.verifyToken(token);
            if (Objects.isNull(loginUser) || !userService.loginCheck(loginUser)) return null;
            SESSIONS_MAP
                    .computeIfAbsent(loginUser.getOsuId(), (key) -> new CopyOnWriteArrayList<>())
                    .add(session);
            return loginUser;
        });
        ContextUtil.getContextUserOptional().ifPresentOrElse((u) -> log.info("user: {}", u.getOsuId()), () -> {
            try {
                session.close();
            } catch (IOException ignored) {
            }
        });
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NotNull CloseStatus status) throws Exception {
        var userId = ((LoginUser) session.getAttributes().get(USER_KEY)).getOsuId();
        var webSocketSessions = SESSIONS_MAP.get(userId);
        if (!CollectionUtils.isEmpty(webSocketSessions)) {
            webSocketSessions.remove(session);
            if (CollectionUtils.isEmpty(webSocketSessions)) SESSIONS_MAP.remove(userId);
        }
        super.afterConnectionClosed(session, status);
    }
}
