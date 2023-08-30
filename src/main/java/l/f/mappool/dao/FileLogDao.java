package l.f.mappool.dao;

import l.f.mappool.entity.FileLog;
import l.f.mappool.properties.BeatmapSelectionProperties;
import l.f.mappool.repository.FileLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class FileLogDao {
    private FileLogRepository fileLogRepository;

    private final String SAVE_PATH;

    @Autowired
    public FileLogDao(BeatmapSelectionProperties properties, FileLogRepository fileLogRepository) throws IOException {
        SAVE_PATH = properties.getFilePath();
        Path p = Path.of(SAVE_PATH);
        if (!Files.isDirectory(p)) {
            Files.createDirectories(p);
        }
        this.fileLogRepository = fileLogRepository;
    }

    public String getFileName(String key) {
        var fileLog = fileLogRepository.getFileLogByLocalName(key);
        if (fileLog.isPresent()) {
            return fileLog.get().getFileName();
        } else {
            return "unknown";
        }
    }
    public Optional<FileLog> getFileLog(String key) {
        return fileLogRepository.getFileLogByLocalName(key);
    }
    public byte[] getData(FileLog fileLog) throws IOException {
        var path = Path.of(SAVE_PATH, fileLog.getLocalName());

        if (Files.isRegularFile(path) && Files.isReadable(path)) {
            var fileData =  Files.readAllBytes(path);
            fileLog.update();
            fileLogRepository.save(fileLog);
            return fileData;
        } else {
            fileLogRepository.delete(fileLog);
        }
        throw new IOException("file not in local");
    }

    public String writeFile(String name, InputStream in) throws IOException {
        var key = UUID.randomUUID().toString();
        Path path = Path.of(SAVE_PATH, key);
        var data = in.readAllBytes();
        Files.write(path, data, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        fileLogRepository.save(name,key);
        return key;
    }

    public String writeFile(String name, byte[] in) throws IOException {
        var key = UUID.randomUUID().toString();
        Path path = Path.of(SAVE_PATH, key);
        Files.write(path, in, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        fileLogRepository.save(name,key);
        return key;
    }

    public void deleteFile(String key) throws IOException {
        Path path = Path.of(SAVE_PATH, key);
        if (Files.isRegularFile(path) && Files.isWritable(path)) {
            Files.delete(path);
            fileLogRepository.deleteByLocalName(key);
        }
    }


    public int deleteAllOldFile(){
        // when a file not visited for 30 days, delete it
        LocalDateTime before = LocalDateTime.now().minusDays(31);
        List<String> files = fileLogRepository.getLocalNamesByUpdateTimeBefore(before);
        if (files.isEmpty()) {
            return 0;
        }
        AtomicInteger deleteCount = new AtomicInteger(0);
        files.forEach(f -> {
            try {
                Files.delete(Path.of(SAVE_PATH, f));
                deleteCount.addAndGet(1);
            } catch (IOException e) {
                // ignore
            }
        });
        fileLogRepository.deleteByLocalName(files);
        return deleteCount.get();
    }
}
