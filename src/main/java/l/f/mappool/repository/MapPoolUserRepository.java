package l.f.mappool.repository;

import l.f.mappool.entity.MapPoolUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public interface MapPoolUserRepository extends JpaRepository<MapPoolUser, Integer> {

    @Transactional
    @Query(value = "select o from MapPoolUser o where o.poolId=:poolId and o.userId=:userId")
    Optional<MapPoolUser> getMapPoolUserByPoolIdAndUserId(int poolId, long userId);

    List<MapPoolUser> searchAllByUserId(long userId);
}
