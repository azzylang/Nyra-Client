package de.constt.nyra.client.impl;

import imgui.ImGuiIO;

@FunctionalInterface
public interface RenderInterface {

    void render(final ImGuiIO io);

}