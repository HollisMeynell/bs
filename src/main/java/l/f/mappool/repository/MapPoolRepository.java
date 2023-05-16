package l.f.mappool.repository;

import l.f.mappool.entity.MapPool;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public interface MapPoolRepository extends JpaRepository<MapPool, Integer> {

    @Transactional
//    @Query(value = "select o from MapPool o join MapPoolUser u on o.id = u.poolId order by u.permission desc ")
    @Query(value = "select distinct u.pool from MapPoolUser u where u.userId=:userId and u.pool.status != l.f.mappool.enums.PoolStatus.DELETE")
    List<MapPool> searchAllByUserId(long userId);

    @Query("select p from MapPool p where p.status = l.f.mappool.enums.PoolStatus.SHOW")
    List<MapPool> getAllOpenPool();

    @Query("select distinct u.pool from MapPoolUser u where u.userId!=:userId and u.pool.status=l.f.mappool.enums.PoolStatus.SHOW")
    List<MapPool> getAllMapPoolOpenPoolExcludeUser(long userId);

    @Query("select count(p) from MapPool p where p.status=l.f.mappool.enums.PoolStatus.SHOW and p.name like %:name%")
    int countByName(String name);

    @Query("select p from MapPool p where p.status=l.f.mappool.enums.PoolStatus.SHOW and p.name like %:name%")
    List<MapPool> queryByName(String name, Pageable pageable);

    @Query("select p from MapPool p where p.id in (select m.pid from MapPoolMark4User m where m.uid=:uid)")
    List<MapPool> queryByUserMark(long uid);
}
