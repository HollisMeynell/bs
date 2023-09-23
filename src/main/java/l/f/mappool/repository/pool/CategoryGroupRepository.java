package l.f.mappool.repository.pool;


import l.f.mappool.entity.pool.PoolCategoryGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface CategoryGroupRepository extends JpaRepository<PoolCategoryGroup, Integer> {

}