package l.f.mappool.repository.osu;

import l.f.mappool.entity.osu.OsuUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public interface OsuUserRepository extends JpaRepository<OsuUser, Long> {
    @Modifying
    @Transactional
    @SuppressWarnings("unused")
    @Query("update OsuUser o set o.accessToken = :accessToken,o.refreshToken = :refreshToken, o.time = :time where o.osuId=:uid")
    void updateToken(Long uid, String accessToken, String refreshToken, Long time);
    @Modifying
    @Transactional
    @Query("update OsuUser o set o.accessToken = :#{#user.accessToken},o.refreshToken = :#{#user.refreshToken}, o.time = :#{#user.time} where o.osuId=:#{#user.osuId}")
    void updateToken(@Param("user")OsuUser user);
}
