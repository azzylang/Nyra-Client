package de.constt.nyra.client.events;

import de.constt.nyra.client.NyraMod;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class MessageListener {
    public static void register() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            NyraMod.LOGGER.info("MESSAGE EVENT");

            String content = message.getString();
            NyraMod.LOGGER.info("Received chat: {}", content);

            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player != null) {
                minecraft.player.sendSystemMessage(Component.literal("Recived Message"));
            }
        });
    }
}