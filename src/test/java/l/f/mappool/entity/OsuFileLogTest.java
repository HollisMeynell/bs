package l.f.mappool.entity;

import jakarta.annotation.Resource;
import l.f.mappool.dao.FileLogDao;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class OsuFileLogTest {
    @Resource
    FileLogDao fileLogDao;

    @Test
    void testWriteZipFiles() throws IOException {
        try(var out = new FileOutputStream(Path.of("D:\\file\\test\\x.zip").toFile());) {
            fileLogDao.zipOsuFiles(out, 1456709, 503213, 382400);
        }
    }
    @Test
    void testWriteOneZipFile() throws IOException {
        try(var out = new FileOutputStream(Path.of("D:\\file\\test\\x2.zip").toFile());) {
            fileLogDao.outOsuZipFile( 382400, out);
        }
    }
}