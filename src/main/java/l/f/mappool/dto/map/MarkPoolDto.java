package l.f.mappool.dto.map;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

/***
 * 传递计入左侧抽屉的
 */
@Data
public class MarkPoolDto {
    @Range(min = 0, max = Integer.MAX_VALUE, message = "范围异常")
    int poolid;
}
