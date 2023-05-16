package l.f.mappool.repository;

import l.f.mappool.entity.BeatMapSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface BeatMapSetRepository extends JpaRepository<BeatMapSet, Long> {

}
