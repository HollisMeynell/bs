package l.f.mappool.dto;

import l.f.mappool.dto.validator.favorite.*;
import l.f.mappool.dto.validator.mapPool.CreateCategory;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class FavoriteDto {
    @NotNull(groups = {
            UpdateFavorite.class, UpdateTags.class, DeleteFavorite.class,
            AddTag.class, DeleteTag.class, ReplaceTag.class,
    })
    Integer id;
    @NotNull(groups = {CreateCategory.class})
    Long bid;
    @NotEmpty(groups = {CreateCategory.class, UpdateFavorite.class})
    String info;

    /**
     * ReplaceTags 将 tags[0] 替换为 tags[1]
     */
    @NotEmpty(groups = {GetByTags.class, UpdateTags.class, ReplaceTag.class})
    String[] tags;
    @NotNull(groups = {AddTag.class, DeleteTag.class})
    String tag;
}
