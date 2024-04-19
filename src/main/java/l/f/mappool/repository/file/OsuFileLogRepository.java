package l.f.mappool.repository.file;

import jakarta.transaction.Transactional;
import l.f.mappool.entity.file.OsuFileRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
@Component
public interface OsuFileLogRepository extends JpaRepository<OsuFileRecord, Long> {

    List<OsuFileRecord> findOsuFileLogBySid(Long sid);

    Optional<OsuFileRecord> findOsuFileRecordByBid(Long bid);

    @Transactional
    void deleteAllBySid(long sid);

    @Query("select count(osu)from OsuFileRecord osu")
    int countAll();

    @Query("select count(distinct osu.sid)from OsuFileRecord osu")
    int countBySid();
}
