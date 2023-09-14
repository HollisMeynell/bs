package l.f.mappool.controller;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import l.f.mappool.config.interceptor.Open;
import l.f.mappool.dao.MapPoolDao;
import l.f.mappool.dto.ProxyDto;
import l.f.mappool.entity.MapFeedback;
import l.f.mappool.entity.MapPool;
import l.f.mappool.exception.HttpError;
import l.f.mappool.service.MapPoolService;
import l.f.mappool.service.OsuApiService;
import l.f.mappool.vo.DataListVo;
import l.f.mappool.vo.DataVo;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.constraints.NotNull;
import java.util.List;

@Open
@Controller
@ResponseBody
@RequestMapping(value = "/api/public", produces = "application/json;charset=UTF-8")
public class PublicApi {
    @Resource
    OsuApiService osuService;
    @Resource
    MapPoolDao mapPoolDao;
    @Resource
    MapPoolService mapPoolService;
    @Resource
    WebClient webClient;
    @Resource
    WebClient osuApiWebClient;

    /**
     * 获取公开图池
     */
    @GetMapping("getAllPool")
    DataVo<List<MapPool>> getAllPool() {
        return new DataVo<>(mapPoolDao.getPublicPool());
    }

    /**
     * 获取绑定链接
     *
     * @return 链接
     */
    @GetMapping("getOauthUrl")
    DataVo<String> getOauthUrl() {
        return new DataVo<>(osuService.getOauthUrl("test"));
    }

    /**
     * 前端代理, 只能支持 json 的接口
     *
     * @param config 请求配置
     * @return 代理数据
     * @throws HttpError 请求异常
     */
    @PostMapping("proxy")
    Object proxy(@RequestBody @Validated ProxyDto config) throws HttpError {
        var method = HttpMethod.valueOf(config.getMethod().toUpperCase());

        UriComponentsBuilder uri = UriComponentsBuilder.fromHttpUrl(config.getUrl());
        if (config.getParameter() != null && config.getParameter().size() > 0) {
            for (var i : config.getParameter().entrySet()) {
                uri.queryParam(i.getKey(), i.getValue());
            }
        }

        WebClient client;
        if (config.getUrl().contains("ppy.sh")) {
            client = osuApiWebClient;
        } else {
            client = webClient;
        }
        var res = client.method(method)
                .uri((r) -> uri.build().toUri())
                .headers(headers -> {
                    if (config.getHeaders() != null && config.getHeaders().size() > 0) {
                        headers.setAll(config.getHeaders());
                    }
                });
        if (method != HttpMethod.GET && config.getBody() != null) {
            res.bodyValue(config.getBody());
        }

        try {
            var rep = res
                    .retrieve()
                    .onStatus(statusCode -> !statusCode.is2xxSuccessful(),
                            r -> {
                                var code = r.statusCode().value();
                                return r.bodyToMono(String.class).map(s -> new HttpError(code, s));
                            })
                    .toEntity(JsonNode.class)
                    .block();
            if (rep == null) {
                return null;
            }
            return rep.getBody();
        } catch (Exception e) {
            if (e.getCause() instanceof HttpError httpError) {
                throw httpError;
            }
            throw new HttpError(500, e.getMessage());
        }
    }
    @GetMapping("feedback")
    DataListVo<MapFeedback> getFeedback(@NotNull(message = "id 不能为空") @RequestParam("id") int itemId){
        var feedbacks = mapPoolService.getPublicFeedbackFromItem(itemId);
        return new DataListVo<MapFeedback>()
                .setTotalItems(feedbacks.size())
                .setPageSize(feedbacks.size())
                .setData(feedbacks);
    }
}
