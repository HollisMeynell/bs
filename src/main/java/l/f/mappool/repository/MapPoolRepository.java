package l.f.mappool.repository;

import l.f.mappool.entity.MapPool;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@SuppressWarnings("unused")
public interface MapPoolRepository extends JpaRepository<MapPool, Integer> {

    @Transactional
//    @Query(value = "select o from MapPool o join MapPoolUser u on o.id = u.poolId order by u.permission desc ")
    @Query(value = "select distinct u.pool from MapPoolUser u where u.userId=:userId and u.pool.status != l.f.mappool.enums.PoolStatus.DELETE")
    List<MapPool> searchAllByUserId(long userId);

    @Query("select p from MapPool p where p.status = l.f.mappool.enums.PoolStatus.SHOW")
    List<MapPool> getAllOpenPool();

    @Query("select distinct u.pool from MapPoolUser u where u.userId!=:userId and u.pool.status=l.f.mappool.enums.PoolStatus.SHOW")
    List<MapPool> getAllMapPoolOpenPoolExcludeUser(long userId);

    @Query("select count(p) from MapPool p where (p.status=l.f.mappool.enums.PoolStatus.SHOW or p.id in (select distinct u.pool.id from MapPoolUser u where u.userId=:userId and u.pool.status != l.f.mappool.enums.PoolStatus.DELETE)) and p.name like %:name%")
    int countByName(String name, long userId);

    @Query("select p from MapPool p where (p.status=l.f.mappool.enums.PoolStatus.SHOW or p.id in (select distinct u.pool.id from MapPoolUser u where u.userId=:userId and u.pool.status != l.f.mappool.enums.PoolStatus.DELETE)) and p.name like %:name%")
    List<MapPool> queryByName(String name, long userId, Pageable pageable);

    @Query("select p from MapPool p where p.id in (select m.pid from MapPoolMark4User m where m.uid=:uid)")
    List<MapPool> queryByUserMark(long uid);

    @Query("select count(p) from MapPool p where p.status!=l.f.mappool.enums.PoolStatus.DELETE and p.name like :name")
    int hasPool(String name);

    @Query("select count(p) from MapPool p where p.status!=l.f.mappool.enums.PoolStatus.DELETE and p.id=:id")
    int getCountById(int id);

    @Query("select p from MapPool p where p.status!=l.f.mappool.enums.PoolStatus.DELETE and p.id=:id")
    Optional<MapPool> getByIdNotDelete(int id);
    Optional<MapPool> getById(int id);
}
