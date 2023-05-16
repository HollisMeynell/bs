package l.f.mappool.repository;

import l.f.mappool.entity.MapCategory;
import l.f.mappool.entity.MapCategoryGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public interface MapCategoryRepository extends JpaRepository<MapCategory, Integer> {
    @Transactional
    @Query("select g from MapCategoryGroup g where g.pool.id=:poolId")
    List<MapCategoryGroup> getAllCategory(int poolId);
    List<MapCategory> getAllByGroup(MapCategoryGroup g);
}
