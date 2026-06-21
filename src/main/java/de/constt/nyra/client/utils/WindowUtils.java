package de.constt.nyra.client.utils;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;

public class WindowUtils {

    public static void setWindowTitle(String title) {
        Minecraft mc = Minecraft.getInstance();

        Window window = mc.getWindow();
        if (window != null) {
            window.setTitle(title);
        }
    }

    public static void unfocusMouse() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.mouseHandler == null) return;

        if (mc.mouseHandler.isMouseGrabbed()) {
            mc.mouseHandler.releaseMouse();
        }
    }
}