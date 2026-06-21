package de.constt.nyra.client.impl;

import com.mojang.blaze3d.platform.Window;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.gl3.ImGuiImplGl3;
import net.minecraft.client.Minecraft;

public class ImGuiImpl {

    private static boolean initialized = false;
    private static ImGuiImplGl3 renderer;

    /** Called once at game startup with the GLFW window handle */
    public static void create(long windowHandle) {
        if (initialized) return;

        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);

        // Set initial display size (will be updated each frame)
        Window window = Minecraft.getInstance().getWindow();
        io.setDisplaySize(window.getWidth(), window.getHeight());
        io.setDisplayFramebufferScale((float) window.getGuiScale(), (float) window.getGuiScale());

        io.getFonts().build();

        renderer = new ImGuiImplGl3();
        renderer.init("#version 150");

        initialized = true;
    }

    public static void dispose() {
        if (!initialized) return;

        renderer.dispose();
        ImGui.destroyContext();
        initialized = false;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void beginImGuiRendering() {
        if (!initialized) {
            throw new IllegalStateException("ImGui not initialized!");
        }

        // Get the Minecraft window
        Window window = Minecraft.getInstance().getWindow();
        ImGuiIO io = ImGui.getIO();

        // MUST set display size before newFrame()
        io.setDisplaySize(window.getWidth(), window.getHeight());
        io.setDisplayFramebufferScale((float) window.getGuiScale(), (float) window.getGuiScale());

        // Optional: update mouse position if you want ImGui to receive mouse input
        // io.setMousePos(mouseX, mouseY);

        ImGui.newFrame();
    }

    public static void endImGuiRendering() {
        ImGui.render();

        var drawData = ImGui.getDrawData();
        if (drawData != null && drawData.getCmdListsCount() > 0) {
            renderer.renderDrawData(drawData);
        }
    }
}