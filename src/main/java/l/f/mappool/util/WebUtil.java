package l.f.mappool.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import l.f.mappool.config.interceptor.Open;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

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
    }

    public static boolean checkBot(Open annotation, HttpServletRequest request) {
        return request.getHeader("AuthorizationX") != null &&
                StringUtils.hasLength(BOT_KEY) &&
                request.getHeader("AuthorizationX").equals(BOT_KEY) &&
                Objects.nonNull(annotation) &&
                annotation.bot();
    }

    public static void setOriginAllow(HttpServletResponse response, HttpServletRequest request) {
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
