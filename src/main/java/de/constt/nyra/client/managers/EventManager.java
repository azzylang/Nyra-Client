package de.constt.nyra.client.managers;

import de.constt.nyra.client.events.ClientReceiveMessageEvent;
import de.constt.nyra.client.events.ClientSendMessageEvent;

public class EventManager {
    public static void registerEvents() {
        ClientReceiveMessageEvent.register();
        ClientSendMessageEvent.register();
    }
}
