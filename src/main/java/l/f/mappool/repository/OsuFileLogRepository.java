package l.f.mappool.repository;

import l.f.mappool.entity.OsuFileRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface OsuFileLogRepository extends JpaRepository<OsuFileRecord, Long> {

    @SuppressWarnings("unused")
    Optional<OsuFileRecord> findOsuFileLogBySid(Long sid);
}
