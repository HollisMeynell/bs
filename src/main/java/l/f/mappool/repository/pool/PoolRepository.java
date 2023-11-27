package l.f.mappool.repository.pool;

import l.f.mappool.entity.pool.Pool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@SuppressWarnings("unused")
public interface PoolRepository extends JpaRepository<Pool, Integer> {

    @Transactional
//    @Query(value = "select o from MapPool o join MapPoolUser u on o.id = u.poolId order by u.permission desc ")
    @Query(value = "select distinct u.pool from PoolUser u where u.userId=:userId and u.pool.status != l.f.mappool.enums.PoolStatus.DELETE")
    List<Pool> searchAllByUserId(long userId);

    @Query("select p from Pool p where p.status = l.f.mappool.enums.PoolStatus.SHOW")
    List<Pool> getAllOpenPool();

    @Transactional
    @Query("select p from Pool p where p.status = l.f.mappool.enums.PoolStatus.SHOW")
    Page<Pool> getAllOpenPool(Pageable pageable);

    @Query("select distinct u.pool from PoolUser u where u.userId!=:userId and u.pool.status=l.f.mappool.enums.PoolStatus.SHOW")
    List<Pool> getAllMapPoolOpenPoolExcludeUser(long userId);

    @Query("select count(p) from Pool p where (p.status=l.f.mappool.enums.PoolStatus.SHOW or p.id in (select distinct u.pool.id from PoolUser u where u.userId=:userId and u.pool.status != l.f.mappool.enums.PoolStatus.DELETE)) and p.name like %:name%")
    int countByName(String name, long userId);

    @Query("select p from Pool p where (p.status=l.f.mappool.enums.PoolStatus.SHOW or p.id in (select distinct u.pool.id from PoolUser u where u.userId=:userId and u.pool.status != l.f.mappool.enums.PoolStatus.DELETE)) and p.name like %:name%")
    List<Pool> queryByName(String name, long userId, Pageable pageable);

    @Query("select p from Pool p where p.id in (select m.pid from PoolMark4User m where m.uid=:uid) and p.status != l.f.mappool.enums.PoolStatus.DELETE and p.status != l.f.mappool.enums.PoolStatus.SHOW")
    List<Pool> queryByUserMark(long uid);

    @Query("select count(p) from Pool p where p.status!=l.f.mappool.enums.PoolStatus.DELETE and p.name like :name")
    int hasPool(String name);

    @Query("select count(p) from Pool p where p.status!=l.f.mappool.enums.PoolStatus.DELETE and p.id=:id")
    int getCountById(int id);

    @Query("select p from Pool p where p.status!=l.f.mappool.enums.PoolStatus.DELETE and p.id=:id")
    Optional<Pool> getByIdNotDelete(int id);
    Optional<Pool> getById(int id);
}
