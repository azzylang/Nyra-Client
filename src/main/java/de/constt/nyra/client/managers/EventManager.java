package de.constt.nyra.client.managers;

import de.constt.nyra.client.events.ClientReceiveMessageEvent;

public class EventManager {
    public static void registerEvents() {
        ClientReceiveMessageEvent.register();
    }
}
