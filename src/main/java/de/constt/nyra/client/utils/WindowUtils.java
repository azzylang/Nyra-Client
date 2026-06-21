package de.constt.nyra.client.utils;

import net.minecraft.client.Minecraft;

public class WindowUtils {
    public static void setWindowTitle(Minecraft client, String title) {
        client.getWindow().setTitle(title);
    }

    public static void unfocusMouse() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.mouseHandler.isMouseGrabbed()) {
            mc.mouseHandler.releaseMouse();
        }
    }
}
