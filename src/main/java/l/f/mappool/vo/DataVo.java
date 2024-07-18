package l.f.mappool.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DataVo<T> {
    int code = 200;
    String message = "ok";
    T data;

    public DataVo() {
    }

    public DataVo(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public DataVo(String message) {
        this.message = message;
    }

    public DataVo(T data) {
        this.data = data;
    }

    public DataVo(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public DataVo(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
