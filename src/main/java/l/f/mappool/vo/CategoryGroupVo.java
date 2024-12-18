package l.f.mappool.vo;

import l.f.mappool.entity.pool.PoolCategoryGroup;
import l.f.mappool.entity.pool.PoolCategoryItem;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Data
@NoArgsConstructor
public class CategoryGroupVo {
    String name;
    String info;
    Integer color;
    Integer modsOptional;
    Integer modsRequired;
    List<Category> category;

    public record Category(String name, Long bid, Long creater){}

    public CategoryGroupVo(PoolCategoryGroup m){
        name = m.getName();
        info = m.getInfo();
        color = m.getColor();
        modsOptional = m.getModsOptional();
        modsRequired = m.getModsRequired();

        if (CollectionUtils.isEmpty(m.getCategories())) return;
        category = m.getCategories()
                .stream()
                .map(c -> new Category(c.getName(), c.getChosed(), c.getItems()
                        .stream()
                        .filter(i -> i.getChous().equals(c.getChosed()))
                        .map(PoolCategoryItem::getCreaterId)
                        .findAny()
                        .orElse(0L))
                )
                .toList();
    }


}
