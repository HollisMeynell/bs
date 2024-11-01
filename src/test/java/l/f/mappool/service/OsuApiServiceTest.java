package l.f.mappool.service;

import jakarta.annotation.Resource;
import l.f.mappool.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Slf4j
class OsuApiServiceTest {
    @Resource
    OsuApiService osuApiService;

    @Test
    void getMapsetInfo() {
        try {
            var set = osuApiService.getMapsetInfo(725853);
//            var set = osuApiService.getMapsetInfo(1198905L);
            log.info(JsonUtil.objectToJsonPretty(set));
        } catch (WebClientResponseException.NotFound e) {
            log.error("err: {}", e.getClass());
        }
    }

    @Test
    void getAllUser() {
        try {
            var users = osuApiService.getAllUser(List.of(15030466, 1793289));
            log.info(JsonUtil.objectToJsonPretty(users));
        } catch (WebClientResponseException.NotFound e) {
            log.error("err: {}", e.getClass());
        }
    }

    @Test
    void getMatches() {
        var m = osuApiService.getMatches();
        log.info(JsonUtil.objectToJsonPretty(m));
    }

    @Test
    void getMatchesInfo() {
        var m = osuApiService.getMatchesInfo(111203621L,
                Optional.empty(),
                Optional.empty(),
                100);
        log.info(JsonUtil.objectToJsonPretty(m));
    }
}
