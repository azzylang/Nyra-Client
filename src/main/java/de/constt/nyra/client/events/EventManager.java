package de.constt.nyra.client.events;

public class EventManager {
    public static void registerEvents() {
        ClientTickEventsEvent.register();
        ClientReceiveMessageEvent.register();
        ClientSendMessageEvent.register();
    }
}
