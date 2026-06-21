package de.constt.nyra.client.discordRpc;

import com.google.gson.JsonObject;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.ActivityType;
import com.jagrosh.discordipc.entities.Packet;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.User;
import de.constt.nyra.client.NyraMod;
import net.minecraft.SharedConstants;

import java.time.OffsetDateTime;

public final class DiscordIPCCore {

    private static IPCClient client;
    private static boolean active;
    private static final long CLIENT_ID = 1517957156710908025L;

    public static void start() {
        if (active) {
            NyraMod.LOGGER.debug("Discord IPC already active, skipping");
            return;
        }

        client = new IPCClient(CLIENT_ID);
        NyraMod.LOGGER.info("Discord IPC client created with ID {}", CLIENT_ID);

        client.setListener(new IPCListener() {
            @Override
            public void onReady(IPCClient c) {
                NyraMod.LOGGER.info("Discord IPC ready!");
                try {
                    RichPresence.Builder builder = new RichPresence.Builder()
                            .setActivityType(ActivityType.Playing)
                            .setState("Being Open Source")
                            //~ if <1.21.6 'name()' -> 'getName()'
                            .setDetails(SharedConstants.getCurrentVersion().name())
                            .setLargeImage("logo-1600")
                            .setStartTimestamp(OffsetDateTime.now().toEpochSecond());

                    c.sendRichPresence(builder.build());
                    NyraMod.LOGGER.info("RichPresence sent successfully");
                } catch (Throwable t) {
                    NyraMod.LOGGER.error("Failed to send RichPresence", t);
                }
            }

            @Override
            public void onPacketSent(IPCClient c, Packet p) {
                NyraMod.LOGGER.debug("Packet sent: {}", p.getJson());
            }

            @Override
            public void onPacketReceived(IPCClient c, Packet p) {
                NyraMod.LOGGER.debug("Packet received: {}", p.getJson());
            }

            @Override
            public void onActivityJoin(IPCClient c, String s) {
                NyraMod.LOGGER.debug("Activity join: {}", s);
            }

            @Override
            public void onActivitySpectate(IPCClient c, String s) {
                NyraMod.LOGGER.debug("Activity spectate: {}", s);
            }

            @Override
            public void onActivityJoinRequest(IPCClient c, String s, User u) {
                NyraMod.LOGGER.debug("Join request from {}: {}", u.getName(), s);
            }

            @Override
            public void onClose(IPCClient c, JsonObject j) {
                NyraMod.LOGGER.warn("Discord IPC closed: {}", j);
                active = false;
            }

            @Override
            public void onDisconnect(IPCClient c, Throwable t) {
                NyraMod.LOGGER.error("Discord IPC disconnected", t);
                active = false;
            }
        });

        try {
            client.connect();
            active = true;
            NyraMod.LOGGER.info("Discord IPC connected successfully");
        } catch (Throwable t) {
            NyraMod.LOGGER.error("Discord IPC connect() failed", t);
            shutdown();
        }
    }

    public static void shutdown() {
        if (!active && client == null) {
            return;
        }

        NyraMod.LOGGER.info("Shutting down Discord IPC...");
        active = false;

        try {
            if (client != null) {
                client.close();
                NyraMod.LOGGER.debug("Discord IPC client closed");
            }
        } catch (Throwable t) {
            NyraMod.LOGGER.error("Error closing Discord IPC client", t);
        } finally {
            client = null;
        }
    }

    public static boolean isActive() {
        return active;
    }
}