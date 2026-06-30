package de.constt.nyra.client.events;

import de.constt.nyra.client.utils.MessageUtils;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;

public class ClientSendMessageEvent {

    public static void register() {

        ClientSendMessageEvents.ALLOW_CHAT.register((message) -> {
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

            }

            return false;

        });
    }

}
