package l.f.mappool.repository;

import l.f.mappool.entity.OsuFileLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface OsuFileLogRepository extends JpaRepository<OsuFileLog, Long> {

    @SuppressWarnings("unused")
    Optional<OsuFileLog> findOsuFileLogBySid(Long sid);
}
