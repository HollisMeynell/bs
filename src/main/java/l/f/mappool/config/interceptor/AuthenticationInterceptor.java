package l.f.mappool.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import l.f.mappool.exception.HttpTipException;
import l.f.mappool.service.UserService;
import l.f.mappool.util.ContextUtil;
import l.f.mappool.util.WebUtil;
import org.springframework.lang.NonNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;
import java.util.Set;

public class AuthenticationInterceptor implements HandlerInterceptor {
    static final private Set<String> PUBLIC_PATH = Set.of("/error", "/swagger-ui");
    private final UserService userService;

    public AuthenticationInterceptor(UserService userService) {
        this.userService = userService;
    }

    private static Open getAnnotation(HandlerMethod handlerMethod) {
        // 检查方法上是否有@Open注解
        Open methodAnnotation = handlerMethod.getMethod().getAnnotation(Open.class);
        if (Objects.nonNull(methodAnnotation)) return methodAnnotation;

        // 检查Controller类上是否有@Open注解
        Open classAnnotation = handlerMethod.getBeanType().getAnnotation(Open.class);
        if (Objects.nonNull(classAnnotation)) return classAnnotation;

        return null;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            Open methodAnnotation = getAnnotation(handlerMethod);
            if (WebUtil.checkBot(methodAnnotation, request)) {
                return true;
            }

            if (WebUtil.limitRequest(request, handlerMethod)) {
                // 对请求限速
                throw new HttpTipException(429, "Too Many Requests");
            }

            WebUtil.originAllow(request, response);

            if (WebUtil.isPublic(methodAnnotation) || PUBLIC_PATH.contains(request.getRequestURI())) {
                // 公开访问，无需验证身份
                return true;
            }

            // 权限检查
            WebUtil.permission(request, userService, methodAnnotation);
        }
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
        ContextUtil.clearContext();
    }
}
