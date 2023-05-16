package l.f.mappool.dto.map;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;

@Data
public class CategoryItemDto {
    @NotNull(message = "类别不能为空")
    int categoryId;
    @NotNull(message = "谱面不能为空")
    @Min(message = "范围异常", value = 1000)
    long beatmapId;
    @NotEmpty(message = "介绍不能为空")
    String info;
}
