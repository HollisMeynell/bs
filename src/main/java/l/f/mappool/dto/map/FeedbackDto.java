package l.f.mappool.dto.map;

import l.f.mappool.dto.validator.mapPool.CreateFeedback;
import l.f.mappool.dto.validator.mapPool.DeleteFeedback;
import l.f.mappool.dto.validator.mapPool.HandleFeedback;
import l.f.mappool.dto.validator.mapPool.SetFeedback;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Data
public class FeedbackDto {
    @NotNull(message = "itemId 不能为空", groups = {CreateFeedback.class})
    @Range(min = 0, max = Integer.MAX_VALUE, message = "范围异常")
    Integer itemId;
    @NotNull(message = "id 不能为空", groups = {SetFeedback.class, DeleteFeedback.class, HandleFeedback.class})
    @Range(min = 0, max = Integer.MAX_VALUE, message = "范围异常")
    Integer id;
    @NotNull(message = "状态不能为空", groups = {HandleFeedback.class})
    Boolean handle;

    Boolean agree;
    String feedback;
}
