package l.f.mappool.repository.pool;

import l.f.mappool.entity.pool.PoolFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public interface FeedbackRepository extends JpaRepository<PoolFeedback, Integer> {
    @Modifying
    @Transactional
    @Query("delete from PoolFeedback f where f.item.id=:poolCategoryItemId")
    void deleteAllByPoolCategoryItemId(int poolCategoryItemId);
}
