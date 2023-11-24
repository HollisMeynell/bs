package l.f.mappool.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
@SuppressWarnings("unused")
public class JsonUtil {
    private static final ObjectMapper mapper = JsonMapper.builder().build().registerModules(new JavaTimeModule());

    public static <T>String objectToJsonPretty(T obj){
        if(obj == null){
            return "{}";
        }
        try {
            if (obj instanceof String s) {
                return s;
            }

            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse Object to Json error",e);
            return "{error}";
        }
    }
    public static <T>String objectToJson(T obj){
        if(obj == null){
            return "{}";
        }
        try {
            if (obj instanceof String s) {
                return s;
            }

            return  mapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse Object to Json error",e);
            return "{error}";
        }
    }

    public static <T> T parseObject(String body, Class<T> clazz) {
        JsonNode node;
        try {
            node = mapper.readTree(body);
            return parseObject(node, clazz);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @SneakyThrows
    public static <T> T parseObject(JsonNode body, Class<T> clazz) {
        return mapper.treeToValue(body, clazz);
    }

    public static <T> List<T> parseObjectList(String body, Class<T> clazz) {
        JsonNode node;
        try {
            node = mapper.readTree(body);
            return parseObjectList(node, clazz);
        } catch (IOException | RuntimeException e) {
            log.error(e.getMessage(), e);
        }
        return List.of();
    }

    public static <T> List<T> parseObjectList(JsonNode body, Class<T> clazz) {
        if (body == null || !body.isArray()) {
            throw new RuntimeException("不能为空或非数组");
        }
        return mapper.convertValue(
                body,
                mapper.getTypeFactory().constructCollectionType(List.class, clazz)
        );
    }

}
