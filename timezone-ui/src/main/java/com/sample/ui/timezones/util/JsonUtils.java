package com.sample.ui.timezones.util;

import static com.sample.ui.timezones.util.SharedConstants.UNABLE_PARSE_JSON;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class JsonUtils {

    private static ObjectMapper mapper = new Jackson2ObjectMapperBuilder().failOnEmptyBeans(false)
            .failOnUnknownProperties(false).featuresToEnable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
            .featuresToEnable(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
            .featuresToEnable(JsonParser.Feature.ALLOW_COMMENTS).build();

    private JsonUtils() {
    }

    /**
     * Converts JSON representation of type T to an T instance
     *
     * @param request
     *            JSON request
     *
     * @return An instance of T type
     */
    public static <T> T jsonToObject(String request, Class<T> type) {
        try {
            return mapper.readValue(request, type);
        } catch (IOException e) {
            throw new RuntimeException(UNABLE_PARSE_JSON, e);
        }
    }

    /**
     * Converts JSON representation of type T to list of T instances
     *
     * @param request
     *            JSON request
     *
     * @return A list of T type
     */
    public static <T> List<T> jsonToList(String request, Class<T> type) {
        try {
            return mapper.readValue(request, TypeFactory.defaultInstance().constructCollectionType(List.class, type));
        } catch (IOException e) {
            throw new RuntimeException(UNABLE_PARSE_JSON, e);
        }
    }

    /**
     * Converts JSON representation to hash map of key/value pairs
     *
     * @param request
     *            JSON request
     *
     * @return An instance of T type
     */
    public static Map<String, String> jsonToMap(String request) {
        try {
            JsonNode rootNode = mapper.readTree(request);
            Map<String, String> result = new HashMap<>();
            Iterator<Map.Entry<String, JsonNode>> fieldsIterator = rootNode.fields();
            while (fieldsIterator.hasNext()) {
                Map.Entry<String, JsonNode> field = fieldsIterator.next();
                result.put(field.getKey(), mapper.writeValueAsString(field.getValue()));
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(UNABLE_PARSE_JSON, e);
        }
    }

    /**
     * Converts JSON serializable object to JSON
     *
     * @param jsonSerializableObject
     *            Object to be converted to JSON
     *
     * @return JSON {@link String}
     */
    public static String objectToJson(Object jsonSerializableObject) {
        try {
            return mapper.writeValueAsString(jsonSerializableObject);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(UNABLE_PARSE_JSON, e);
        }
    }

}
