package de.constt.nyra.client.utils;


import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;

public class TitleUtils {

    private static void playSound(SoundEvent sound, float pitch) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                mc.player.playSound(sound, 1.0f, pitch);
            }

    }

    private static void sendTitle(Component title, Component subtitle) {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;
            if (player == null) return;

            int fadeIn = 10;
            int stay = 70;
            int fadeOut = 20;

            mc.gui.hud.setOverlayMessage(title, false); // shows it as overlay in client
            mc.gui.hud.setOverlayMessage(subtitle, false); // subtitle
    }

    public static void sendCSTitleNeutral(String msg) {
        sendTitle(Component.literal(msg).withStyle(ChatFormatting.GRAY), Component.literal(""));
    }

    public static void sendCSTitleWarning(String msg) {
        sendTitle(Component.literal(msg).withStyle(ChatFormatting.YELLOW), Component.literal(""));
    }

    public static void sendCSTitleError(String msg) {
        sendTitle(Component.literal(msg).withStyle(ChatFormatting.RED), Component.literal(""));
    }
}
