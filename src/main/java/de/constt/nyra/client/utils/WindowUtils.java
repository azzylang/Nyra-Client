package de.constt.nyra.client.utils;

import net.minecraft.client.Minecraft;

public class WindowUtils {
    public static void unfocusMouse() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.mouseHandler == null) return;

        if (mc.mouseHandler.isMouseGrabbed()) {
            mc.mouseHandler.releaseMouse();
        }
    }
}