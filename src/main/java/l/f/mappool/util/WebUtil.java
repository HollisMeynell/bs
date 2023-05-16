package l.f.mappool.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class WebUtil {
    private static final Logger log = LoggerFactory.getLogger(WebUtil.class);
    public static final ObjectMapper objectMapper = JsonMapper.builder().build();

    public static void writeObjectToResponse(HttpServletResponse response, Object obj) throws IOException {
        try (var write = response.getWriter()) {
            response.setContentType("application/json; charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            write.print(objectToJson(obj));
            write.flush();
        }
    }

    public static <T> String objectToJsonPretty(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse Object to Json error", e);
            e.printStackTrace();
            return null;
        }
    }

    public static <T> String objectToJson(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse Object to Json error", e);
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T jsonToObject(String src, Class<T> clazz) {
        if (src == null || "".equals(src.trim()) || clazz == null) {
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T) src : objectMapper.readValue(src, clazz);
        } catch (Exception e) {
            log.warn("Parse Json to Object error", e);
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T parseObject(JsonNode node, Class<T> clazz) {
        try {
            return objectMapper.treeToValue(node, clazz);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
