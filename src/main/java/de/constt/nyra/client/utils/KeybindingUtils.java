package de.constt.nyra.client.utils;

import org.lwjgl.glfw.GLFW;

public class KeybindingUtils {
    public static int getKeyCode(String keybindArg) {
        int keyCode = 0;

        if (keybindArg.length() == 1) {
            char c = keybindArg.charAt(0);
            if (c >= 'A' && c <= 'Z') keyCode = GLFW.GLFW_KEY_A + (c - 'A');
            else if (c >= '0' && c <= '9') keyCode = GLFW.GLFW_KEY_0 + (c - '0');
        } else if (keybindArg.equals("SPACE")) keyCode = GLFW.GLFW_KEY_SPACE;

        else if (keybindArg.equals("SHIFT")) keyCode = GLFW.GLFW_KEY_LEFT_SHIFT;
        return keyCode;
    }
}
