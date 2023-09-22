package l.f.mappool.repository.pool;

import l.f.mappool.entity.pool.Pool;
import l.f.mappool.entity.pool.PoolUser;
import l.f.mappool.enums.PoolPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public interface PoolUserRepository extends JpaRepository<PoolUser, Integer> {
    long deleteByPool(Pool pool);

    @Transactional
    @Query(value = "select u from PoolUser u where u.userId=:userId and u.pool.id=:poolId")
    Optional<PoolUser> getMapPoolUserByPoolIdAndUserId(int poolId, long userId);

    @Query("select u.permission from PoolUser u where u.userId=:userId and u.pool.id=:poolId")
    Optional<PoolPermission> getMapPoolUserPermission(int poolId, long userId);

    @Transactional
    @Query(value = "select u.permission from PoolUser u where u.userId=:userId and u.pool in (select g.pool from PoolCategoryGroup g where g.id=:groupId)")
    Optional<PoolPermission> getMapPoolUserPermissionByGroupIdAndUserId(int groupId, long userId);
    @Transactional
    @Query(value = "select u from PoolUser u where u.userId=:userId and u.pool in (select g.group.pool from PoolCategory g where g.id=:categoryId)")
    Optional<PoolUser> getMapPoolUserByCategoryIdAndUserId(int categoryId, long userId);

    List<PoolUser> searchAllByUserId(long userId);

    @Transactional
    @Query("select count(distinct u) from PoolUser u where u.permission=l.f.mappool.enums.PoolPermission.CREATE")
    Integer getUserCreatedSize(long userId);

}
