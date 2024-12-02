package l.f.mappool.repository.file;

import l.f.mappool.entity.file.OsuFileRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
@Component
public interface OsuFileLogRepository extends JpaRepository<OsuFileRecord, Long> {
    @Query("select r from OsuFileRecord r")
    Page<OsuFileRecord> queryAll(Pageable page);

    @Query("select r from OsuFileRecord r where r.bid = :id or r.sid = :id")
    List<OsuFileRecord> queryFileById(Long id);

    Page<OsuFileRecord> queryByFileContainingIgnoreCase(String key, Pageable page);

    List<OsuFileRecord> findOsuFileLogBySid(Long sid);

    Optional<OsuFileRecord> findOsuFileRecordByBid(Long bid);

    @Query("select osu from OsuFileRecord osu where osu.status in (-2, -1, 0) order by osu.last limit :size")
    List<OsuFileRecord> queryByGraveyard(Integer size);

    @Transactional()
    void deleteAllBySid(long sid);

    @Query("select osu.sid from OsuFileRecord osu where osu.bid = :bid")
    Optional<Long> querySidByBid(Long bid);

    @Query("select count(osu)from OsuFileRecord osu")
    int countAll();

    @Query("select count(distinct osu.sid)from OsuFileRecord osu")
    int countBySid();


}
