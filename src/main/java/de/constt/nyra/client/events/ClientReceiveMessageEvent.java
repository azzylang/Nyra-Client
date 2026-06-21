package de.constt.nyra.client.events;


import de.constt.nyra.client.utils.MessageUtils;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class ClientReceiveMessageEvent {

    public static void register() {
        ClientReceiveMessageEvents.ALLOW_CHAT.register((chatMSG, playerChatMessage, gameProfile, bound, instant) -> {
            LocalPlayer player = Minecraft.getInstance().player;
            assert player != null;

            MessageUtils.sendCSMessageSucess("Detected sent message!");

            return true;
        });
    }
}