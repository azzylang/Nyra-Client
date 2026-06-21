package de.constt.nyra.client.roots.implementations;

import de.constt.nyra.client.roots.implementations.settings.BooleanSettingImplementation;
import de.constt.nyra.client.utils.ModuleAnnotationUtils;
import de.constt.nyra.client.utils.ModuleCacheUtils;
import net.minecraft.network.protocol.Packet;
import org.lwjgl.glfw.GLFW;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class ModuleImplementation {

    protected boolean enabled = false;
    public int keyBindingCode = GLFW.GLFW_KEY_UNKNOWN;
    private final Map<String, SettingImplementation<?>> settings = new HashMap<>();

    protected ModuleImplementation() {
        registerSetting(new BooleanSettingImplementation("Disable on toggle", false));
    }

    protected void registerSetting(SettingImplementation<?> setting) {
        settings.put(setting.getName(), setting);
        setting.addChangeListener(this::onSettingChanged);
    }

    protected void onSettingChanged(SettingImplementation<?> setting) {
        ModuleCacheUtils.saveModule(this);
    }

    // ADD THIS METHOD
    public void renderCustomSettings() {
        // Default: do nothing - override in modules for custom ImGui content
    }

    public SettingImplementation<?> getSetting(String name) {
        return settings.get(name.toUpperCase());
    }

    public Collection<SettingImplementation<?>> getSettings() {
        return settings.values();
    }

    public void toggle() {
        BooleanSettingImplementation disableOnToggle =
                (BooleanSettingImplementation) getSetting("Disable on toggle");

        if (enabled) {
            if (disableOnToggle != null && disableOnToggle.get()) {
                enabled = false;
                onDisable();
                ModuleCacheUtils.saveModule(this);
                return;
            }
            enabled = false;
            onDisable();
            ModuleCacheUtils.saveModule(this);
            return;
        }

        enabled = true;
        onEnable();
        ModuleCacheUtils.saveModule(this);
    }

    public int getKeybindingCode() {
        return keyBindingCode;
    }

    public String getTranslatableText() {
        return ModuleAnnotationUtils.getName(this.getClass());
    }

    public boolean getEnabledStatus() {
        return enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void tick() { }

    public void postTick() { }

    public void onEnable() { }

    public void onDisable() { }

    public boolean modifyPacket(Packet<?> packet) {
        return false;
    }
}