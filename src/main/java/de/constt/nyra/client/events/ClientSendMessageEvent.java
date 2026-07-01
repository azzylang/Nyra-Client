package de.constt.nyra.client.events;

import de.constt.nyra.client.clientcommands.CCommandManager;
import de.constt.nyra.client.utils.CommandAnnotationUtils;
import de.constt.nyra.client.utils.MessageUtils;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.util.Arrays;

public class ClientSendMessageEvent {

    public static void register() {

        ClientSendMessageEvents.ALLOW_CHAT.register((message) -> {

            if (message.charAt(0) == '$') {
                LocalPlayer player = Minecraft.getInstance().player;
                System.out.println("a");
                if (player == null) return false;
                System.out.println("b");

                if (!message.startsWith(CCommandManager.cmdPrefix)) return true;

                String raw = message.substring(CCommandManager.cmdPrefix.length()).trim();
                String[] split = raw.split(" ");

                String cmdName = split[0];
                String[] args = split.length > 1
                        ? Arrays.copyOfRange(split, 1, split.length)
                        : new String[0];

                for (var commandClass : CCommandManager.getCommands()) {
                    String name = CommandAnnotationUtils.getCommand(commandClass.getClass());

                    System.out.println("name: " + name + " | cmdName: " + cmdName + " | args: " + Arrays.toString(args));

                    if (name.equalsIgnoreCase(cmdName)) {
                        commandClass.executeCommand(args);
                        break;
                    }
                }

                return false;
            }

            if (message.charAt(0) == '#') {

                String msg;

                if (message.charAt(1) == ' ') { // avoid space at the start of message
                    msg = message.substring(2);
                } else {
                    msg = message.substring(1);
                }

                MessageUtils.sendCSMessageNeutral("Message started with #," +
                        " sending this message to IPC: " + msg);

                // finish code when server is obtained

                return false;

            }

            return true;

        });
    }

}
