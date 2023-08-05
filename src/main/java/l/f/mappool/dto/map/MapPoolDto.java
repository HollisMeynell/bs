package l.f.mappool.dto.map;

import l.f.mappool.dto.validator.mapPool.CreateCategory;
import l.f.mappool.dto.validator.mapPool.CreateCategoryGroup;
import l.f.mappool.dto.validator.mapPool.CreatePool;
import l.f.mappool.dto.validator.mapPool.SetPool;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import javax.validation.constraints.*;

@Data
public class MapPoolDto {
    @NotEmpty(message = "名称不能为空",
            groups = {CreatePool.class, CreateCategoryGroup.class, CreateCategory.class, SetPool.class})
    String name;
    @NotEmpty(message = "横幅背景不能为空", groups = {CreatePool.class, SetPool.class})
    String banner;
    @NotEmpty(message = "介绍不能为空", groups = {CreatePool.class, CreateCategoryGroup.class, SetPool.class})
    String info;
    @NotNull(message = "id 不能为空",groups = {CreateCategoryGroup.class, SetPool.class})
    @Range(min = 0, max = Integer.MAX_VALUE, message = "范围异常")
    Integer poolId;
    @NotNull(message = "id 不能为空",groups = {CreateCategory.class})
    @Range(min = 0, max = Integer.MAX_VALUE, message = "范围异常")
    Integer groupId;
    @NotNull(message = "主体色不能为空" , groups = {CreateCategoryGroup.class})
    Integer color;
}
