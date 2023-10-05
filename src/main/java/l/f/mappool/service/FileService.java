package l.f.mappool.service;

import l.f.mappool.entity.file.FileRecord;
import l.f.mappool.entity.file.OsuFileRecord;
import l.f.mappool.exception.HttpError;
import l.f.mappool.exception.LogException;
import l.f.mappool.properties.BeatmapSelectionProperties;
import l.f.mappool.repository.file.FileLogRepository;
import l.f.mappool.repository.file.OsuFileLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 所有文件操作的工具集合类
 */
@Slf4j
@Component
public class FileService {
    private final OsuApiService        osuApiService;
    private final FileLogRepository    fileLogRepository;
    private final OsuFileLogRepository osuFileLogRepository;
    private final BeatmapFileService   beatmapFileService;

    /**
     * 文件上传时的保存路径
     */
    private final String UPLOAD_PATH;

    /**
     * 下载 .osz 的缓存路径, 最终保存格式为 OSU_FILE_PATH/sid/*
     */
    private final String OSU_FILE_PATH;
    private final String STATIC_PATH;


    @Autowired
    public FileService(OsuApiService osuApiService, BeatmapSelectionProperties properties, FileLogRepository fileLogRepository, OsuFileLogRepository osuFileLogRepository, BeatmapFileService beatmapFileService) throws IOException {
        this.osuApiService = osuApiService;
        String SAVE_PATH = properties.getFilePath();
        UPLOAD_PATH = properties.getFilePath() + "/upload";
        OSU_FILE_PATH = properties.getFilePath() + "/osu";
        STATIC_PATH = properties.getFilePath() + "/static";
        this.osuFileLogRepository = osuFileLogRepository;
        this.beatmapFileService = beatmapFileService;
        Path p = Path.of(SAVE_PATH);
        if (!Files.isDirectory(p)) {
            Files.createDirectories(p);
        }
        p = Path.of(OSU_FILE_PATH);
        if (!Files.isDirectory(p)) {
            Files.createDirectories(p);
        }
        p = Path.of(UPLOAD_PATH);
        if (!Files.isDirectory(p)) {
            Files.createDirectories(p);
        }
        p = Path.of(STATIC_PATH);
        if (!Files.isDirectory(p)) {
            Files.createDirectories(p);
        }
        this.fileLogRepository = fileLogRepository;
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
     * 获得谱面文件, 支持获取 音频/背景图片/谱面.osu文件
     *
     * @param type 类型
     */
    @SuppressWarnings("unused")
    public byte[] getOsuFile(long bid, BeatmapFileService.Type type) throws IOException {
        long sid = osuApiService.getMapInfoByDB(bid).getMapsetId();
        String file = getPath(sid, bid, type);
        return Files.readAllBytes(Path.of(OSU_FILE_PATH, Long.toString(sid), file));
    }

    /**
     * 获得谱面文件, 支持获取 音频/背景图片/谱面.osu文件, 直接写入到输出流
     *
     * @param type 类型
     */
    @SuppressWarnings("unused")
    public void outOsuFile(long bid, BeatmapFileService.Type type, OutputStream out) throws IOException {
        long sid = osuApiService.getMapInfoByDB(bid).getMapsetId();
        var file = getPath(sid, bid, type);
        try (var in = new FileInputStream(Path.of(OSU_FILE_PATH, Long.toString(sid), file).toFile()); out) {
            byte[] buf = new byte[1024];
            int i;
            long c = 0;
            while ((i = in.read(buf)) != -1) {
                out.write(buf, 0, i);
                c += i;
            }
            log.info("length=[{}]", c);
            out.flush();
        } catch (FileNotFoundException fileNotFoundException) {
            out.close();
        }
    }

    public long sizeOfOsuFile(long bid, BeatmapFileService.Type type) throws IOException {
        long sid = osuApiService.getMapInfoByDB(bid).getMapsetId();
        var file = getPath(sid, bid, type);
        if (file == null) return 0;

        return Files.size(Path.of(OSU_FILE_PATH, Long.toString(sid), file));
    }

    /**
     * 获得谱面文件在本地缓存中的文件名, 支持获取 音频/背景图片/谱面.osu文件
     */
    public String getPath(long sid, long bid, BeatmapFileService.Type type) throws IOException {
        var fOpt = osuFileLogRepository.findById(bid);
        if (fOpt.isEmpty()) {
            fOpt = downloadOsuFile(sid, bid);
            if (fOpt.isEmpty()) throw new LogException("下载/解析文件出错");
        }
        var fLog = fOpt.get();
        String file = null;
        switch (type) {
            case FILE -> file = fLog.getFile();
            case AUDIO -> file = fLog.getAudio();
            case BACKGROUND -> file = fLog.getBackground();
        }
        return file;
    }

    @SuppressWarnings("unused")
    public byte[] getOsuZipFile(long sid) throws IOException {
        var out = new ByteArrayOutputStream(1024);
        outOsuZipFile(sid, out);
        return out.toByteArray();
    }

    /**
     * 将 .osz 文件写入到输出流, 优先从本地缓存中读取
     */
    public void outOsuZipFile(long sid, OutputStream out) throws IOException {
        Path dir = Path.of(OSU_FILE_PATH, String.valueOf(sid));
        if (!Files.isDirectory(dir)) {
            downloadOsuFile(sid, 0);
        }
        var zipOut = new ZipOutputStream(out);
        try {
            writeDirToZip(zipOut, dir, "");
        } finally {
            zipOut.finish();
            zipOut.closeEntry();
        }
    }

    /**
     * 将多个 .osz 文件打包为一个 .zip 文件 并写入到输出流
     *
     * @param sids sid, 用于打包
     * @throws IOException 当下载出错/并不存在该谱面时报错
     */
    public void zipOsuFiles(OutputStream out, long... sids) throws IOException {
        var zipOut = new ZipOutputStream(out);
        try {
            for (var sid : sids) {
                var zip = new ZipEntry(sid + ".osz");
                zipOut.putNextEntry(zip);
                outOsuZipFile(sid, zipOut);
            }
        } finally {
            zipOut.flush();
            zipOut.finish();
            zipOut.closeEntry();
        }

    }

    /**
     * 下载单个 .osz 并解包写入到缓存目录中, 并将相关文件信息记录到数据库里.
     */
    private Optional<OsuFileRecord> downloadOsuFile(long sid, long bid) throws IOException {
        Path tmp = Path.of(OSU_FILE_PATH, String.valueOf(sid));
        HashMap<String, Path> fileMap = new HashMap<>();
        int writeFile = 0;
        try (var in = beatmapFileService.downloadOsz(sid, beatmapFileService.getRandomAccount());) {
            Files.createDirectories(tmp);
            var zip = new ZipInputStream(in);
            writeFile = loopWriteFile(zip, tmp.toString(), fileMap);
        } finally {
            if (writeFile == 0) {
                Files.delete(tmp);
            }
        }

        return fileMap.entrySet().stream().filter(e -> e.getKey().endsWith(".osu")).map(e -> {
            var path = e.getValue();
            try (var read = Files.newBufferedReader(path);) {
                var log = OsuFileRecord.parse(read);
                log.setFile(path.getFileName().toString());
                log = osuFileLogRepository.saveAndFlush(log);
                return log;
            } catch (IOException ex) {
                //
            }
            return null;
        }).filter(Objects::nonNull).filter(f -> f.getBid() == bid).findAny();

    }

    private int loopWriteFile(ZipInputStream zip, String basePath, HashMap<String, Path> fileMap) throws IOException {
        ZipEntry zipFile;
        int count = 0;
        while ((zipFile = zip.getNextEntry()) != null) {
            try {
                Path zipFilePath = Path.of(basePath, zipFile.getName());
                Files.createDirectories(zipFilePath.getParent());
                Files.write(zipFilePath, zip.readNBytes((int) zipFile.getSize()));
                fileMap.put(zipFile.getName(), zipFilePath);
                count++;
            } catch (IOException e) {
                // do nothing
                log.error("write download file error", e);
            }
        }
        return count;
    }

    private void writeDirToZip(ZipOutputStream zipOut, Path dir, String basePath) throws IOException {
        try (var allFile = Files.list(dir)) {
            for (var file : allFile.toList()) {
                if (Files.isDirectory(file)) {
                    writeDirToZip(zipOut, file, basePath + file.getFileName() + "/");
                    continue;
                }
                var zip = new ZipEntry(basePath + file.getFileName().toString());
                zipOut.putNextEntry(zip);
                zipOut.write(Files.readAllBytes(file));
            }
        }
    }

    public byte[] getStaticFile(String fileName) throws HttpError {
        var path = Path.of(STATIC_PATH, fileName);
        if (!Files.isRegularFile(path)) {
            throw new HttpError(400, "file not found");
        }
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new HttpError(500, "file read error");
        }
    }
}
