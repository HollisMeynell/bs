package l.f.mappool.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import l.f.mappool.config.interceptor.Open;
import l.f.mappool.entity.LoginUser;
import l.f.mappool.exception.PermissionException;
import l.f.mappool.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Slf4j
public class WebUtil {
    private static final Set<String> ORIGIN_ALLOW = new HashSet<>();
    private static final String BOT_KEY;
    static private final String CORS_KEY;

    static {
        BOT_KEY = System.getenv("SUPER_KEY");
        CORS_KEY = System.getenv("CORS_KEY");
        ORIGIN_ALLOW.add("https://bot.365246692.xyz");
        ORIGIN_ALLOW.add("https://bot.v6.365246692.xyz:88");
        ORIGIN_ALLOW.add("https://docs.365246692.xyz");
        ORIGIN_ALLOW.add("https://docs.v6.365246692.xyz:88");
        ORIGIN_ALLOW.add("http://localhost:5173");
        ORIGIN_ALLOW.add("https://siyuyuko.github.io");
        ORIGIN_ALLOW.add("https://a.yasunaori.be");
    }

    public static boolean checkBot(Open annotation, HttpServletRequest request) {
        return request.getHeader("AuthorizationX") != null &&
                StringUtils.hasLength(BOT_KEY) &&
                request.getHeader("AuthorizationX").equals(BOT_KEY) &&
                Objects.nonNull(annotation) &&
                annotation.bot();
    }

    public static boolean limitRequest(HttpServletRequest request, HandlerMethod handlerMethod) {
        if (!TokenBucketUtil.getToken(request.getRemoteAddr(), 120, 30)) {
            return true;
        }

        if (handlerMethod.getMethod().getName().equals("proxy") && !TokenBucketUtil.getToken('p' + request.getRemoteAddr(), 20, 0.2)) {
            return true;
        }

        return false;
    }

    public static void originAllow(HttpServletRequest request, HttpServletResponse response) {
        String key;
        if (Objects.nonNull(key = request.getParameter("key")) && key.equals(CORS_KEY) ||
                Objects.nonNull((key = request.getHeader("key"))) && key.equals(CORS_KEY)) {
            WebUtil.setOriginAllow(response);
        }
    }

    public static boolean isPublic(Open methodAnnotation) {
        return Objects.nonNull(methodAnnotation)
                && methodAnnotation.pub()
                && !methodAnnotation.admin();
    }

    public static void permission(HttpServletRequest request, UserService userService, Open methodAnnotation) {
        String header = request.getHeader("Authorization");
        if (ObjectUtils.isEmpty(header) || !header.startsWith("Bearer ")) {
            throw new PermissionException();
        }
        String token = header.substring(7);
        LoginUser loginUser = JwtUtil.verifyToken(token);
        if (ObjectUtils.isEmpty(loginUser) || !userService.loginCheck(loginUser)) {
            throw new PermissionException();
        }
        // 是否为后台管理员
        if (!loginUser.isAdmin() && Objects.nonNull(methodAnnotation) && methodAnnotation.admin()) {
            throw new PermissionException();
        }
    }

    public static void setOriginAllow(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        if (ORIGIN_ALLOW.contains(origin)) {
            setOriginAllow(response, origin);
        }
    }

    public static void setOriginAllow(HttpServletResponse response) {
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET");
    }

    public static void setOriginAllow(HttpServletResponse response, String allow) {
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, allow);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET");
    }
}
