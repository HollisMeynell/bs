package l.f.mappool.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import l.f.mappool.vo.DataListVo;
import l.f.mappool.vo.DataVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 异常处理
 */
@Controller
@ControllerAdvice
public class WebExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(WebExceptionHandler.class);
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Object errorHandler(HttpServletRequest request, HttpServletResponse response, Exception exception){
        log.warn("接口异常[{}] : {}", request.getRequestURI(),exception.getMessage(), exception);
        response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
        return new DataVo(500, "请求出现错误", exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public DataListVo validException(HttpServletRequest request, HttpServletResponse response, MethodArgumentNotValidException exception) {
        log.warn("接口异常[{}] : {}", request.getRequestURI(),exception.getMessage(), exception);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        var errList = exception.getFieldErrors().stream().map(r -> '[' + r.getField() + "]: " + r.getDefaultMessage()).toList();
        return new DataListVo().setCode(400).setMessage("参数异常").setData(errList);
    }
}
