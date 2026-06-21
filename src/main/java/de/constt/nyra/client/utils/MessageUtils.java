package de.constt.nyra.client.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class MessageUtils {
    public static String prefix;

    public static String getPrefix(boolean isClean) {
        if (prefix == null) return "";
        String result = prefix + "§r | ";
        if (isClean) {
            result = result.replaceAll("§.", "");
            result = result.replaceAll(" \\| $", "");
        }
        return result;
    }

    public static void setPrefix(String newPrefix) {
        prefix = newPrefix;
    }

    public static void sendCSMessageNeutral(String msg) {
        Minecraft mc = Minecraft.getInstance();

        //~ if < 26.2 'mc.gui.hud.getChat()' -> 'mc.gui.getChat()'
        //~ if <= 1.21.11 'addClientSystemMessage' -> 'addMessage'
        mc.gui.hud.getChat().addClientSystemMessage(
                Component.literal(getPrefix(false))
                        .append(Component.literal(msg).withStyle(ChatFormatting.GRAY)));
    }

    public static void sendCSMessageSucess(String msg) {
        Minecraft mc = Minecraft.getInstance();

        //~ if < 26.2 'mc.gui.hud.getChat()' -> 'mc.gui.getChat()'
        //~ if <= 1.21.11 'addClientSystemMessage' -> 'addMessage'
        mc.gui.hud.getChat().addClientSystemMessage(
                Component.literal(getPrefix(false))
                        .append(Component.literal(msg).withStyle(ChatFormatting.GREEN))
        );
    }

    public static void sendCSMessageWarning(String msg) {
        Minecraft mc = Minecraft.getInstance();

        //~ if < 26.2 'mc.gui.hud.getChat()' -> 'mc.gui.getChat()'
        //~ if <= 1.21.11 'addClientSystemMessage' -> 'addMessage'
        mc.gui.hud.getChat().addClientSystemMessage(
        Component.literal(getPrefix(false))
                .append(Component.literal(msg).withStyle(ChatFormatting.YELLOW)));
    }

    public static void sendCSMessageError(String msg) {
        Minecraft mc = Minecraft.getInstance();

        //~ if < 26.2 'mc.gui.hud.getChat()' -> 'mc.gui.getChat()'
        //~ if <= 1.21.11 'addClientSystemMessage' -> 'addMessage'
        mc.gui.hud.getChat().addClientSystemMessage(
                Component.literal(getPrefix(false))
                        .append(Component.literal(msg).withStyle(ChatFormatting.RED)));
    }
}