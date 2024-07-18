package l.f.mappool.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class DataListVo<T> {
    int code = 200;
    String message = "ok";

    private int totalPages = 1;
    private long totalItems = 1;
    private int currentPage = 1;
    private int pageSize;

    private List<T> data;
}
