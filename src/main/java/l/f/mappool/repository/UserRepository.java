package l.f.mappool.repository;

import l.f.mappool.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByOsuId(long osuId);
    @Query("select count(i) from User i where i.osuId=:osuId and i.code=:code")
    int countByOsuIdAndCode(long osuId, String code);
}