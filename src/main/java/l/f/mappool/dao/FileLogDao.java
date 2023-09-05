package l.f.mappool.dao;

import l.f.mappool.entity.FileLog;
import l.f.mappool.entity.OsuFileLog;
import l.f.mappool.exception.LogException;
import l.f.mappool.properties.BeatmapSelectionProperties;
import l.f.mappool.repository.FileLogRepository;
import l.f.mappool.repository.OsuFileLogRepository;
import l.f.mappool.service.BeatmapFileService;
import l.f.mappool.service.OsuApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Component
public class FileLogDao {
    private final OsuApiService osuApiService;
    private final FileLogRepository fileLogRepository;
    private final OsuFileLogRepository osuFileLogRepository;
    private final BeatmapFileService beatmapFileService;

    private final String UPLOAD_PATH;
    private final String OSU_FILE_PATH;


    @Autowired
    public FileLogDao(
            OsuApiService osuApiService,
            BeatmapSelectionProperties properties,
            FileLogRepository fileLogRepository,
            OsuFileLogRepository osuFileLogRepository,
            BeatmapFileService beatmapFileService
    ) throws IOException {
        this.osuApiService = osuApiService;
        String SAVE_PATH = properties.getFilePath();
        UPLOAD_PATH = properties.getFilePath() + "/upload";
        OSU_FILE_PATH = properties.getFilePath() + "/osu";
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
        var path = Path.of(UPLOAD_PATH, fileLog.getLocalName());

        if (Files.isRegularFile(path) && Files.isReadable(path)) {
            var fileData = Files.readAllBytes(path);
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
        Path path = Path.of(UPLOAD_PATH, key);
        var data = in.readAllBytes();
        Files.write(path, data, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
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

    @SuppressWarnings("unused")
    public byte[] getOsuFile(long bid, BeatmapFileService.Type type) throws IOException {
        long sid = osuApiService.getMapInfoByDB(bid).getMapsetId();
        String file = getPath(sid, bid, type);
        return Files.readAllBytes(Path.of(OSU_FILE_PATH, Long.toString(sid), file));
    }

    @SuppressWarnings("unused")
    public void outOsuFile(long bid, BeatmapFileService.Type type, OutputStream out) throws IOException {
        long sid = osuApiService.getMapInfoByDB(bid).getMapsetId();
        var file = getPath(sid, bid, type);
        try (var in = new FileInputStream(Path.of(OSU_FILE_PATH, Long.toString(sid), file).toFile()); out) {
            byte[] buf = new byte[1024];
            int i;
            while ((i = in.read(buf)) != -1) {
                out.write(buf, 0, i);
            }
        }
    }

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

    public void outOsuZipFile(long sid, OutputStream out) throws IOException {
        Path dir = Path.of(OSU_FILE_PATH, String.valueOf(sid));
        if (!Files.isDirectory(dir)) {
            downloadOsuFile(sid, 0);
        }

        var zipOut = new ZipOutputStream(out);

        try (var allFile = Files.list(dir);) {
            for (var p : allFile.toList()) {
                var zip = new ZipEntry(p.getFileName().toString());
                zipOut.putNextEntry(zip);
                zipOut.write(Files.readAllBytes(p));
            }
        } finally {
            zipOut.finish();
            zipOut.closeEntry();
        }
    }

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

    private Optional<OsuFileLog> downloadOsuFile(long sid, long bid) throws IOException {
        Path tmp = Path.of(OSU_FILE_PATH, String.valueOf(sid));
        Files.createDirectories(tmp);
        HashMap<String, Path> fileMap = new HashMap<>();
        try (var in = beatmapFileService.download(sid, beatmapFileService.getRandomAccount());) {
            var zip = new ZipInputStream(in);
            ZipEntry zipFile;
            while ((zipFile = zip.getNextEntry()) != null) {
                try {
                    Path zipFilePath = Path.of(tmp.toString(), zipFile.getName());
                    Files.write(zipFilePath, zip.readNBytes((int) zipFile.getSize()));
                    fileMap.put(zipFile.getName(), zipFilePath);
                } catch (IOException e) {
                    // do nothing
                }
            }
        }

        return fileMap
                .entrySet()
                .stream()
                .filter(e -> e.getKey().endsWith(".osu"))
                .map(e -> {
                    var path = e.getValue();
                    try (var read = Files.newBufferedReader(path);) {
                        var log = OsuFileLog.parse(read);
                        log.setFile(path.getFileName().toString());
                        log = osuFileLogRepository.saveAndFlush(log);
                        return log;
                    } catch (IOException ex) {
                        //
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .filter(f -> f.getBid() == bid)
                .findAny();

    }
}
