package l.f.mappool.repository;

import l.f.mappool.entity.MapPool;
import l.f.mappool.entity.MapPoolUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public interface MapPoolUserRepository extends JpaRepository<MapPoolUser, Integer> {
    long deleteByPool(MapPool pool);

    @Transactional
    @Query(value = "select u from MapPoolUser u where u.userId=:userId and u.pool.id=:poolId ")
    Optional<MapPoolUser> getMapPoolUserByPoolIdAndUserId(int poolId, long userId);

    @Transactional
    @Query(value = "select u from MapPoolUser u where u.userId=:userId and u.pool in (select g.pool from MapCategoryGroup g where g.id=:groupId)")
    Optional<MapPoolUser> getMapPoolUserByGroupIdAndUserId(int groupId, long userId);
    @Transactional
    @Query(value = "select u from MapPoolUser u where u.userId=:userId and u.pool in (select g.group.pool from MapCategory g where g.id=:categoryId)")
    Optional<MapPoolUser> getMapPoolUserByCategoryIdAndUserId(int categoryId, long userId);

    List<MapPoolUser> searchAllByUserId(long userId);

    @Transactional
    @Query("select count(distinct u) from MapPoolUser u where u.permission=l.f.mappool.enums.PoolPermission.CREATE")
    Integer getUserCreatedSize(long userId);

}
