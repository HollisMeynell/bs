package l.f.mappool.repository;

import l.f.mappool.entity.MapCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface MapCategoryRepository extends JpaRepository<MapCategory, Integer> {

}
