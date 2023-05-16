package l.f.mappool.repository;

import l.f.mappool.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public interface NotationRepository extends JpaRepository<Favorite, Integer>, JpaSpecificationExecutor<Integer> {
    //select * from notation where :serchText like any(column)
    //select DISTINCT unnest(notation.tags) as all from notation where userid=
    //SELECT DISTINCT value FROM Notation e, IN(e.tags) value
    @Transactional
    @Query(value = "select distinct unnest(notation.tags) as all from notation where user_id = :uid", nativeQuery = true)
    List<String> allUserTags(Long uid);

    @Transactional
    @Query(value = "select * from notation where :tag like any(notation.tags)", nativeQuery = true)
    List<Favorite> searchAllByTags(String tag);
    Optional<Favorite> findByBidAndUserId(Long bid, Long userId);
}
