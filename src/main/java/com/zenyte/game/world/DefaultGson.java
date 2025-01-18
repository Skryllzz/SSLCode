package com.zenyte.game.world;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zenyte.game.constants.GameConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public enum DefaultGson {
    ;

    /**
     * The Gson loader
     */
    private static final ThreadLocal<Gson> gson = ThreadLocal.withInitial(() ->
            new GsonBuilder()
                    .disableHtmlEscaping()
                    .setPrettyPrinting()
                    .create());

    public static Gson getGson() {
        return gson.get();
    }

    public static <T> T fromGsonString(String jsonString, Class<T> classOfT) {
        jsonString = jsonString.replace("%SERVER_NAME%", GameConstants.SERVER_NAME);
        return getGson().fromJson(jsonString, classOfT);
    }

    public static <T> T fromGson(BufferedReader reader, Class<T> classOfT) {
        String jsonString = reader.lines().collect(Collectors.joining());
        return fromGsonString(jsonString, classOfT);
    }

    public static <T> T fromGson(Path filePath, Class<T> classOfT) throws IOException {
        String jsonString = Files.readString(filePath);
        return fromGsonString(jsonString, classOfT);
    }

    public static <T> T fromGson(File file, Class<T> classOfT) throws IOException {
        return fromGson(file.toPath(), classOfT);
    }

    public static <T> T fromGson(String filePath, Class<T> classOfT) throws IOException {
        return fromGson(Path.of(filePath), classOfT);
    }

}
