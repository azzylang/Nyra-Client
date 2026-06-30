package de.constt.nyra.client;

import com.jagrosh.discordipc.IPCClient;
import de.constt.nyra.client.clientcommands.CCommandManager;
import de.constt.nyra.client.discordRpc.DiscordIPCCore;
import de.constt.nyra.client.managers.EventManager;
import de.constt.nyra.client.roots.modules.ModuleManager;
import de.constt.nyra.client.utils.InstanceUtils;
import de.constt.nyra.client.utils.MessageUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NyraMod implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("nyra");
    public static final String VERSION = /*$ mod_version*/ "0.0.1";
    public static final String MINECRAFT = /*$ minecraft*/ "26.2";

    @Override
    public void onInitializeClient() {
        LOGGER.info("Nyra {} for MC {} initializing...", VERSION, MINECRAFT);

        // -- Variable Setting --
        MessageUtils.setPrefix("Nyra");

        // -- Events --
        EventManager.registerEvents();

        // -- Modules --
        ModuleManager.init();

        // -- Commands --
        CCommandManager.init();

        // Check if DiscordIPC class loaded
        try {
            LOGGER.info("DiscordIPC class: {}", IPCClient.class.getName());
        } catch (Throwable t) {
            LOGGER.error("DiscordIPC not found!", t);
        }

        DiscordIPCCore.start();

        String title = "Nyra | CL V. " + VERSION + " | MC V. " + MINECRAFT + " | #" + InstanceUtils.getInstanceId();

        ClientTickEvents.END_CLIENT_TICK.register(client -> client.getWindow().setTitle(title));

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