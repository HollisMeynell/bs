package l.f.mappool.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.util.MultiValueMap;

import javax.validation.constraints.NotEmpty;
import java.util.Map;

@Data
@Accessors(chain = true)
public class ProxyDto {
    @NotEmpty
    String method = "GET";
    @NotEmpty
    String url;

    Map<String, String> headers;
    Object              body;
    Map<String, String> parameter;
}
