package de.constt.nyra.client.managers;

import de.constt.nyra.client.events.MessageListener;

public class EventManager {
    public static void registerEvents() {
        MessageListener.register();
    }
}
