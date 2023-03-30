package l.f.mappool.repository;

import l.f.mappool.entity.MapPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public interface MapPoolRepository extends JpaRepository<MapPool, Integer> {

    @Transactional
    @Query(value = "select o from MapPool o join MapPoolUser u on o.id = u.poolId order by u.permission desc ")
    List<MapPool> searchAllByUserId(long userId);
}
