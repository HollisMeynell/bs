package l.f.mappool.repository.pool;

import l.f.mappool.entity.pool.PoolFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface FeedbackRepository extends JpaRepository<PoolFeedback, Integer> {

}
