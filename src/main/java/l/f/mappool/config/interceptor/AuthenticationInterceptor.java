package l.f.mappool.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import l.f.mappool.entity.User;
import l.f.mappool.service.UserService;
import l.f.mappool.util.ContextUtil;
import l.f.mappool.util.JwtUtil;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

public class AuthenticationInterceptor implements HandlerInterceptor {
    static final private Set<String> PUBLIC_PATH = Set.of("/error", "/swagger-ui");
    private final UserService userService;

    public AuthenticationInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            // 检查方法上是否有@Open注解
            Open methodAnnotation = handlerMethod.getMethod().getAnnotation(Open.class);
            if (methodAnnotation != null) {
                return true; // 公开访问，无需验证身份
            }

            // 检查Controller类上是否有@Open注解
            Open classAnnotation = handlerMethod.getBeanType().getAnnotation(Open.class);
            if (classAnnotation != null) {
                return true; // 公开访问，无需验证身份
            }

            if (PUBLIC_PATH.contains(request.getRequestURI())) {
                return true;
            }

            String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                throw new RuntimeException("no login");
            }
            String token = header.substring(7);
            User user = JwtUtil.verifyToken(token);
            if (user == null || !userService.loginCheck(user)) {
                throw new RuntimeException("身份验证失败,尝试重新登陆");
            }
            ContextUtil.setContextUser(user);
            return true;
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
        ContextUtil.clearContext();
    }
}
