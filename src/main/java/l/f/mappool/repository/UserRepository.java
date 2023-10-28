package l.f.mappool.repository;

import l.f.mappool.entity.LoginUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<LoginUser, Long> {
    List<LoginUser> findByOsuId(long osuId);

    @Query("select count(i) from LoginUser i where i.osuId=:osuId and i.code=:code")
    int countByOsuIdAndCode(long osuId, String code);
}