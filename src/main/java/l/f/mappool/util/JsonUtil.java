package l.f.mappool.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
@SuppressWarnings("unused")
public class JsonUtil {
    public static final ObjectMapper DEFAULT_MAPPER = JsonMapper.builder()
            // 空 val 不报错
            .enable(JsonReadFeature.ALLOW_MISSING_VALUES)
            // 允许 key 是任意字符
            .enable(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES)
            // 支持尾随逗号
            .enable(JsonReadFeature.ALLOW_TRAILING_COMMA)
            // 支持单引号
            .enable(JsonReadFeature.ALLOW_SINGLE_QUOTES)
            // 支持字符串转义以及多行处理
            .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
            // 数字支持 无穷大, NaN
            .enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS)
            // 允许注释
            .enable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
            // 允许数字小数点在两端
            .enable(JsonReadFeature.ALLOW_LEADING_DECIMAL_POINT_FOR_NUMBERS)
            .enable(JsonReadFeature.ALLOW_TRAILING_DECIMAL_POINT_FOR_NUMBERS)
            // 允许数字前置加号
            .enable(JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS)
            // 序列化时忽略 null 字段
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .build()
            // 设置允许忽略未知的字段
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true)
            // 设置可见性
            .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
            // 默认使用驼峰转下划线命名
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .registerModules(new Hibernate6Module())
            .registerModules(new JavaTimeModule());


    public static <T>String objectToJsonPretty(T obj){
        if(obj == null){
            return "{}";
        }
        try {
            if (obj instanceof String s) {
                return s;
            }

            return DEFAULT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
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

            return DEFAULT_MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse Object to Json error",e);
            return "{error}";
        }
    }

    public static <T> T parseObject(String body, Class<T> clazz) {
        JsonNode node;
        try {
            node = DEFAULT_MAPPER.readTree(body);
            return parseObject(node, clazz);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @SneakyThrows
    public static <T> T parseObject(JsonNode body, Class<T> clazz) {
        return DEFAULT_MAPPER.treeToValue(body, clazz);
    }

    public static <T> List<T> parseObjectList(String body, Class<T> clazz) {
        JsonNode node;
        try {
            node = DEFAULT_MAPPER.readTree(body);
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
        return DEFAULT_MAPPER.convertValue(
                body,
                DEFAULT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz)
        );
    }

}
