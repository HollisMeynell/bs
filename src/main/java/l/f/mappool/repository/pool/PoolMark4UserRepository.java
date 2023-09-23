package l.f.mappool.repository.pool;

import jakarta.transaction.Transactional;
import l.f.mappool.entity.pool.PoolMark4User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PoolMark4UserRepository extends JpaRepository<PoolMark4User, Long> {
    @Transactional
    @Modifying
    @Query("delete from PoolMark4User b where b.uid=:uid and b.pid=:pid")
    int deleteAllByUidaAndPid(long uid, int pid);

    Optional<PoolMark4User> queryMapPoolMark4UserByUidAndPid(long uid, int pid);
}