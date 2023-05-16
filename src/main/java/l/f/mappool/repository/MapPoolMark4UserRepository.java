package l.f.mappool.repository;

import jakarta.transaction.Transactional;
import l.f.mappool.entity.MapPoolMark4User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MapPoolMark4UserRepository extends JpaRepository<MapPoolMark4User, Long> {
    @Transactional
    @Modifying
    @Query("delete from MapPoolMark4User b where b.uid=:uid and b.pid=:pid")
    int deleteAllByUidaAndPid(long uid, int pid);
}