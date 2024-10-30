package l.f.mappool.service;

import l.f.mappool.entity.file.OsuFileRecord;
import l.f.mappool.entity.osu.BeatMap;
import l.f.mappool.entity.osu.BeatMapSet;
import l.f.mappool.exception.HttpTipException;
import l.f.mappool.exception.NotFoundException;
import l.f.mappool.properties.BeatmapSelectionProperties;
import l.f.mappool.repository.file.OsuFileLogRepository;
import l.f.mappool.util.AsyncMethodExecutor;
import l.f.mappool.util.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * 所有Osu相关文件操作的工具集合类
 */
@Slf4j
@Component
public class OsuFileService {
    private final OsuApiService          osuApiService;
    private final OsuFileLogRepository   osuFileLogRepository;
    private final DownloadOsuFileService downloadOsuFileService;

    /**
     * 下载 .osz 的缓存路径, 最终保存格式为 OSU_FILE_PATH/sid/*
     */
    private final String OSU_FILE_PATH;

    private final Optional<Path> OSU_COPY_DIR;

    public OsuFileService(
            OsuApiService osuApiService,
            BeatmapSelectionProperties properties,
            OsuFileLogRepository osuFileLogRepository,
            DownloadOsuFileService downloadOsuFileService
    ) throws IOException {
        this.osuApiService = osuApiService;
        String SAVE_PATH = properties.getFilePath();
        OSU_FILE_PATH = properties.getFilePath() + "/osu";
        this.osuFileLogRepository = osuFileLogRepository;
        this.downloadOsuFileService = downloadOsuFileService;
        Path p = Path.of(SAVE_PATH);
        if (!Files.isDirectory(p)) {
            Files.createDirectories(p);
        }
        p = Path.of(OSU_FILE_PATH);
        if (!Files.isDirectory(p)) {
            Files.createDirectories(p);
        }

        var pathOpt = properties.getLocalOsuDirectory()
                .map(Path::of);
        if (pathOpt.map(Files::isDirectory).orElse(false)) {
            OSU_COPY_DIR = pathOpt;
        } else {
            OSU_COPY_DIR = Optional.empty();
        }
    }

    @SuppressWarnings("unused")
    public long sizeOfOsuFile(long bid, DownloadOsuFileService.Type type) throws IOException {
        var file = getPath(bid, type);
        return Files.size(file);
    }

    /**
     * 获得谱面文件, 支持获取 音频/背景图片/谱面.osu文件
     *
     * @param type 类型
     */
    public byte[] getOsuFile(long bid, DownloadOsuFileService.Type type) throws IOException {
        Path file = getPath(bid, type);
        return Files.readAllBytes(file);
    }

    /**
     * 获得谱面文件, 支持获取 音频/背景图片/谱面.osu文件, 直接写入到输出流
     *
     * @param type 类型
     */
    @SuppressWarnings("unused")
    public InputStream outOsuFile(long bid, DownloadOsuFileService.Type type) throws IOException {
        return new FileInputStream(getPathByBid(bid, type).toFile());
    }

    public boolean onUploadMap(long sid, InputStream in) throws IOException {
        var mapSet = osuApiService.getMapsetInfo(sid);
        return doDownloadStream(sid, mapSet, in);
    }

    private void doDownload(long sid, BeatMapSet mapSet) throws IOException {
        var in = downloadOsuFileService.downloadOsz(sid, downloadOsuFileService.getRandomAccount());
        doDownloadStream(sid, mapSet, in);
    }

    private boolean doDownloadStream(long sid, BeatMapSet mapSet, InputStream in) throws IOException {
        var maps = new HashMap<Long, BeatMap>(mapSet.getBeatMaps().size());
        for (var m : mapSet.getBeatMaps()) {
            maps.put(m.getId(), m);
        }
        Path osuPath = Path.of(OSU_FILE_PATH, String.valueOf(sid));
        if (Files.isDirectory(osuPath)) FileSystemUtils.deleteRecursively(osuPath);
        HashMap<String, Path> fileMap = new HashMap<>();
        int writeFile = 0;
        try (in) {
            Files.createDirectories(osuPath);
            var zipInput = new ZipArchiveInputStream(in);
            writeFile = loopWriteFile(zipInput, osuPath.toString(), fileMap);
        } finally {
            if (writeFile == 0 && Files.isDirectory(osuPath)) {
                FileSystemUtils.deleteRecursively(osuPath);
            }
        }
        // 解析数据 并记录
        fileMap.entrySet().stream().filter(e -> e.getKey().endsWith(".osu")).forEach(e -> {
            var path = e.getValue();
            try (var read = Files.newBufferedReader(path)) {
                var log = OsuFileRecord.parse(read, mapSet);
                var info = maps.get(log.getBid());
                String md5;
                if (Objects.isNull(info) || !StringUtils.hasText(info.getChecksum())) {
                    md5 = DataUtil.getFileMd5(path);
                } else {
                    md5 = info.getChecksum();
                }
                log.setCheck(md5);
                log.setStatus(mapSet.getStatus());
                log.setLast(mapSet.getLastUpdated().toLocalDateTime());
                log.setFile(path.getFileName().toString());
                copyLink(log.getBid(), path);
                osuFileLogRepository.saveAndFlush(log);
            } catch (IOException | RuntimeException err) {
                log.error("解析数据时出错, 源文件: [{}]", path.toString(), err);
                // 不处理, 跳过
            }
        });
        return writeFile != 0;
    }

    public Path getPathByBid(long bid, DownloadOsuFileService.Type type) throws IOException {
        var file = getPath(bid, type);
        return file.toAbsolutePath();
    }

    /**
     * 获得谱面文件在本地缓存中的文件名, 支持获取 音频/背景图片/谱面.osu文件
     */
    public Path getPath(long bid, DownloadOsuFileService.Type type) throws IOException {
        if (bid < 75) {
            throw new HttpTipException("bid 不合法");
        }
        var fOpt = osuFileLogRepository.findById(bid);

        long sid = fOpt.map(OsuFileRecord::getSid)
                .orElseGet(() -> osuApiService.getMapInfoByDB(bid).getMapsetId());

        Path basePath = getPathAsync(sid);

        fOpt = osuFileLogRepository.findById(bid);

        int retryTime = 0;

        while (fOpt.isEmpty() || !Files.isDirectory(basePath)) {
            retryTime++;
            AsyncMethodExecutor.sleep(retryTime * retryTime * 1000);
            basePath = getPathAsync(sid);
            fOpt = osuFileLogRepository.findById(bid);
            if (retryTime >= 3) throw new HttpTipException("文件缓存失效, 正在更新, 请稍候尝试");
        }

        var fLog = fOpt.get();
        String fileLocal = switch (type) {
            case FILE -> fLog.getFile();
            case AUDIO -> fLog.getAudio();
            case BACKGROUND -> fLog.getBackground();
        };

        if (fileLocal == null) throw new NotFoundException();

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
        Path dir = getPathAsync(sid);
        if (Objects.isNull(out)) return;
        var zipOut = new ZipArchiveOutputStream(out);
        try {
            writeDirToZip(zipOut, dir, "");
        } finally {
            zipOut.finish();
        }
    }

    public FileOut outOsuZipFile(long sid) throws IOException {
        Path dir = getPathAsync(sid);
        return outStream -> {
            var zipOut = new ZipArchiveOutputStream(outStream);
            try {
                writeDirToZip(zipOut, dir, "");
            } finally {
                zipOut.finish();
            }
        };
    }

    public boolean hasOsuFileCache(long bid, long sid) {
        final Path path = Path.of(OSU_FILE_PATH, String.valueOf(sid));
        if (!Files.isDirectory(path)) {
            return false;
        }

        if (OSU_COPY_DIR.isEmpty()) return true;

        var dotOsu = OSU_COPY_DIR.map(p -> p.resolve(bid + ".osu")).get();

        return Files.isRegularFile(dotOsu);
    }

    private Path getPathAsync(long sid) throws IOException {
        try {
            return AsyncMethodExecutor.<Path>execute(
                    () -> getPath(sid),
                    sid,
                    () -> Path.of(OSU_FILE_PATH, String.valueOf(sid)));
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            log.error("download error", e);
            throw new HttpTipException("下载/解析文件出错");
        }
    }

    private Path getPath(long sid) throws IOException {
        final Path path = Path.of(OSU_FILE_PATH, String.valueOf(sid));
        boolean needDownload = false;

        if (!Files.isDirectory(path)) {
            BeatMapSet mapSet = osuApiService.getMapsetInfo(sid);
            doDownload(sid, mapSet);
            return path;
        }

        var list = osuFileLogRepository.findOsuFileLogBySid(sid);

        if (list.isEmpty()) {
            BeatMapSet mapSet = osuApiService.getMapsetInfo(sid);
            doDownload(sid, mapSet);
            return path;
        }

        var firstBeatmap = list.getFirst();
        // rank/approved/loved 直接不用更新
        if (
                Objects.nonNull(firstBeatmap.getStatus()) &&
                        (firstBeatmap.getStatus() == 1
                                || firstBeatmap.getStatus() == 2
                                || firstBeatmap.getStatus() == 4)
                        && !needDownload
        ) {
            return path;
        }

        BeatMapSet mapSet = osuApiService.getMapsetInfo(sid);

        // 修补以前的 rank 图, 补上日期
        if (Objects.isNull(firstBeatmap.getStatus())) {
            list.forEach(s -> {
                s.setStatus(mapSet.getStatus());
                s.setLast(mapSet.getLastUpdated().toLocalDateTime());
            });
            osuFileLogRepository.saveAll(list);
        }

        if (mapSet.getLastUpdated().toLocalDateTime()
                .isAfter(firstBeatmap.getLast().plusMinutes(2))) {
            doDownload(sid, mapSet);
        }

        return path;
    }

    /**
     * 将多个 .osz 文件打包为一个 .zip 文件 并写入到输出流
     *
     * @param sids sid, 用于打包
     * @throws IOException 当下载出错/并不存在该谱面时报错
     */
    public void zipOsuFiles(OutputStream out, long... sids) throws IOException {
        var zipOut = new ZipArchiveOutputStream(out);
        try {
            for (var sid : sids) {
                var zip = new ZipArchiveEntry(sid + ".osz");
                zipOut.putArchiveEntry(zip);
                outOsuZipFile(sid, zipOut);
            }
        } finally {
            zipOut.flush();
            zipOut.finish();
            zipOut.closeArchiveEntry();
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
            var zipOut = new ZipArchiveOutputStream(outStream);
            files.forEach((key, out) -> {
                try {
                    var zip = new ZipArchiveEntry(key + ".osz");
                    zipOut.putArchiveEntry(zip);
                    out.write(zipOut);
                } catch (IOException e) {
                    log.error("写入文件时异常: ", e);
                }
            });
            zipOut.closeArchiveEntry();
            zipOut.flush();
            zipOut.finish();
        };
    }

    public interface FileOut {
        void write(OutputStream out) throws IOException;
    }

    /**
     * 下载时通过 zip 流 写入所有文件, 包括音效/背景
     *
     * @throws IOException 写入异常
     */
    private int loopWriteFile(ZipArchiveInputStream zip, String basePath, HashMap<String, Path> fileMap) throws IOException {
        ZipArchiveEntry zipFile;
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

    private void writeDirToZip(ZipArchiveOutputStream zipOut, Path dir, String basePath) throws IOException {
        try (var allFile = Files.list(dir)) {
            for (var file : allFile.toList()) {
                if (Files.isDirectory(file)) {
                    writeDirToZip(zipOut, file, basePath + file.getFileName() + "/");
                    continue;
                }
                var zip = new ZipArchiveEntry(basePath + file.getFileName().toString());
                zipOut.putArchiveEntry(zip);
                IOUtils.copy(Files.newInputStream(file), zipOut);
                zipOut.closeArchiveEntry();
            }
        }
    }

    public void removeFile(long delSid) {
        List<OsuFileRecord> files;
        try {
            Path path = Path.of(OSU_FILE_PATH, String.valueOf(delSid));

            files = osuFileLogRepository.findOsuFileLogBySid(delSid);
            if (!CollectionUtils.isEmpty(files) && OSU_COPY_DIR.isPresent()) {
                var directory = OSU_COPY_DIR.get();
                files.stream()
                        .map(f -> directory.resolve(f.getBid() + ".osu"))
                        .forEach(path1 -> {
                            try {
                                Files.deleteIfExists(path1);
                            } catch (IOException ignore) {
                            }
                        });
            }
            if (Files.isDirectory(path)) {
                FileSystemUtils.deleteRecursively(path);
            }
        } catch (IOException e) {
            log.error("清空 map 文件夹出错", e);
        }
        osuFileLogRepository.deleteAllBySid(delSid);
    }

    public BeatmapSetCount getCount() {
        int all = osuFileLogRepository.countAll();
        int allSet = osuFileLogRepository.countBySid();
        return new BeatmapSetCount(allSet, all);
    }

    public record BeatmapSetCount(int countMapSet, int countBeatmap) {
    }

    public int rebuildLink() {
        Pageable pageable = PageRequest.of(0, 300);
        Page<OsuFileRecord> page;
        int size = 0;
        do {
            page = osuFileLogRepository.queryAll(pageable);
            pageable = page.nextOrLastPageable();
            for (var data : page) {
                copyLink(
                        data.getBid(),
                        Path.of(OSU_FILE_PATH, String.valueOf(data.getSid()), data.getFile())
                );
                size++;
            }
        } while (page.hasNext());
        return size;
    }

    /**
     * 将创建符号链接到 复制文件夹里
     *
     * @param bid    bid
     * @param source 源文件路径
     */
    public void copyLink(long bid, Path source) {
        if (OSU_COPY_DIR.isEmpty()) return;

        var target = OSU_COPY_DIR.map(p -> p.resolve(bid + ".osu")).get();

        if (Files.isSymbolicLink(target)) return;

        if (Files.isRegularFile(target)) {
            try {
                Files.deleteIfExists(target);
            } catch (IOException e) {
                log.error("删除原文件出错", e);
            }
        }

        if (!Files.isRegularFile(source)) {
            log.error("源文件不存在: [{}]", source);
        }

        try {
            Files.createSymbolicLink(target, source);
        } catch (IOException e) {
            log.error("创建符号连接出错", e);
        }
    }

    public int clearGraveyardFile() {
        var list = osuFileLogRepository.queryByGraveyard(100);
        for (var log : list) {
            removeFile(log.getSid());
        }
        return list.size();
    }
}
