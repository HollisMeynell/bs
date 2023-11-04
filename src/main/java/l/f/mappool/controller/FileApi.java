package l.f.mappool.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import l.f.mappool.config.interceptor.Open;
import l.f.mappool.entity.file.FileRecord;
import l.f.mappool.exception.HttpError;
import l.f.mappool.exception.HttpTipException;
import l.f.mappool.service.BeatmapFileService;
import l.f.mappool.service.FileService;
import l.f.mappool.vo.DataVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Open
@Slf4j
@Controller
@ResponseBody
@RequestMapping(value = "/api/file", produces = "application/json;charset=UTF-8")
public class FileApi {
    /**
     * 上传文件
     *
     * @param file 文件结构 formData{ filename: xxx, ...}
     * @return {filename: key}
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DataVo<Map<String, String>> upload(MultipartHttpServletRequest file) {
        final Map<String, String> files = new HashMap<>();
        file.getFileMap().forEach((key, value) -> {
            try {
                String fileKey = fileService.writeFile(value.getName(), value.getBytes());
                files.put(value.getName(), fileKey);
            } catch (IOException ex) {
                log.error("文件写入错误", ex);
            }
        });
        return new DataVo<>(files);
    }

    /**
     * 上传单个文件
     *
     * @param name 文件名
     * @return key
     * @throws IOException 文件写入失败
     */
    @PostMapping(value = "/stream/{name}", consumes = {MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public DataVo<String> upload(@PathVariable("name") String name, HttpServletRequest request) throws IOException {
        String fileName = URLDecoder.decode(name, StandardCharsets.UTF_8);
        String fileKey = fileService.writeFile(fileName, request.getInputStream());
        return new DataVo<>("ok", fileKey);
    }

    /**
     * 加载图片
     *
     * @param key 文件key
     */
    @GetMapping(value = "/image/{key}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable("key") String key) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.inline().filename(fileService.getFileName(key)).build());
        headers.setContentType(MediaType.IMAGE_PNG);
        try {
            Optional<FileRecord> fileLog = fileService.getFileRecord(key);
            if (fileLog.isEmpty()) {
                throw new IOException();
            }
            byte[] data = fileService.getData(fileLog.get());
            headers.setContentLength(data.length);
            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (IOException e) {
            throw new HttpTipException(400, "文件已失效...");
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping(value = "/delete")
    public DataVo<Boolean> deleteFile(@RequestParam("key") String key) {
        try {
            fileService.deleteFile(key);
        } catch (IOException e) {
            return new DataVo<>(Boolean.FALSE).setMessage("删除失败: " + e.getMessage()).setCode(502);
        }
        return new DataVo<>(Boolean.TRUE);
    }

    /**
     * 下载文件
     *
     * @param key 文件key
     */
    @GetMapping(value = "/download/{key}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] getFile(@PathVariable("key") String key) {
        try {
            Optional<FileRecord> fileLog = fileService.getFileRecord(key);
            if (fileLog.isEmpty()) {
                throw new IOException();
            }
            return fileService.getData(fileLog.get());
        } catch (IOException e) {
            throw new HttpTipException(400, "文件已失效...");
        }
    }

    /**
     * 下载素材
     *
     * @param name 位于 static 的路径
     * @return
     */
    @GetMapping(value = "/static/{name}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] getStaticFile(@PathVariable("name") String name) throws HttpError {
        return fileService.getStaticFile(name);
    }

    /**
     * 下载谱面bg
     */
    @Open
    @GetMapping(value = "/map/{type}/{bid}")
    public void downloadMapBGFile(@PathVariable Long bid, @PathVariable String type,
                                  @RequestHeader(value = "Range", required = false) String range,
                                  HttpServletResponse response) throws IOException {
        String mediaType;
        var atype = switch (type) {
            case "bg" -> {
                mediaType = "image/jpeg";
                yield BeatmapFileService.Type.BACKGROUND;
            }
            case "song" -> {
                mediaType = "audio/mpeg";
                yield BeatmapFileService.Type.AUDIO;
            }
            case "osufile" -> {
                mediaType = "application/octet-stream";
                yield BeatmapFileService.Type.FILE;
            }
            default -> throw new HttpTipException(400, "未知类型");
        };
        var in = new RandomAccessFile(fileService.getPathByBid(bid, atype).toFile(), "r");
        var size = in.length();
        long needWriteSize;
        if (Objects.isNull(range)) {
            response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(size));
            response.setStatus(200);
            needWriteSize = size;
        } else {
            var f = range.substring(6).split("-");
            long bytesStart;
            long bytesEnd;
            if (f.length == 2) {
                bytesStart = Long.parseLong(f[0]);
                bytesEnd = Long.parseLong(f[1]);
            } else if (f.length == 1) {
                bytesStart = Long.parseLong(f[0]);
                bytesEnd = Math.min(bytesStart + 1048576, size - 1);
            } else {
                throw new HttpTipException(400, "Range error");
            }
            String rangeValue = String.format("bytes %s-%s/%s", bytesStart, bytesEnd, size);
            in.seek(bytesStart);
            needWriteSize = bytesEnd - bytesStart + 1;
            response.setStatus(206);
            response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(needWriteSize));
            response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
            response.setHeader(HttpHeaders.CONTENT_RANGE, rangeValue);
//            return ResponseEntity
//                    .ok()
//                    .contentLength(bytesEnd - bytesStart)
//                    .contentType(MediaType.valueOf(mediaType))
//                    .header(HttpHeaders.ACCEPT_RANGES,
//                            "bytes")
//                    .header(HttpHeaders.CONTENT_DISPOSITION,
//                            String.format("attachment; filename=\"%s\"", bid))
//                    .header(HttpHeaders.CONTENT_RANGE,
//                            rangeValue)
//                    .body(new InputStreamResource(in));
        }

        response.setHeader(HttpHeaders.CONTENT_TYPE, mediaType);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", bid));
        try (in;
             var out = response.getOutputStream()) {

            log.debug("下载文件:range[{}] type[{}] id[{}]", range, atype, bid);
            int i;
            // 已写入输出流的字节数
            int alreadyWriteSize = 0;
            byte[] data = new byte[1024];
            while ((i = in.read(data)) != -1 && alreadyWriteSize < needWriteSize) {
                out.write(data, 0, i);
                alreadyWriteSize += i;
                out.flush();
            }
        } catch (Exception e) {
            log.error("下载出现异常", e);
        }

    }


    @Open
    @GetMapping("/map/{sid}")
    public void downloadMapFile(@PathVariable Long sid, HttpServletResponse response) throws IOException {
        var fileOut = fileService.outOsuZipFile(sid);
        try (var out = getResponseOut(response, sid + ".osz")) {
            fileOut.write(out);
        } catch (IOException e) {
            log.error("导出 map 出错: ");
            throw new HttpTipException(500, "写入流出错");
        }
    }

    @Open
    @GetMapping("/maps/{sidStr}")
    public void downloadMapPackage(@PathVariable String sidStr, HttpServletResponse response) throws IOException {
        var s = sidStr.split("-");
        var ids = new long[s.length];
        for (int i = 0; i < s.length; i++) {
            ids[i] = Long.parseLong(s[i]);
        }
        var fileOut = fileService.zipOsuFiles(ids);

        try (var out = getResponseOut(response, "package.zip")) {
            fileOut.write(out);
        } catch (IOException e) {
            log.error("导出 maps 出错: ", e);
            throw new HttpTipException(500, "写入流出错");
        }
    }

    /**
     * 获取 http 响应的输出流
     *
     * @param response 响应对象
     * @param name     文件名
     * @return 响应输出流
     * @throws IOException 打开失败
     */
    private OutputStream getResponseOut(@NotNull HttpServletResponse response, String name) throws IOException {
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setCharacterEncoding("utf-8");
        if (name != null && !name.isBlank()) {
            response.setHeader("Content-Disposition", "attachment;filename=" + name);
        } else {
            response.setHeader("Content-Disposition", "attachment;filename=file");
        }
        return response.getOutputStream();
    }

    @Open(bot = true)
    @GetMapping("local/{type}/{bid}")
    public String getLocalPath(@PathVariable Long bid, @PathVariable String type) throws IOException {
        var atype = switch (type) {
            case "bg" -> BeatmapFileService.Type.BACKGROUND;
            case "song" -> BeatmapFileService.Type.AUDIO;
            case "osufile" -> BeatmapFileService.Type.FILE;
            default -> throw new HttpTipException(400, "未知类型");
        };
        try {
            return fileService.getPathByBid(bid, atype).toString();
        } catch (WebClientResponseException e) {
            throw new HttpTipException(400, e.getMessage());
        }
    }

    static int SUM = 0;

    @Open(bot = true, pub = false)
    @GetMapping("local/async/{bid}")
    public String getLocalPathAsync(@PathVariable Long bid, @RequestHeader("SET_ID") Long sid) throws IOException {
        log.info("异步任务: 开始下载 [{}]", sid);
        if (SUM < 50) {
            Thread.startVirtualThread(() -> {
                try {
                    fileService.outOsuZipFile(sid, null);
                } catch (IOException e) {
                    log.error("Async download osu file error", e);
                }
            });
            SUM++;
        }
        return "ok";
    }


    private final FileService fileService;

    @Autowired
    public FileApi(FileService fileService) {
        this.fileService = fileService;
    }
}
