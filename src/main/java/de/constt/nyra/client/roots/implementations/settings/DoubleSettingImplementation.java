package de.constt.nyra.client.roots.implementations.settings;

import de.constt.nyra.client.roots.implementations.SettingImplementation;
import imgui.ImGui;
import imgui.type.ImDouble;

public final class DoubleSettingImplementation extends SettingImplementation<Double> {

    private final double min;
    private final double max;
    private final String format;
    private final ImDouble imValue;

    public DoubleSettingImplementation(String name, double defaultValue, double min, double max) {
        this(name, defaultValue, min, max, "%.6f");
    }

    public DoubleSettingImplementation(String name, double defaultValue, double min, double max, String format) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
        this.format = format;
        this.imValue = new ImDouble(defaultValue);
    }

    @Override
    public void renderImGui() {
        imValue.set(value);
        if (ImGui.sliderScalar(getName(), imValue.getData(), min, max, format)) {
            value = imValue.get();
        }
    }
}