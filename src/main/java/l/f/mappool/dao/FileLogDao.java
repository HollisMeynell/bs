package l.f.mappool.dao;

import l.f.mappool.properties.BeatmapSelectionProperties;
import l.f.mappool.repository.FileLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

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
    public byte[] getData(String key) throws IOException {
        var path = Path.of(SAVE_PATH, key);
        if (Files.isRegularFile(path) && Files.isReadable(path)) {
            return Files.readAllBytes(path);
        }
        throw new IOException("file not found");
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
}
