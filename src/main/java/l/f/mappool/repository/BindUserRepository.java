package l.f.mappool.repository;

import l.f.mappool.entity.BindUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public interface BindUserRepository extends JpaRepository<BindUser, Long> {
    @Modifying
    @Transactional
    @Query("update BindUser o set o.accessToken = :accessToken,o.refreshToken = :refreshToken, o.time = :time where o.osuId=:uid")
    void updateToken(Long uid, String accessToken, String refreshToken, Long time);
}
