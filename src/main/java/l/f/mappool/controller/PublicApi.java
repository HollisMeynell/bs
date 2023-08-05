package l.f.mappool.controller;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import l.f.mappool.config.interceptor.Open;
import l.f.mappool.dao.MapPoolDao;
import l.f.mappool.dto.ProxyDto;
import l.f.mappool.exception.HttpError;
import l.f.mappool.service.OsuGetService;
import l.f.mappool.vo.DataVo;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Open
@Controller
@CrossOrigin
@ResponseBody
@RequestMapping(value = "/api/public", produces = "application/json;charset=UTF-8")
public class PublicApi {
    @Resource
    OsuGetService osuService;
    @Resource
    MapPoolDao    mapPoolDao;
    @Resource
    RestTemplate  restTemplate;

    /**
     * 获取公开图池
     */
    @GetMapping("getAllPool")
    DataVo getAllPool() {
        return new DataVo(mapPoolDao.getPublicPool());
    }

    /**
     * 获取绑定链接
     * @return 链接
     */
    @GetMapping("getOauthUrl")
    DataVo<String> getOauthUrl() {
        return new DataVo<>(osuService.getOauthUrl("test"));
    }

    /**
     * 前端代理, 只能支持 json 的接口
     * @param config 请求配置
     * @return 代理数据
     * @throws HttpError 请求异常
     */
    @PostMapping("proxy")
    Object proxy(@RequestBody @Validated ProxyDto config) throws HttpError {
        var method = HttpMethod.valueOf(config.getMethod().toUpperCase());
        HttpHeaders headers = new HttpHeaders();
        if (config.getHeaders() != null && config.getHeaders().size() > 0) {
            headers.setAll(config.getHeaders());
        }
        HttpEntity<Object> httpEntity;
        if (method == HttpMethod.GET || config.getBody() == null) {
            httpEntity = new HttpEntity<>(headers);
        } else {
            httpEntity = new HttpEntity<>(config.getBody(), headers);
        }

        UriComponentsBuilder uri = UriComponentsBuilder.fromHttpUrl(config.getUrl());
        if (config.getParameter() != null && config.getParameter().size() > 0) {
            for (var i : config.getParameter().entrySet()) {
                uri.queryParam(i.getKey(), i.getValue());
            }
        }
        var rep = restTemplate.exchange(
                uri.toUriString(),
                method,
                httpEntity,
                JsonNode.class
        );
        if (rep.getStatusCode().is2xxSuccessful()) {
            return rep.getBody();
        }
        throw new HttpError(rep.getStatusCode().value(), "请求失败");
    }
}
