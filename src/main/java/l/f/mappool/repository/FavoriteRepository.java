package l.f.mappool.repository;

import l.f.mappool.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public interface FavoriteRepository extends JpaRepository<Favorite, Integer>, JpaSpecificationExecutor<Integer> {
    //select * from notation where :serchText like any(column)
    //select DISTINCT unnest(notation.tags) as all from notation where userid=
    //SELECT DISTINCT value FROM Notation e, IN(e.tags) value
    @Transactional
    @Query(value = "select distinct unnest(favorite.tags) from favorite where user_id = :uid", nativeQuery = true)
    List<String> allUserTags(Long uid);

    @Modifying
    @Transactional
    @Query(value = "update favorite set tags = array_remove(tags, :tag) where id = :id", nativeQuery = true)
    void deleteTags(int id, String tag);

    @Modifying
    @Transactional
    @Query(value = "update favorite set tags = (select array_agg(distinct t) from unnest(array_cat(tags, :tags)) as t) where id = :id", nativeQuery = true)
    void addTags(int id, String... tags);

    @Modifying
    @Transactional
    @Query(value = "update favorite set tags[array_position(tags, :oldTag)] = :tag where id = :id", nativeQuery = true)
    void replaceTags(int id, String oldTag, String tag);

    @Transactional
    @Query(value = "select * from favorite where favorite.tags @> ARRAY[:tag]", nativeQuery = true)
    List<Favorite> searchAllByTags(String... tag);
    @Transactional
    @Query(value = "select * from favorite where user_id = :uid and favorite.tags @> ARRAY[:tag]", nativeQuery = true)
    List<Favorite> searchUserAllByTags(long uid, String... tag);
    @Transactional
    List<Favorite> getAllByUserId(long userId);
    Optional<Favorite> findByBidAndUserId(Long bid, Long userId);
}
