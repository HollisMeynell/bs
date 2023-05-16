package l.f.mappool.repository;

import l.f.mappool.entity.MapFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface MapFeedbackRepository extends JpaRepository<MapFeedback, Integer> {

}
