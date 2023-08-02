package l.f.mappool.repository;

import l.f.mappool.entity.FileLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

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
}
