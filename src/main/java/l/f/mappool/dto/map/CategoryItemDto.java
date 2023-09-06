package l.f.mappool.dto.map;

import l.f.mappool.dto.validator.mapPool.CreateCategoryItem;
import l.f.mappool.dto.validator.mapPool.DeleteCategoryItem;
import l.f.mappool.dto.validator.mapPool.SetCategoryItem;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CategoryItemDto {
    @NotNull(message = "类别不能为空",
            groups = {CreateCategoryItem.class, })
    Integer categoryId;
    @NotNull(message = "id不能为空",
            groups = {SetCategoryItem.class, DeleteCategoryItem.class})
    Integer itemId;
    @NotNull(message = "谱面不能为空",
            groups = {CreateCategoryItem.class, SetCategoryItem.class})
    @Min(message = "范围异常", value = 1000)
    Long beatmapId;
    @NotEmpty(message = "介绍不能为空",
            groups = {CreateCategoryItem.class, SetCategoryItem.class})
    String info;

    @NotEmpty(message = "序号不能为空",
            groups = {SetCategoryItem.class, })
    Integer sort;
}
