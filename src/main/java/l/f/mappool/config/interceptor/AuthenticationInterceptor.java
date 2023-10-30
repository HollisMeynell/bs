package l.f.mappool.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import l.f.mappool.entity.LoginUser;
import l.f.mappool.exception.HttpTipException;
import l.f.mappool.exception.PermissionException;
import l.f.mappool.service.UserService;
import l.f.mappool.util.ContextUtil;
import l.f.mappool.util.JwtUtil;
import l.f.mappool.util.TokenBucketUtil;
import org.springframework.lang.NonNull;
import org.springframework.util.ObjectUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;
import java.util.Set;

public class AuthenticationInterceptor implements HandlerInterceptor {
    static final private Set<String> PUBLIC_PATH = Set.of("/error", "/swagger-ui");
    private final UserService userService;
    private static final String BOT_KEY = System.getenv("SUPER_KEY");

    public AuthenticationInterceptor(UserService userService) {
        this.userService = userService;
    }

    private static Open getAnnotation(HandlerMethod handlerMethod) {
        // 检查方法上是否有@Open注解
        Open methodAnnotation = handlerMethod.getMethod().getAnnotation(Open.class);
        if (methodAnnotation != null) return methodAnnotation;

        // 检查Controller类上是否有@Open注解
        Open classAnnotation = handlerMethod.getBeanType().getAnnotation(Open.class);
        if (classAnnotation != null) return classAnnotation;

        return null;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            if (request.getHeader("AuthorizationX") != null
                    && BOT_KEY.isBlank()
                    && request.getHeader("AuthorizationX").equals(BOT_KEY)
            ) {
                // 特殊接口
                Open annotation = getAnnotation(handlerMethod);
                return Objects.nonNull(annotation) && annotation.bot();
            }
            if (!TokenBucketUtil.getToken(request.getRemoteAddr(), 60, 1.5)) {
                // 对请求限速
                throw new HttpTipException(429, "Too Many Requests");
            }

            if (handlerMethod.getMethod().getName().equals("proxy") && !TokenBucketUtil.getToken('p' + request.getRemoteAddr(), 20, 0.2)) {
                throw new HttpTipException(429, "Too Many Requests");
            }

            Open methodAnnotation = getAnnotation(handlerMethod);
            if (Objects.nonNull(methodAnnotation)
                    && methodAnnotation.pub()
                    && !methodAnnotation.admin()
            ) {
                // 公开访问，无需验证身份
                return true;
            }

            if (PUBLIC_PATH.contains(request.getRequestURI())) {
                return true;
            }

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
            ContextUtil.setContextUser(loginUser);
            return true;
        }
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
        ContextUtil.clearContext();
    }
}
