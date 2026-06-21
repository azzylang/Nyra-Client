package de.constt.nyra.client.roots.implementations.settings;

import de.constt.nyra.client.roots.implementations.SettingImplementation;
import imgui.ImGui;
import imgui.type.ImInt;

public final class NumberSettingImplementation extends SettingImplementation<Integer> {

    private final int min;
    private final int max;
    private final ImInt imValue;

    public NumberSettingImplementation(String name, int defaultValue, int min, int max) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
        this.imValue = new ImInt(defaultValue);
    }

    @Override
    public void renderImGui() {
        imValue.set(value);
        if (ImGui.sliderInt(getName(), imValue.getData(), min, max)) {
            value = imValue.get();
        }
    }
}
