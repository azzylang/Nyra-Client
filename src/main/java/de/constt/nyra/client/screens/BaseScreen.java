package de.constt.nyra.client.screens;

import foundry.imgui.api.ImGuiMC;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class BaseScreen extends Screen {

    public BaseScreen() {
        super(Component.literal("Base Screen"));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        try(ImGuiMC.ActiveContext ctx = ImGuiMC.withImGui()) {
            if (ctx != null) {
                render();
            }
        }
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    public void render() {}
}
