package de.constt.nyra.client.screens;

import de.constt.nyra.client.roots.implementations.CategoryImplementation;
import de.constt.nyra.client.roots.implementations.ModuleImplementation;
import de.constt.nyra.client.roots.modules.ModuleManager;
import imgui.ImGui;
import imgui.flag.ImGuiCond;

public class ModulesScreen extends BaseScreen {
    @Override
    public void render() {
        super.render();

        int offset = 0;

        for (CategoryImplementation.Categories category : CategoryImplementation.Categories.values()) {

            ImGui.setNextWindowPos(40 + offset, 40, ImGuiCond.FirstUseEver);
            ImGui.setNextWindowSize(220, 350, ImGuiCond.FirstUseEver);

            if (ImGui.begin(category.name())) {
                for (ModuleImplementation module : ModuleManager.getModules()) {
                    if(ModuleManager.getCategory(module.getClass()) == category) {
                        if(ImGui.button(module.getTranslatableText())) {
                            module.toggle();
                        }
                    }
                }
            }

            ImGui.end();

            offset += 240;
        }
    }
}