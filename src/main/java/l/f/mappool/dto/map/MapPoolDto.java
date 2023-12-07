package l.f.mappool.dto.map;

import l.f.mappool.dto.validator.mapPool.*;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class MapPoolDto {
    @NotEmpty(message = "名称不能为空",
            groups = {CreatePool.class, CreateCategoryGroup.class, CreateCategory.class, SetPool.class, SetCategoryGroup.class, SetCategory.class})
    String name;
    @NotEmpty(message = "横幅背景不能为空", groups = {CreatePool.class, SetPool.class})
    String banner;
    @NotEmpty(message = "介绍不能为空", groups = {CreatePool.class, CreateCategoryGroup.class, SetPool.class, SetCategoryGroup.class})
    String info;
    @NotNull(message = "id 不能为空",groups = {CreateCategoryGroup.class, SetPool.class, DeletePool.class, SetCategoryGroup.class})
    @Range(min = 0, max = Integer.MAX_VALUE, message = "范围异常")
    Integer poolId;
    @NotNull(message = "id 不能为空",groups = {CreateCategory.class, SetCategoryGroup.class, DeleteCategoryGroup.class})
    @Range(min = 0, max = Integer.MAX_VALUE, message = "范围异常")
    Integer groupId;
    @NotNull(message = "id 不能为空",groups = {SetCategory.class, DeleteCategory.class})
    @Range(min = 0, max = Integer.MAX_VALUE, message = "范围异常")
    Integer categoryId;
    @NotNull(message = "主体色不能为空" , groups = {CreateCategoryGroup.class, SetCategoryGroup.class})
    Integer color;
    @NotNull(message = "位序不能为空" , groups = {SetCategoryGroup.class})
    Integer sort;
    @NotEmpty(message = "模式不能为空", groups = {CreatePool.class, SetPool.class})
    @Range(min = 0, max = 4, message = "mode 只允许输入 0-3")
    Integer mode;
    Integer modOptional;
    Integer modRequired;
}
