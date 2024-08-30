package l.f.mappool.config;

import jakarta.annotation.Resource;
import l.f.mappool.service.LocalFileService;
import l.f.mappool.service.OsuFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
public class SchedulingTasks {
    @Resource
    OsuFileService   osuFileService;
    @Resource
    LocalFileService localFileService;

    //@Scheduled(cron = "0(秒) 0(分) 0(时) *(日) *(月) *(周) *(年,可选)")

    // 每天清理超过30天未访问的文件
    @Scheduled(cron = "0 0 3 1 * *")
    public void cleanFile() {
        log.info("开始清理文件");
        int deleteCount = localFileService.deleteAllOldFile();
        log.info("清理文件完成, 删除文件数: {}", deleteCount);
    }

    @Scheduled(cron = "0 0 3 1/3 * *")
    public void removeOsuFile() {
        log.info("开始清理osu文件");
        var size = osuFileService.clearGraveyardFile();
        log.info("清理osu文件完成, 删除文件数: {}", size);
    }
}
