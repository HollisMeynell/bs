package l.f.mappool.controller;

import jakarta.servlet.http.HttpServletRequest;
import l.f.mappool.config.interceptor.Open;
import l.f.mappool.dao.FileLogDao;
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

@Open
@Slf4j
@Controller
@CrossOrigin
@ResponseBody
@RequestMapping(value = "/api/file", produces = "application/json;charset=UTF-8")
public class FileApi {
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

    @PostMapping(value = "/stream/{name}", consumes = {MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public DataVo<String> upload(@PathVariable("name") String name, HttpServletRequest request) throws IOException {
        String fileName = URLDecoder.decode(name, StandardCharsets.UTF_8);
        String fileKey = fileLogDao.writeFile(fileName, request.getInputStream());
        return new DataVo<>("ok", fileKey);
    }

    @GetMapping(value = "/image/{key}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable("key")String key) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", fileLogDao.getFileName(key));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(fileLogDao.getData(key), headers, HttpStatus.OK);
    }
    @GetMapping(value = "/download/{key}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] getFile(@PathVariable("key")String key) throws IOException {
        return fileLogDao.getData(key);
    }

    private FileLogDao fileLogDao;

    @Autowired
    public FileApi(FileLogDao fileLogDao) throws IOException {
        this.fileLogDao = fileLogDao;
    }
}
