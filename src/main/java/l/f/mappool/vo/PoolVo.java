package l.f.mappool.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import l.f.mappool.entity.pool.Pool;
import l.f.mappool.entity.pool.PoolCategoryGroup;
import l.f.mappool.enums.PoolStatus;
import l.f.mappool.exception.HttpError;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.Comparator;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("unused")
public class PoolVo extends Pool {
    public PoolVo(){}
    public PoolVo(Pool p) {
        BeanUtils.copyProperties(p, this, "users", "groups");
        categoryList = p.getGroups()
                .stream()
                .sorted(Comparator.comparing(PoolCategoryGroup::getSort))
                .map(CategoryGroupVo::new)
                .toList();
    }
    List<CategoryGroupVo> categoryList;
}
