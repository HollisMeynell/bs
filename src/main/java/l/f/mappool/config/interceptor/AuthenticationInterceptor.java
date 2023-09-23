package l.f.mappool.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import l.f.mappool.entity.User;
import l.f.mappool.exception.HttpError;
import l.f.mappool.service.UserService;
import l.f.mappool.util.ContextUtil;
import l.f.mappool.util.JwtUtil;
import l.f.mappool.util.TokenBucketUtil;
import org.springframework.lang.NonNull;
import org.springframework.util.ObjectUtils;
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
    public boolean preHandle(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull Object handler) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            if (!TokenBucketUtil.getToken(request.getRemoteAddr(), 60, 1.5)) {
                throw new HttpError(429, "Too Many Requests");
            }

            if (handlerMethod.getMethod().getName().equals("proxy") && !TokenBucketUtil.getToken('p' + request.getRemoteAddr(), 20, 0.2)) {
                throw new HttpError(429, "Too Many Requests");
            }

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
            if (ObjectUtils.isEmpty(header) || !header.startsWith("Bearer ")) {
                throw new HttpError(401, "no login");
            }
            String token = header.substring(7);
            User user = JwtUtil.verifyToken(token);
            if (ObjectUtils.isEmpty(user) || !userService.loginCheck(user)) {
                throw new HttpError(401, "身份验证失败,尝试重新登陆");
            }
            ContextUtil.setContextUser(user);
            return true;
        }
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
        ContextUtil.clearContext();
    }
}
