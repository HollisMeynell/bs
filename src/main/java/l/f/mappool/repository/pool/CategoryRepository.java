package l.f.mappool.repository.pool;

import l.f.mappool.entity.pool.PoolCategory;
import l.f.mappool.entity.pool.PoolCategoryGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public interface CategoryRepository extends JpaRepository<PoolCategory, Integer> {
    @Transactional
    @Query("select g from PoolCategoryGroup g where g.pool.id=:poolId")
    List<PoolCategoryGroup> getAllCategory(int poolId);
    List<PoolCategory> getAllByGroup(PoolCategoryGroup g);
}
