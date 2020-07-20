package com.sql.analysis;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * 单例对象，解析json
 */
public class JsonAnalysis {

    private static JsonObject parse = null;

    private JsonAnalysis() {
    }

    private static JsonAnalysis instance = null;

    public static JsonAnalysis getInstance(String jsonFile) {
        if (instance == null) {
            synchronized (JsonAnalysis.class) {
                if (instance == null) {
                    instance = new JsonAnalysis();
                    JsonParser jsonParser = new JsonParser();
                    String jsonStr;
                    try {
                        jsonStr = FileUtils.readFileToString(new File(jsonFile), "utf-8");
                    } catch (IOException e) {
                        instance = null;
                        return null;
                    }
                    parse = jsonParser.parse(jsonStr).getAsJsonObject();
                }
            }
        }
        return instance;
    }
    /**
     * 用key获取值,若为数字，则不进行获取，直接返回
     * @param key 关键字或者数值
     * @return json中获取的结果
     */
    public String getValue(String key) {
        if (key == null) {
            return null;
        }
        try {
            // 如果为数字，则不进行取值
            Double.parseDouble(key);
            return key;
        } catch (NumberFormatException e) {
            return ParseJson(key);
        }
    }

    private String ParseJson(String key) {
        try {
            // 解析带.的层级
            if (key.contains(".")) {
                String[] split = key.split("\\.");
                JsonObject jsonElement = parse;
                for (int i = 0; i < split.length-1; i++) {
                    jsonElement = jsonElement.getAsJsonObject(split[i]);
                }
                // 层层解析后返回结果
                return jsonElement.getAsJsonPrimitive(split[split.length-1]).getAsString();
            } else {
                return parse.getAsJsonPrimitive(key).getAsString();
            }
        } catch (Exception e) {
            return null;
        }
    }

}


