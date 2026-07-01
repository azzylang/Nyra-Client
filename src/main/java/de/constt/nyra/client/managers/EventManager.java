package de.constt.nyra.client.managers;

import de.constt.nyra.client.events.ClientSendMessageEvent;

public class EventManager {
    public static void registerEvents() {
        ClientSendMessageEvent.register();
    }
}
