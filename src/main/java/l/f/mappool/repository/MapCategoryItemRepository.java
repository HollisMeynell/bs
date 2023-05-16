package l.f.mappool.repository;

import l.f.mappool.entity.MapCategory;
import l.f.mappool.entity.MapCategoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MapCategoryItemRepository extends JpaRepository<MapCategoryItem, Integer> {
    List<MapCategoryItem> findAllByCategory(MapCategory g);
}
