package l.f.mappool.controller;

import jakarta.servlet.http.HttpServletRequest;
import l.f.mappool.config.interceptor.Open;
import l.f.mappool.dao.FileLogDao;
import l.f.mappool.entity.FileLog;
import l.f.mappool.exception.LogException;
import l.f.mappool.vo.DataVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Open
@Slf4j
@Controller
@CrossOrigin
@ResponseBody
@RequestMapping(value = "/api/file", produces = "application/json;charset=UTF-8")
public class FileApi {
    /**
     * 上传文件
     * @param file 文件结构 formData{ filename: xxx, ...}
     * @return {filename: key}
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DataVo<Map<String, String>> upload(MultipartHttpServletRequest file) {
        final Map<String, String> files = new HashMap<>();
        file.getFileMap().forEach((key, value) -> {
            try {
                String fileKey = fileLogDao.writeFile(value.getName(), value.getBytes());
                files.put(value.getName(), fileKey);
            } catch (IOException ex) {
                log.error("文件写入错误", ex);
            }
        });
        return new DataVo<>(files);
    }

    /**
     * 上传单个文件
     * @param name 文件名
     * @return key
     * @throws IOException 文件写入失败
     */
    @PostMapping(value = "/stream/{name}", consumes = {MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public DataVo<String> upload(@PathVariable("name") String name, HttpServletRequest request) throws IOException {
        String fileName = URLDecoder.decode(name, StandardCharsets.UTF_8);
        String fileKey = fileLogDao.writeFile(fileName, request.getInputStream());
        return new DataVo<>("ok", fileKey);
    }

    /**
     * 加载图片
     * @param key 文件key
     */
    @GetMapping(value = "/image/{key}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable("key")String key) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.inline().filename(fileLogDao.getFileName(key)).build());
        headers.setContentType(MediaType.IMAGE_PNG);
        try {
            Optional<FileLog> fileLog = fileLogDao.getFileLog(key);
            if (fileLog.isEmpty()){
                throw new IOException();
            }
            byte[] data = fileLogDao.getData(fileLog.get());
            headers.setContentLength(data.length);
            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (IOException e) {
            throw new LogException("文件已失效...", 404);
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping(value = "/delete")
    public DataVo<Boolean> deleteFile(@RequestParam("key") String key) {
        try {
            fileLogDao.deleteFile(key);
        } catch (IOException e) {
            return new DataVo<>(Boolean.FALSE).setMessage("删除失败: " + e.getMessage()).setCode(502);
        }
        return new DataVo<>(Boolean.TRUE);
    }

    /**
     * 下载文件
     * @param key 文件key
     */
    @GetMapping(value = "/download/{key}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] getFile(@PathVariable("key")String key) throws IOException {
        try {
            Optional<FileLog> fileLog = fileLogDao.getFileLog(key);
            if (fileLog.isEmpty()){
                throw new IOException();
            }
            return fileLogDao.getData(fileLog.get());
        } catch (IOException e) {
            throw new LogException("文件已失效...", 404);
        }
    }

    private final FileLogDao fileLogDao;

    @Autowired
    public FileApi(FileLogDao fileLogDao) {
        this.fileLogDao = fileLogDao;
    }
}
