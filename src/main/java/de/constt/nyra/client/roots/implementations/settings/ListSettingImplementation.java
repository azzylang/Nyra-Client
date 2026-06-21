package de.constt.nyra.client.roots.implementations.settings;

import de.constt.nyra.client.roots.implementations.SettingImplementation;
import imgui.ImGui;
import imgui.type.ImInt;

public final class ListSettingImplementation extends SettingImplementation<String> {

    private final String[] options;
    private final ImInt imIndex;

    public ListSettingImplementation(String name, String[] options, String defaultValue) {
        super(name, defaultValue);
        this.options = options;
        this.imIndex = new ImInt(indexOf(defaultValue));
    }

    @Override
    public void renderImGui() {
        imIndex.set(indexOf(value));

        if (ImGui.combo(getName(), imIndex, options)) {
            value = options[imIndex.get()];
        }
    }

    /** Returns the selected option index, or 0 if not found. */
    private int indexOf(String val) {
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(val)) return i;
        }
        return 0;
    }
}