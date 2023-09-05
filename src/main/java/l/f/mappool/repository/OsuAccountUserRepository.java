package l.f.mappool.repository;

import l.f.mappool.entity.OsuAccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

@Component
public interface OsuAccountUserRepository extends JpaRepository<OsuAccountUser, Long>, JpaSpecificationExecutor<OsuAccountUser> {
    @Query(value = "select * from account_user limit 1 offset :index", nativeQuery = true)
    OsuAccountUser getByIndex(long index);
}
