package de.constt.nyra.client.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class JsonUtils {
    private static final Gson gson = new Gson();

    public static JsonObject parse(String json) {
        return gson.fromJson(json, JsonObject.class);
    }
}
