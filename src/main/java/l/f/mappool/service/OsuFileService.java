package l.f.mappool.service;

import l.f.mappool.entity.file.OsuFileRecord;
import l.f.mappool.entity.osu.BeatMapSet;
import l.f.mappool.exception.HttpTipException;
import l.f.mappool.properties.BeatmapSelectionProperties;
import l.f.mappool.repository.file.OsuFileLogRepository;
import l.f.mappool.util.AsyncMethodExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 所有Osu相关文件操作的工具集合类
 */
@Slf4j
@Component
public class OsuFileService {
    private final OsuApiService osuApiService;
    private final OsuFileLogRepository osuFileLogRepository;
    private final BeatmapFileService beatmapFileService;

    /**
     * 下载 .osz 的缓存路径, 最终保存格式为 OSU_FILE_PATH/sid/*
     */
    private final String OSU_FILE_PATH;

    /**
     * 非最终状态下的 .osz 的缓存路径, 最终保存格式为 OSU_FILE_PATH_TMP/sid/*
     */
    private final String OSU_FILE_PATH_TMP;


    @Autowired
    public OsuFileService(
            OsuApiService osuApiService,
            BeatmapSelectionProperties properties,
            OsuFileLogRepository osuFileLogRepository,
            BeatmapFileService beatmapFileService
    ) throws IOException {
        this.osuApiService = osuApiService;
        String SAVE_PATH = properties.getFilePath();
        OSU_FILE_PATH = properties.getFilePath() + "/osu";
        OSU_FILE_PATH_TMP = OSU_FILE_PATH + "/tmp";
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
    }


    @SuppressWarnings("unused")
    public long sizeOfOsuFile(long bid, BeatmapFileService.Type type) throws IOException {
        long sid = osuApiService.getMapInfoByDB(bid).getMapsetId();
        var file = getPath(sid, bid, type);
        return Files.size(file);
    }

    /**
     * 获得谱面文件, 支持获取 音频/背景图片/谱面.osu文件
     *
     * @param type 类型
     */
    @SuppressWarnings("unused")
    public byte[] getOsuFile(long bid, BeatmapFileService.Type type) throws IOException {
        long sid = osuApiService.getMapInfoByDB(bid).getMapsetId();
        Path file = getPath(sid, bid, type);
        return Files.readAllBytes(file);
    }

    /**
     * 获得谱面文件, 支持获取 音频/背景图片/谱面.osu文件, 直接写入到输出流
     *
     * @param type 类型
     */
    @SuppressWarnings("unused")
    public InputStream outOsuFile(long bid, BeatmapFileService.Type type) throws IOException {
        return new FileInputStream(getPathByBid(bid, type).toFile());
    }

    private boolean doDownload(long sid) throws IOException {
        BeatMapSet mapSet = osuApiService.getMapsetInfo(sid);
        Path tmp;
        // 对应 ranked / loved
        boolean isArchive = mapSet.getStatus() == 1 || mapSet.getStatus() == 4;
        if (isArchive) {
            tmp = Path.of(OSU_FILE_PATH, String.valueOf(sid));
        } else {
            tmp = Path.of(OSU_FILE_PATH_TMP, String.valueOf(sid));
        }
        HashMap<String, Path> fileMap = new HashMap<>();
        int writeFile = 0;
        try (var in = beatmapFileService.downloadOsz(sid, beatmapFileService.getRandomAccount())) {
            Files.createDirectories(tmp);
            var zip = new ZipInputStream(in);
            writeFile = loopWriteFile(zip, tmp.toString(), fileMap);
        } finally {
            if (writeFile == 0 && Files.isDirectory(tmp)) {
                FileSystemUtils.deleteRecursively(tmp);
            }
        }
        // 解析数据 并记录
        fileMap.entrySet().stream().filter(e -> e.getKey().endsWith(".osu")).forEach(e -> {
            var path = e.getValue();
            try (var read = Files.newBufferedReader(path)) {
                var log = OsuFileRecord.parse(read, mapSet);
                log.setFile(path.getFileName().toString());
                osuFileLogRepository.saveAndFlush(log);
            } catch (IOException | RuntimeException err) {
                log.error("解析数据时出错", err);
                // 不处理, 跳过
            }
        });
        return isArchive;
    }

    public Path getPathByBid(long bid, BeatmapFileService.Type type) throws IOException {
        long sid = osuApiService.getMapInfoByDB(bid).getMapsetId();
        var file = getPath(sid, bid, type);
        return file.toAbsolutePath();
    }

    /**
     * 获得谱面文件在本地缓存中的文件名, 支持获取 音频/背景图片/谱面.osu文件
     */
    public Path getPath(long sid, long bid, BeatmapFileService.Type type) throws IOException {
        Path basePath = getPath(sid);
        var fOpt = osuFileLogRepository.findById(bid);
        if (fOpt.isEmpty()) {
            FileSystemUtils.deleteRecursively(basePath);
            throw new HttpTipException("下载/解析文件出错");
        }
        var fLog = fOpt.get();
        String fileLocal = switch (type) {
            case FILE -> fLog.getFile();
            case AUDIO -> fLog.getAudio();
            case BACKGROUND -> fLog.getBackground();
        };

        if (fileLocal == null) throw new IOException("no file in this beatmap");

        return basePath.resolve(fileLocal);
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
        Path dir = getPath(sid);
        if (Objects.isNull(out)) return;
        var zipOut = new ZipOutputStream(out);
        try {
            writeDirToZip(zipOut, dir, "");
        } finally {
            zipOut.finish();
            zipOut.closeEntry();
        }
    }

    public FileOut outOsuZipFile(long sid) throws IOException {
        Path dir = getPath(sid);
        return outStream -> {
            var zipOut = new ZipOutputStream(outStream);
            try {
                writeDirToZip(zipOut, dir, "");
            } finally {
                zipOut.finish();
                zipOut.closeEntry();
            }
        };
    }

    private Path getPath(long sid) throws IOException {
        final Path path = Path.of(OSU_FILE_PATH, String.valueOf(sid));
        final Path path_tmp = Path.of(OSU_FILE_PATH_TMP, String.valueOf(sid));
        boolean isArchive = true;
        if (!Files.isDirectory(path)
                && (isArchive = !Files.isDirectory(path_tmp))) {
            isArchive = downloadOsuFile(sid);
        }
        if (isArchive) {
            return path;
        } else {
            return path_tmp;
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
     * 将多个 .osz 文件打包为一个 .zip 文件 并写入到输出流
     *
     * @param sids sid, 用于打包
     * @throws IOException 当下载出错/并不存在该谱面时报错
     */
    public FileOut zipOsuFiles(long... sids) throws IOException {
        HashMap<Long, FileOut> files = new HashMap<>(sids.length);
        for (var sid : sids) {
            files.put(sid, outOsuZipFile(sid));
        }
        return outStream -> {
            var zipOut = new ZipOutputStream(outStream);
            files.forEach((key, out) -> {
                try {
                    var zip = new ZipEntry(key + ".osz");
                    zipOut.putNextEntry(zip);
                    out.write(zipOut);
                } catch (IOException e) {
                    log.error("写入文件时异常: ", e);
                }
            });
            zipOut.flush();
            zipOut.finish();
            zipOut.closeEntry();
        };
    }

    /**
     * 下载单个 .osz 并解包写入到缓存目录中, 并将相关文件信息记录到数据库里.
     */
    private boolean downloadOsuFile(long sid) throws IOException {
        try {
            return AsyncMethodExecutor.<Boolean>execute(
                    () -> doDownload(sid),
                    sid,
                    () -> Files.isDirectory(Path.of(OSU_FILE_PATH, String.valueOf(sid))));

        } catch (IOException ioException) {
            throw ioException;
        } catch (Exception e) {
            log.error("执行出现其他异常", e);
            throw new RuntimeException(e);
        }
    }

    public interface FileOut {
        void write(OutputStream out) throws IOException;
    }

    private int loopWriteFile(ZipInputStream zip, String basePath, HashMap<String, Path> fileMap) throws IOException {
        ZipEntry zipFile;
        int count = 0;
        while ((zipFile = zip.getNextEntry()) != null) {
            try {
                if (zipFile.isDirectory()) continue;
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

    public void removeFile(long delSid) {
        try {
            Path path = Path.of(OSU_FILE_PATH, String.valueOf(delSid));

            if (Files.isDirectory(path)) {
                FileSystemUtils.deleteRecursively(path);
            }

            path = Path.of(OSU_FILE_PATH_TMP, String.valueOf(delSid));
            if (Files.isDirectory(path)) {
                FileSystemUtils.deleteRecursively(path);
            }
        } catch (IOException e) {
            log.error("清空 tmp map 文件夹出错", e);
        }

        osuFileLogRepository.deleteAllBySid(delSid);
    }

    public void removeTemp() throws IOException {
        Consumer<Path> consumer = p -> {
            var sid = Long.parseLong(p.getFileName().toString());
            try {
                FileSystemUtils.deleteRecursively(p);
                osuFileLogRepository.deleteAllBySid(sid);
            } catch (IOException e) {
                log.error("清空 tmp map 文件夹出错", e);
            }
        };
        try (var stream = Files.list(Path.of(OSU_FILE_PATH_TMP))) {
            stream.forEach(consumer);
        }
    }

    public void removeTemp(long delSid) {
        Consumer<Path> consumer = p -> {
            var sid = Long.parseLong(p.getFileName().toString());
            try {
                FileSystemUtils.deleteRecursively(p);
                osuFileLogRepository.deleteAllBySid(sid);
            } catch (IOException e) {
                log.error("删除 tmp map 文件出错", e);
            }
        };
        Path path = Path.of(OSU_FILE_PATH_TMP, String.valueOf(delSid));
        if (Files.isDirectory(path)) {
            consumer.accept(path);
        }
    }

    public BeatmapSetCount getCount() {
        int all = osuFileLogRepository.countAll();
        int allSet = osuFileLogRepository.countBySid();
        return new BeatmapSetCount(allSet, all);
    }

    public record BeatmapSetCount(int countMapSet, int countBeatmap) {
    }
}