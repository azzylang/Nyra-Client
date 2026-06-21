package de.constt.nyra.client.utils;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

public class CacheUtils {
    private static final Path BASE_CACHE_DIR = FabricLoader.getInstance().getConfigDir().resolve("scalare/.cache");
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    static {
        // Ensure directory exists on class load
        try {
            Files.createDirectories(BASE_CACHE_DIR);
        } catch (IOException e) {
            System.err.println("Failed to create cache directory: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Path getCacheDir() {
        return BASE_CACHE_DIR;
    }

    public static Path getCacheFile(String filename) {
        return BASE_CACHE_DIR.resolve(filename);
    }

    public static void save(String filename, Object data) {
        try {
            Path file = getCacheFile(filename);

            // Double-check parent directories exist
            if (file.getParent() != null) {
                Files.createDirectories(file.getParent());
            }

            String json = GSON.toJson(data);
            Files.writeString(file, json);

            System.out.println("[Scalare] Saved cache: " + file.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save cache " + filename + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static <T> T load(String filename, Type type, Supplier<T> defaultSupplier) {
        Path file = getCacheFile(filename);
        if (!Files.exists(file)) {
            System.out.println("[Scalare] Cache file not found: " + file.toAbsolutePath());
            return defaultSupplier.get();
        }

        try {
            String content = Files.readString(file);
            System.out.println("[Scalare] Loaded cache: " + file.toAbsolutePath());
            return GSON.fromJson(content, type);
        } catch (Exception e) {
            System.err.println("Failed to load cache " + filename + ": " + e.getMessage());
            e.printStackTrace();
            return defaultSupplier.get();
        }
    }

    public static <T> T load(String filename, Class<T> clazz, Supplier<T> defaultSupplier) {
        return load(filename, (Type) clazz, defaultSupplier);
    }

    public static boolean exists(String filename) {
        return Files.exists(getCacheFile(filename));
    }

    public static void delete(String filename) {
        try {
            Files.deleteIfExists(getCacheFile(filename));
        } catch (IOException e) {
            System.err.println("Failed to delete cache " + filename + ": " + e.getMessage());
        }
    }

    public static Gson getGson() {
        return GSON;
    }
}