package de.constt.nyra.client.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class MessageUtils {
    public static Component prefix;

    public static Component getPrefix(boolean isClean) {

        if (prefix == null) return Component.empty();
        MutableComponent result = Component.literal("§e[§6Nyra§e]§r ");

        if (isClean) {
            String replaceable = result.getString();
            replaceable = replaceable.replaceAll("§.", "");
            replaceable = replaceable.replaceAll(" \\| $", "");
            return Component.literal(replaceable);
        }
        return result;
    }

    public static void setPrefix(Component newPrefix) {
        prefix = newPrefix;
    }

    public static void sendCSMessageNeutral(String msg) {
        Minecraft mc = Minecraft.getInstance();

        //~ if < 26.2 'mc.gui.hud.getChat()' -> 'mc.gui.getChat()'
        //~ if <= 1.21.11 'addClientSystemMessage' -> 'addMessage'

        MutableComponent component = getPrefix(false).copy();

        mc.gui.hud.getChat().addClientSystemMessage(
                component.append(Component.literal(msg).withStyle(ChatFormatting.GRAY)));
    }

    public static void sendCSMessageSucess(String msg) {
        Minecraft mc = Minecraft.getInstance();

        //~ if < 26.2 'mc.gui.hud.getChat()' -> 'mc.gui.getChat()'
        //~ if <= 1.21.11 'addClientSystemMessage' -> 'addMessage'

        MutableComponent component = getPrefix(false).copy();

        mc.gui.hud.getChat().addClientSystemMessage(
                component.append(Component.literal(msg).withStyle(ChatFormatting.GREEN))
        );
    }

    public static void sendCSMessageWarning(String msg) {
        Minecraft mc = Minecraft.getInstance();

        //~ if < 26.2 'mc.gui.hud.getChat()' -> 'mc.gui.getChat()'
        //~ if <= 1.21.11 'addClientSystemMessage' -> 'addMessage'

        MutableComponent component = getPrefix(false).copy();

        mc.gui.hud.getChat().addClientSystemMessage(
                component.append(Component.literal(msg).withStyle(ChatFormatting.YELLOW)));
    }

    public static void sendCSMessageError(String msg) {
        Minecraft mc = Minecraft.getInstance();

        //~ if < 26.2 'mc.gui.hud.getChat()' -> 'mc.gui.getChat()'
        //~ if <= 1.21.11 'addClientSystemMessage' -> 'addMessage'

        MutableComponent component = getPrefix(false).copy();

        mc.gui.hud.getChat().addClientSystemMessage(
                component.append(Component.literal(msg).withStyle(ChatFormatting.RED)));
    }
}