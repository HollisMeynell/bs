package l.f.mappool.repository;


import l.f.mappool.entity.MapCategoryGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface MapCategoryGroupRepository extends JpaRepository<MapCategoryGroup, Integer> {

}