package l.f.mappool.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Aspect
@Component
public class AspectConfig {
    public static final AtomicLong ERROR_COUNT = new AtomicLong();

    @Pointcut("execution(* l.f.mappool.config.interceptor.WebExceptionHandler.*(jakarta.servlet.http.HttpServletRequest, ..))")
    public void errorHandle() {
    }


    @After(value = "errorHandle()")
    public void countError() {
        ERROR_COUNT.addAndGet(1);
    }
}
