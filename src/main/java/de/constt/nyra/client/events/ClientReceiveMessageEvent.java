package de.constt.nyra.client.events;


import de.constt.nyra.client.utils.MessageUtils;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

public class ClientReceiveMessageEvent { // Fixed typo

    private static boolean guiOpen = false;

    public static void register() {
        ClientSendMessageEvents.ALLOW_CHAT.register((chatMSG) -> {
            LocalPlayer player = Minecraft.getInstance().player;
            assert player != null;

            // Toggle ImGui GUI with command
            if (chatMSG.equalsIgnoreCase(".openGUI") || chatMSG.equalsIgnoreCase(".opengui")) {
                Minecraft.getInstance().execute(() -> {
                    Minecraft.getInstance().setScreenAndShow(new de.constt.nyra.client.screens.ExampleScreen(Component.literal("ExampleScreen")));
                });
                MessageUtils.sendCSMessageSucess("Opening GUI..."); // Fixed typo: Sucess -> Success
                return false; // Prevent the command from being sent to server
            }

            return true; // Allow normal chat messages
        });
    }
}