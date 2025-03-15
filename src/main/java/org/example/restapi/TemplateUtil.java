package org.example.restapi;

import java.util.HashMap;
import java.util.Map;

public class TemplateUtil {
    public static Map<String, Object> map(String key, Object value) {
        Map<String, Object> model = new HashMap<>();
        model.put(key, value);
        return model;
    }
}