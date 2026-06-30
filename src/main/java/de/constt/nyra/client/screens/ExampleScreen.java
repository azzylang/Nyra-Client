package de.constt.nyra.client.screens;

import foundry.imgui.api.ImGuiMC;
import imgui.ImGui;
import imgui.type.ImBoolean;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ExampleScreen extends Screen {
    private static final ImBoolean showDemoWindow = new ImBoolean(false);

    public ExampleScreen() {
        super(Component.literal("Example Screen"));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        try(ImGuiMC.ActiveContext ctx = ImGuiMC.withImGui()) {
            if (ctx != null) {
                if (ImGui.begin("Hello, World!")) {
                    ImGui.setWindowSize(800, 600);
                    ImGui.checkbox("Show Demo Window", showDemoWindow);
                    ImGui.end();
                }

                ImGui.showDemoWindow(showDemoWindow);
            }
        }
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }
}