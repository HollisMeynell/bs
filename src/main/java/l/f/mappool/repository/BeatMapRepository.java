package l.f.mappool.repository;

import l.f.mappool.entity.BeatMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface BeatMapRepository extends JpaRepository<BeatMap, Long> {
    Optional<BeatMap> findBeatMapById(long id);
}