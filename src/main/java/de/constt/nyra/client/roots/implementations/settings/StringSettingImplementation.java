package de.constt.nyra.client.roots.implementations.settings;

import de.constt.nyra.client.roots.implementations.SettingImplementation;
import imgui.ImGui;
import imgui.type.ImString;

public final class StringSettingImplementation extends SettingImplementation<String> {

    private final ImString imValue;

    public StringSettingImplementation(String name, String defaultValue) {
        super(name, defaultValue);
        this.imValue = new ImString(defaultValue, 256);
    }

    @Override
    public void renderImGui() {
        imValue.set(value);
        if (ImGui.inputText(getName(), imValue)) {
            value = imValue.get();
        }
    }
}
