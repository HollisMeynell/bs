package l.f.mappool.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class LogException extends RuntimeException{
    int code = 0;
    public LogException(String message) {
        super(message);
    }
    public LogException(String message, int code) {
        super(message);
        this.code = code;
    }
}
