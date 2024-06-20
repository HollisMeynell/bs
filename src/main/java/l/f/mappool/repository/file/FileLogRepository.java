package l.f.mappool.repository.file;

import l.f.mappool.entity.file.FileRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("UnusedReturnValue")
@Component
public interface FileLogRepository extends JpaRepository<FileRecord, Integer> {

    Optional<FileRecord> getFileLogByLocalName(String localName);
    @SuppressWarnings("UnusedReturnValue")
    default FileRecord save(String name, String local) {
        var f = new FileRecord();
        f.setFileName(name);
        f.setLocalName(local);
        return save(f);
    }

    @Query("select f.localName from FileRecord f where f.updateTime<:time")
    List<String> getLocalNamesByUpdateTimeBefore(LocalDateTime time);

    @Modifying
    @Transactional
    @Query("delete from FileRecord f where f.localName in (:localNames)")
    void deleteByLocalName(List<String> localNames);

    @Modifying
    @Transactional
    @Query("delete from FileRecord f where f.localName=:localNames")
    void deleteByLocalName(String localNames);
}
