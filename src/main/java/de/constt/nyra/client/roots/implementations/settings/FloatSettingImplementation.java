package de.constt.nyra.client.roots.implementations.settings;

import de.constt.nyra.client.roots.implementations.SettingImplementation;
import imgui.ImGui;
import imgui.type.ImFloat;

public final class FloatSettingImplementation extends SettingImplementation<Float> {

    private final float min;
    private final float max;
    private final String format;
    private final ImFloat imValue;

    public FloatSettingImplementation(String name, float defaultValue, float min, float max) {
        this(name, defaultValue, min, max, "%.3f");
    }

    public FloatSettingImplementation(String name, float defaultValue, float min, float max, String format) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
        this.format = format;
        this.imValue = new ImFloat(defaultValue);
    }

    @Override
    public void renderImGui() {
        imValue.set(value);
        if (ImGui.sliderFloat(getName(), imValue.getData(), min, max, format)) {
            value = imValue.get();
        }
    }
}