package l.f.mappool.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 作为提示使用, 不会记录日志
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class HttpTipException extends RuntimeException {
    int code = 0;

    public HttpTipException(String message) {
        super(message);
    }

    public HttpTipException(String message, int code) {
        super(message);
        this.code = code;
    }

    public HttpTipException(int code, String message) {
        super(message);
        this.code = code;
    }
}
