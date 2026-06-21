package de.constt.nyra.client.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import de.constt.nyra.client.roots.implementations.ModuleImplementation;
import de.constt.nyra.client.roots.implementations.SettingImplementation;
import de.constt.nyra.client.roots.modules.ModuleManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleCacheUtils {
    private static final String CACHE_FILENAME = "modules.json";
    private static final Type MODULE_DATA_LIST_TYPE = new TypeToken<List<ModuleData>>(){}.getType();

    private static class ModuleData {
        String name;
        boolean enabled;
        Map<String, JsonElement> settings = new HashMap();

        ModuleData() {}

        ModuleData(String name, boolean enabled, Map<String, JsonElement> settings) {
            this.name = name;
            this.enabled = enabled;
            this.settings = settings;
        }
    }

    public static void saveAll() {
        List<ModuleData> dataList = new ArrayList<>();

        for (ModuleImplementation module : ModuleManager.getModules()) {
            dataList.add(serializeModule(module));
        }

        CacheUtils.save(CACHE_FILENAME, dataList);
    }

    public static void loadAll() {
        List<ModuleData> dataList = CacheUtils.load(
                CACHE_FILENAME,
                MODULE_DATA_LIST_TYPE,
                ArrayList::new
        );

        if (dataList.isEmpty()) return;

        for (ModuleData data : dataList) {
            ModuleImplementation module = findModuleByName(data.name);
            if (module == null) continue;

            boolean shouldBeEnabled = data.enabled;
            if (shouldBeEnabled && !module.getEnabledStatus()) {
                module.toggle();
            } else if (!shouldBeEnabled && module.getEnabledStatus()) {
                module.toggle();
            }

            applySettings(module, data.settings);
        }
    }

    public static void saveModule(ModuleImplementation module) {
        if (!CacheUtils.exists(CACHE_FILENAME)) {
            saveAll();
            return;
        }

        List<ModuleData> dataList = CacheUtils.load(
                CACHE_FILENAME,
                MODULE_DATA_LIST_TYPE,
                ArrayList::new
        );

        dataList.removeIf(data -> data.name.equals(module.getTranslatableText()));
        dataList.add(serializeModule(module));

        CacheUtils.save(CACHE_FILENAME, dataList);
    }

    private static ModuleData serializeModule(ModuleImplementation module) {
        Map<String, JsonElement> settingsMap = new HashMap<>();

        for (SettingImplementation<?> setting : module.getSettings()) {
            settingsMap.put(setting.getName(), serializeSetting(setting));
        }

        return new ModuleData(
                module.getTranslatableText(),
                module.getEnabledStatus(),
                settingsMap
        );
    }

    private static JsonElement serializeSetting(SettingImplementation<?> setting) {
        Object value = setting.get();
        if (value instanceof Boolean) {
            return new JsonPrimitive((Boolean) value);
        } else if (value instanceof Number) {
            return new JsonPrimitive((Number) value);
        } else if (value instanceof String) {
            return new JsonPrimitive((String) value);
        } else {
            return new JsonPrimitive(value.toString());
        }
    }

    private static void applySettings(ModuleImplementation module, Map<String, JsonElement> settingsMap) {
        for (Map.Entry<String, JsonElement> entry : settingsMap.entrySet()) {
            SettingImplementation<?> setting = module.getSetting(entry.getKey());
            if (setting == null) continue;

            try {
                applySettingValue(setting, entry.getValue());
            } catch (Exception e) {
                System.err.println("Failed to apply setting " + entry.getKey() + ": " + e.getMessage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void applySettingValue(SettingImplementation<T> setting, JsonElement value) {
        Object currentValue = setting.get();
        T newValue = null;

        if (currentValue instanceof Boolean) {
            newValue = (T) Boolean.valueOf(value.getAsBoolean());
        } else if (currentValue instanceof Integer) {
            newValue = (T) Integer.valueOf(value.getAsInt());
        } else if (currentValue instanceof Double) {
            newValue = (T) Double.valueOf(value.getAsDouble());
        } else if (currentValue instanceof Float) {
            newValue = (T) Float.valueOf(value.getAsFloat());
        } else if (currentValue instanceof String) {
            newValue = (T) value.getAsString();
        } else if (currentValue instanceof Long) {
            newValue = (T) Long.valueOf(value.getAsLong());
        }

        if (newValue != null) {
            ((SettingImplementation<T>) setting).set(newValue);
        }
    }

    private static ModuleImplementation findModuleByName(String name) {
        for (ModuleImplementation module : ModuleManager.getModules()) {
            if (module.getTranslatableText().equals(name)) {
                return module;
            }
        }
        return null;
    }
}