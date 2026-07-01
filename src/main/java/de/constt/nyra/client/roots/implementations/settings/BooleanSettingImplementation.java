package de.constt.nyra.client.roots.implementations.settings;

import de.constt.nyra.client.roots.implementations.SettingImplementation;
import imgui.ImGui;
import imgui.type.ImBoolean;

public final class BooleanSettingImplementation extends SettingImplementation<Boolean> {

    private final ImBoolean imValue;

    public BooleanSettingImplementation(String name, boolean defaultValue) {
        super(name, defaultValue);
        this.imValue = new ImBoolean(defaultValue);
    }

    @Override
    public void renderImGui() {
        if (ImGui.checkbox(getName(), imValue)) {
            value = imValue.get();
        }
    }
}
