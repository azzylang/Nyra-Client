package de.constt.nyra.client.utils;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import de.constt.nyra.client.utils.PlayerUtils;

public class FriendUtils {

    private static final Path FILE =
            FabricLoader.getInstance().getConfigDir()
                    .resolve("scalare")
                    .resolve("friends.cfg");

    /** How long to wait for Mojang UUID / name lookups before giving up. */
    private static final long LOOKUP_TIMEOUT_SECONDS = 5;

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /** Runs a MessageUtils call safely on the main game thread from any thread. */
    private static void sendMessage(Runnable messageCall) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.isSameThread()) {
            messageCall.run();
        } else {
            mc.execute(messageCall);
        }
    }

    // -----------------------------------------------------------------------
    // UUID / name resolution
    // -----------------------------------------------------------------------

    /**
     * Blocks on {@link PlayerUtils#getUUIDAsync(String)} and returns the
     * resolved {@link UUID}, or {@code null} on failure or timeout.
     */
    private static UUID resolveUUID(String username) {
        try {
            CompletableFuture<UUID> future = PlayerUtils.getUUIDAsync(username);
            return future.get(LOOKUP_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException | TimeoutException e) {
            return null;
        }
    }

    /**
     * Blocks on {@link PlayerUtils#getUsernameAsync(UUID)} and returns the
     * resolved username, or {@code null} on failure or timeout.
     */
    private static String resolveUsername(UUID uuid) {
        try {
            CompletableFuture<String> future = PlayerUtils.getUsernameAsync(uuid);
            return future.get(LOOKUP_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException | TimeoutException e) {
            return null;
        }
    }

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Adds {@code username} as a friend.
     * Runs on a background thread — resolves the UUID, validates, then
     * appends the UUID as a new line via {@link FileUtils#appendLine}.
     */
    public static void addFriend(String username) {
        new Thread(() -> {
            UUID uuid = resolveUUID(username);

            if (uuid == null) {
                sendMessage(() -> MessageUtils.sendCSMessageError(
                        "Couldn't add §e" + username + "§c as a friend – are they a real player, or are Mojang servers down?"));
                return;
            }

            if (isFriendByUUID(uuid)) {
                sendMessage(() -> MessageUtils.sendCSMessageError(
                        "§e" + username + "§c is already on your friends list!"));
                return;
            }

            try {
                FileUtils.appendLine(FILE, uuid.toString());
                sendMessage(() -> MessageUtils.sendCSMessageNeutral(
                        "You added §e" + username + "§a as a friend!"));
            } catch (IOException e) {
                System.err.println("[FriendUtils] Failed to add friend " + username + ": " + e.getMessage());
                sendMessage(() -> MessageUtils.sendCSMessageError(
                        "Failed to save §e" + username + "§c to the friends file."));
            }
        }, "FriendUtils-Add").start();
    }

    /**
     * Removes {@code username} from the friends list.
     * Delegates the line removal and file rewrite to {@link FileUtils#removeLine}.
     */
    public static void removeFriend(String username) {
        new Thread(() -> {
            UUID uuid = resolveUUID(username);

            if (uuid == null) {
                sendMessage(() -> MessageUtils.sendCSMessageError(
                        "Couldn't resolve §e" + username + "§c – are they a real player, or are Mojang servers down?"));
                return;
            }

            if (!isFriendByUUID(uuid)) {
                sendMessage(() -> MessageUtils.sendCSMessageError(
                        "§e" + username + "§c is not on your friends list."));
                return;
            }

            try {
                FileUtils.removeLine(FILE, uuid.toString());
                sendMessage(() -> MessageUtils.sendCSMessageNeutral(
                        "You removed §e" + username + "§c from your friends list."));
            } catch (IOException e) {
                System.err.println("[FriendUtils] Failed to remove friend " + username + ": " + e.getMessage());
                sendMessage(() -> MessageUtils.sendCSMessageError(
                        "Failed to remove §e" + username + "§c from the friends file."));
            }
        }, "FriendUtils-Remove").start();
    }

    /**
     * Returns {@code true} if {@code username} is in the friends list.
     * Performs a blocking Mojang UUID lookup — call only from a background thread.
     * Prefer {@link #isFriendByUUID(UUID)} when you already have the UUID.
     */
    public static boolean isFriend(String username) {
        UUID uuid = resolveUUID(username);
        if (uuid == null) return false;
        return isFriendByUUID(uuid);
    }

    /**
     * Returns {@code true} if the given {@link UUID} is saved in the friends file.
     * No network call is made — safe to call from any thread.
     */
    public static boolean isFriendByUUID(UUID uuid) {
        return FileUtils.fileContainsText(FILE, uuid.toString());
    }

    /**
     * Returns an unmodifiable list of every UUID currently saved as a friend.
     * Never returns {@code null}; an empty list is returned on any error.
     */
    public static List<UUID> getFriendUUIDs() {
        try {
            List<String> lines = FileUtils.readLines(FILE);
            List<UUID>   uuids = new ArrayList<>(lines.size());
            for (String line : lines) {
                try {
                    uuids.add(UUID.fromString(line));
                } catch (IllegalArgumentException ignored) {
                    System.err.println("[FriendUtils] Skipping malformed UUID line: \"" + line + "\"");
                }
            }
            return Collections.unmodifiableList(uuids);
        } catch (IOException e) {
            System.err.println("[FriendUtils] Failed to read friends file: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Sends the full friends list to chat, resolving each UUID back to a username.
     * Runs on a background thread to avoid blocking the game loop during lookups.
     */
    public static void listFriends() {
        new Thread(() -> {
            List<UUID> uuids = getFriendUUIDs();

            if (uuids.isEmpty()) {
                sendMessage(() -> MessageUtils.sendCSMessageNeutral("Your friends list is empty."));
                return;
            }

            StringBuilder sb = new StringBuilder("§7Friends (§e" + uuids.size() + "§7): ");
            for (int i = 0; i < uuids.size(); i++) {
                String name = resolveUsername(uuids.get(i));
                sb.append(name != null
                        ? "§a" + name
                        : "§8" + uuids.get(i).toString().substring(0, 8) + "…");
                if (i < uuids.size() - 1) sb.append("§7, ");
            }

            String message = sb.toString();
            sendMessage(() -> MessageUtils.sendCSMessageNeutral(message));
        }, "FriendUtils-List").start();
    }

    /**
     * Clears every entry from the friends list by overwriting the file with
     * an empty line set via {@link FileUtils#writeLines}.
     */
    public static void clearFriends() {
        new Thread(() -> {
            try {
                FileUtils.writeLines(FILE, Collections.emptyList());
                sendMessage(() -> MessageUtils.sendCSMessageNeutral("Your friends list has been cleared."));
            } catch (IOException e) {
                System.err.println("[FriendUtils] Failed to clear friends file: " + e.getMessage());
                sendMessage(() -> MessageUtils.sendCSMessageError("Failed to clear the friends file."));
            }
        }, "FriendUtils-Clear").start();
    }
}