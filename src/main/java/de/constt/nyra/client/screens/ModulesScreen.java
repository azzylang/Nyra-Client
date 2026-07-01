package de.constt.nyra.client.screens;

import de.constt.nyra.client.roots.implementations.CategoryImplementation;
import de.constt.nyra.client.roots.implementations.ModuleImplementation;
import de.constt.nyra.client.roots.implementations.SettingImplementation;
import de.constt.nyra.client.roots.modules.ModuleManager;
import imgui.ImGui;
import imgui.flag.ImGuiCond;

public class ModulesScreen extends BaseScreen {

    private ModuleImplementation settingsModule;

    @Override
    public void render() {
        super.render();

        int offset = 0;

        for (CategoryImplementation.Categories category : CategoryImplementation.Categories.values()) {

            ImGui.setNextWindowPos(40 + offset, 40, ImGuiCond.FirstUseEver);
            ImGui.setNextWindowSize(220, 350, ImGuiCond.FirstUseEver);

            if (ImGui.begin(category.name())) {
                for (ModuleImplementation module : ModuleManager.getModules()) {
                    if (ModuleManager.getCategory(module.getClass()) != category) {
                        continue;
                    }

                    if (ImGui.button(module.getTranslatableText())) {
                        module.toggle();
                    }

                    if (ImGui.isItemClicked(1)) {
                        settingsModule = module;
                    }
                }
            }

            ImGui.end();

            offset += 240;
        }

        if (settingsModule != null) {
            ImGui.setNextWindowSize(300, 400, ImGuiCond.FirstUseEver);

            boolean[] open = {true};

            if (ImGui.begin(settingsModule.getTranslatableText() + " Settings")) {

                renderSettings(settingsModule);

            }

            ImGui.end();

            if (!open[0]) {
                settingsModule = null;
            }
        }
    }

    private void renderSettings(ModuleImplementation module) {
        ImGui.beginChild("Settings");

        for (SettingImplementation<?> setting : module.getSettings()) {
            setting.renderImGui();
        }

        ImGui.separator();

        module.renderCustomSettings();

        ImGui.endChild();
    }
}