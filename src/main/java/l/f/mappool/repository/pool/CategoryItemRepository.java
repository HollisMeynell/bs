package l.f.mappool.repository.pool;

import l.f.mappool.entity.pool.PoolCategory;
import l.f.mappool.entity.pool.PoolCategoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryItemRepository extends JpaRepository<PoolCategoryItem, Integer> {
    List<PoolCategoryItem> findAllByCategory(PoolCategory g);
}
