package de.constt.nyra.client.events;

import de.constt.nyra.client.clientcommands.CCommandManager;
import de.constt.nyra.client.utils.CommandAnnotationUtils;
import de.constt.nyra.client.utils.MessageUtils;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.util.Arrays;

public class ClientReceiveMessageEvent {

    public static void register() {
        ClientReceiveMessageEvents.ALLOW_CHAT.register((chatMSG, playerChatMessage, gameProfile, bound, instant) -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return true;

            String msg = chatMSG.getString();

            if (!msg.startsWith(CCommandManager.cmdPrefix)) return true;

            String raw = msg.substring(CCommandManager.cmdPrefix.length()).trim();
            String[] split = raw.split(" ");

            String cmdName = split[0];
            String[] args = split.length > 1
                    ? Arrays.copyOfRange(split, 1, split.length)
                    : new String[0];

            for (var commandClass : CCommandManager.getCommands()) {
                String name = CommandAnnotationUtils.getCommand(commandClass.getClass());

                if (name.equalsIgnoreCase(cmdName)) {
                    commandClass.executeCommand(args);
                    break;
                }
            }

            return true;
        });
    }
}