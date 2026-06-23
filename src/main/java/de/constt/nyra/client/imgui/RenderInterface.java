package de.constt.nyra.client.imgui;

import imgui.ImGuiIO;

@FunctionalInterface
public interface RenderInterface {

    void render(final ImGuiIO io);

}