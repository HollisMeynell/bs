package l.f.mappool.repository;

import l.f.mappool.entity.FileLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public interface FileLogRepository extends JpaRepository<FileLog, Integer> {

    Optional<FileLog> getFileLogByLocalName(String localName);
    default FileLog save(String name, String local) {
        var f = new FileLog();
        f.setFileName(name);
        f.setLocalName(local);
        return save(f);
    }

    @Query("select f.localName from FileLog f where f.updateTime<:time")
    List<String> getLocalNamesByUpdateTimeBefor(LocalDateTime time);

    @Modifying
    @Transactional
    @Query("update FileLog f set f.updateTime=:time where f.localName=:name")
    void updateByLocalName(String name, LocalDateTime time);

    @Modifying
    @Transactional
    @Query("delete from FileLog f where f.localName in (:localNames)")
    void deleteByUpdateTimeBefore(List<String> localNames);
}
