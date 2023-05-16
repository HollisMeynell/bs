package l.f.mappool.config;

import l.f.mappool.MapPoolApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;

@Component
@Configuration
public class ApplicationConfig {

    @Bean
    public RestTemplate defaultRest() {
        var tempFactory = new OkHttp3ClientHttpRequestFactory();
        tempFactory.setConnectTimeout(3 * 60 * 1000);
        tempFactory.setReadTimeout(3 * 60 * 1000);
        var template = new RestTemplate(tempFactory);
        template.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
                String body = null;
                try {
                    body = new String(response.getBody().readAllBytes());
                } catch (IOException e) {
                    body = "no body";
                }
                MapPoolApplication.log.error("http 请求出错 {} {},path: {} \n请求链接: {}\n请求方式: {}\n{}\n{}",
                        response.getStatusCode().value(),
                        response.getStatusText(),
                        url.getPath(),
                        url,
                        method,
                        response.getHeaders(),
                        body
                );
                super.handleError(url, method, response);
            }
        });
        return template;
    }
}
