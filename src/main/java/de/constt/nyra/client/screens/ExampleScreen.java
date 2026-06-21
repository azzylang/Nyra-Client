package de.constt.nyra.client.screens;

import de.constt.nyra.client.impl.RenderInterface;
import imgui.ImDrawData;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.type.ImBoolean;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ExampleScreen extends Screen implements RenderInterface {

    private final ImBoolean showDemo = new ImBoolean(false);

    public ExampleScreen(Component title) {
        super(title);
    }

    @Override
    public void render(ImGuiIO io) {
        // Just draw ImGui widgets. NOTHING else.
        ImGui.begin("Example Screen");
        ImGui.text("Hello from Nyra!");
        ImGui.checkbox("Show Demo Window", showDemo);

        if (showDemo.get()) {
            ImGui.showDemoWindow(showDemo);
        }

        ImGui.end();
    }
}