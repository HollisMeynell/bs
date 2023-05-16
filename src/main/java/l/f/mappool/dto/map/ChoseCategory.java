package l.f.mappool.dto.map;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Data
public class ChoseCategory {
    @NotNull(message = "categoryId 不能为空")
    @Range(min = 0, max = Integer.MAX_VALUE, message = "范围异常")
    Integer categoryId;
    Long bid;
}
