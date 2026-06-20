package de.constt.nyra.client;

import com.jagrosh.discordipc.IPCClient;
import de.constt.nyra.client.discordRpc.DiscordIPCCore;
import de.constt.nyra.client.managers.EventManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NyraMod implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("nyra");
    public static final String VERSION = /*$ mod_version*/ "0.0.1";
    public static final String MINECRAFT = /*$ minecraft*/ "26.1";

    @Override
    public void onInitializeClient() {
        LOGGER.info("Nyra {} for MC {} initializing...", VERSION, MINECRAFT);

        EventManager.registerEvents();

        // Check if DiscordIPC class loaded
        try {
            LOGGER.info("DiscordIPC class: {}", IPCClient.class.getName());
        } catch (Throwable t) {
            LOGGER.error("DiscordIPC not found!", t);
        }

        DiscordIPCCore.start();
    }

    /**
     * Adapts to the {@link Identifier} changes introduced in 1.21.
     */
    public static Identifier id(String namespace, String path) {
        //? if <1.21 {
        /*return new Identifier(namespace, path);
        *///?} else
        return Identifier.fromNamespaceAndPath(namespace, path);
    }
}