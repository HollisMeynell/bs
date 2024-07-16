package l.f.mappool.repository.file;

import jakarta.transaction.Transactional;
import l.f.mappool.entity.file.OsuFileRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
@Component
public interface OsuFileLogRepository extends JpaRepository<OsuFileRecord, Long> {
    @Query("select r from OsuFileRecord r where r.bid = :id or r.sid = :id")
    Page<OsuFileRecord> queryFileById(Long id, Pageable page);

    Page<OsuFileRecord> queryByFileContainingIgnoreCase(String key, Pageable page);

    List<OsuFileRecord> findOsuFileLogBySid(Long sid);

    Optional<OsuFileRecord> findOsuFileRecordByBid(Long bid);

    @Transactional
    void deleteAllBySid(long sid);

    @Query("select osu.sid from OsuFileRecord osu where osu.bid = :bid")
    Optional<Long> querySidByBid(Long bid);

    @Query("select count(osu)from OsuFileRecord osu")
    int countAll();

    @Query("select count(distinct osu.sid)from OsuFileRecord osu")
    int countBySid();


}
