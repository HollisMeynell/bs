package l.f.mappool.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import l.f.mappool.exception.HttpError;
import l.f.mappool.exception.HttpTipException;
import l.f.mappool.vo.DataListVo;
import l.f.mappool.vo.DataVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 异常处理
 */
@Slf4j
@Controller
@ControllerAdvice
@SuppressWarnings("unused")
public class WebExceptionHandler {
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Object errorHandler(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        log.warn("接口异常[{}] : {}", request.getRequestURI(), exception.getMessage(), exception);
        response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
        return new DataVo<>(500, "请求出现错误", exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = NoResourceFoundException.class)
    public Object resourceNotFoundHandler(HttpServletRequest request, HttpServletResponse response, NoResourceFoundException exception) {
        log.warn("资源/接口无效[{}] : {}", request.getRequestURI(), exception.getMessage());
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return new DataVo<>(404, "访问路径不存在", exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = RuntimeException.class)
    public Object errorRuntimeHandler(HttpServletRequest request, HttpServletResponse response, RuntimeException exception) {
        log.warn("接口异常[{}] : {}", request.getRequestURI(), exception.getMessage(), exception);
        response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
        return new DataVo<>(500, exception.getMessage(), exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = HttpTipException.class)
    public Object logErrorHandler(HttpServletRequest request, HttpServletResponse response, HttpTipException exception) {
        int code = exception.getCode() == 0 ? HttpServletResponse.SC_BAD_GATEWAY : exception.getCode();
        response.setStatus(code);
        return new DataVo<>(code, exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = HttpError.class)
    public Object httpErrorHandler(HttpServletRequest request, HttpServletResponse response, HttpError error) {
        log.warn("请求错误 [{}] : {}", request.getRequestURI(), error.getMessage(), error);
        response.setStatus(error.getCode());
        return new DataVo<>(error.getCode(), "请求出现错误", error.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public DataListVo<String> validException(HttpServletRequest request, HttpServletResponse response, MethodArgumentNotValidException exception) {
        log.warn("接口异常[{}] : {}", request.getRequestURI(), exception.getMessage(), exception);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        var errList = exception.getFieldErrors().stream().map(r -> '[' + r.getField() + "]: " + r.getDefaultMessage()).toList();
        return new DataListVo<String>().setCode(403).setMessage("参数异常").setData(errList);
    }
}
