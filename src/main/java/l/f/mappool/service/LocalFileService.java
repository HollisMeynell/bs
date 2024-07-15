package l.f.mappool.service;

import l.f.mappool.entity.file.FileRecord;
import l.f.mappool.exception.HttpError;
import l.f.mappool.properties.BeatmapSelectionProperties;
import l.f.mappool.repository.file.FileLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 文件操作的工具类
 */
@Slf4j
@Component
public class LocalFileService {
    private final FileLogRepository fileLogRepository;

    /**
     * 文件上传时的保存路径
     */
    private final String UPLOAD_PATH;

    /**
     * 上传静态文件素材的路径
     */
    private final String STATIC_PATH;

    public LocalFileService(BeatmapSelectionProperties properties, FileLogRepository fileLogRepository) throws IOException {
        String SAVE_PATH = properties.getFilePath();
        UPLOAD_PATH = properties.getFilePath() + "/upload";
        STATIC_PATH = properties.getFilePath() + "/static";
        this.fileLogRepository = fileLogRepository;
        Path p = Path.of(SAVE_PATH);
        if (! Files.isDirectory(p)) {
            Files.createDirectories(p);
        }
        p = Path.of(UPLOAD_PATH);
        if (! Files.isDirectory(p)) {
            Files.createDirectories(p);
        }
        p = Path.of(STATIC_PATH);
        if (! Files.isDirectory(p)) {
            Files.createDirectories(p);
        }
    }

    /**
     * 通过文件记录 key 来获得上传时提供的文件名
     *
     * @return 记录中的文件名
     */
    public String getFileName(String key) {
        var fileLog = fileLogRepository.getFileLogByLocalName(key);
        if (fileLog.isPresent()) {
            return fileLog.get().getFileName();
        } else {
            return "unknown";
        }
    }

    /**
     * 通过文件记录 key 来获得上传时的记录信息
     *
     * @return 记录信息
     */
    public Optional<FileRecord> getFileRecord(String key) {
        return fileLogRepository.getFileLogByLocalName(key);
    }

    /**
     * 获取文件 二进制数据
     */
    public byte[] getData(FileRecord fileRecord) throws IOException {
        var path = Path.of(UPLOAD_PATH, fileRecord.getLocalName());

        if (Files.isRegularFile(path) && Files.isReadable(path)) {
            var fileData = Files.readAllBytes(path);
            fileRecord.update();
            fileLogRepository.save(fileRecord);
            return fileData;
        } else {
            fileLogRepository.delete(fileRecord);
        }
        throw new IOException("file not in file");
    }

    public String writeFile(String name, InputStream in) throws IOException {
        var key = UUID.randomUUID().toString();
        Path path = Path.of(UPLOAD_PATH, key);
        while (true) {
            try {
                Files.createFile(path);
                break;
            } catch (IOException e) {
                key = UUID.randomUUID().toString();
                path = Path.of(UPLOAD_PATH, key);
            }
        }
        Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
        fileLogRepository.save(name, key);
        return key;
    }

    public String writeFile(String name, byte[] in) throws IOException {
        var key = UUID.randomUUID().toString();
        Path path = Path.of(UPLOAD_PATH, key);
        Files.write(path, in, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        fileLogRepository.save(name, key);
        return key;
    }

    public void deleteFile(String key) throws IOException {
        Path path = Path.of(UPLOAD_PATH, key);
        if (Files.isRegularFile(path) && Files.isWritable(path)) {
            Files.delete(path);
            fileLogRepository.deleteByLocalName(key);
        }
    }

    /**
     * 删除超过30天未被访问的文件, 防止硬盘空间被无限占用
     *
     * @return 被删除文件的数量
     */
    public int deleteAllOldFile() {
        // when a file not visited for 30 days, delete it
        LocalDateTime before = LocalDateTime.now().minusDays(31);
        List<String> files = fileLogRepository.getLocalNamesByUpdateTimeBefore(before);
        if (files.isEmpty()) {
            return 0;
        }
        AtomicInteger deleteCount = new AtomicInteger(0);
        files.forEach(f -> {
            try {
                Files.delete(Path.of(UPLOAD_PATH, f));
                deleteCount.addAndGet(1);
            } catch (IOException e) {
                // ignore
            }
        });
        fileLogRepository.deleteByLocalName(files);
        return deleteCount.get();
    }

    /**
     * 获取静态文件
     */
    public byte[] getStaticFile(String fileName) throws HttpError {
        var path = Path.of(STATIC_PATH, fileName);
        if (! Files.isRegularFile(path)) {
            throw new HttpError(400, "file not found");
        }
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new HttpError(500, "file read error");
        }
    }

    /**
     * 获取文件的绝对路径
     */
    public Path getStaticFilePath(String fileName) throws HttpError {
        var path = Path.of(STATIC_PATH, fileName);
        if (! Files.isRegularFile(path)) {
            throw new HttpError(400, "file not found");
        }
        return path;
    }
}
