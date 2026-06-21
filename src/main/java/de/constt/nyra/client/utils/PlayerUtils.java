package de.constt.nyra.client.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.regex.Pattern;

public class PlayerUtils {

    // ── Cache ──────────────────────────────────────────────────────────────
    /** Three-state result: TRUE = real player, FALSE = bot, null = unknown / pending */
    private static final Map<UUID, Boolean> mojangCache = new ConcurrentHashMap<>();
    private static final ExecutorService POOL = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "AntiBotModule-Worker");
        t.setDaemon(true);
        return t;
    });

    // ── Patterns ───────────────────────────────────────────────────────────
    /** Matches typical bot name generators: purely random lowercase + digits, 16+ chars */
    private static final Pattern BOT_NAME_PATTERN = Pattern.compile(
            "^[a-z0-9]{16,}$|^[A-Za-z0-9]{1,2}$|.*[\\u0000-\\u001F].*"
    );
    /** Valid Minecraft usernames: 3–16 alphanumeric/_ */
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile(
            "^[A-Za-z0-9_]{3,16}$"
    );

    // ──────────────────────────────────────────────────────────────────────
    //  Bot detection
    // ──────────────────────────────────────────────────────────────────────

    /**
     * Returns whether the given player is likely a bot.
     * Fast heuristics run synchronously; Mojang validation is async and
     * populates the cache for subsequent calls.
     */
    public static boolean isBot(Player player) {
        Minecraft mc = Minecraft.getInstance();

        // ── Null / self guards ─────────────────────────────────────────────
        if (player == null) return true;
        if (player == mc.player) return false;

        // ── Tab-list presence ──────────────────────────────────────────────
        if (mc.getConnection() == null) return true;
        if (mc.getConnection().getPlayerInfo(player.getUUID()) == null) return true;

        // ── Game-profile completeness ──────────────────────────────────────
        var profile = player.getGameProfile();
        //~if < 1.21.9 'name()' -> 'getName()'
        String name = profile.name();
        //~if < 1.21.9 'id()' -> 'getId()'
        UUID   uuid = profile.id();

        if (name == null || name.isEmpty()) return true;
        if (uuid == null) return true;

        // ── Name validity ──────────────────────────────────────────────────
        if (!VALID_NAME_PATTERN.matcher(name).matches()) return true;
        if (BOT_NAME_PATTERN.matcher(name).matches())    return true;

        // ── UUID version check (online-mode players always get v4 UUIDs) ───
        if (uuid.version() != 4) return true;

        // ── Skin properties (online players always have a textures property)
        //~if < 1.21.9 'properties()' -> 'getProperties()'
        if (profile.properties().isEmpty()) return true;

        // ── Entity-state heuristics ────────────────────────────────────────
        if (player.tickCount < 5) return true;                           // just spawned
        if (player.isInvisible() && !player.isSpectator()) return true;  // invisible non-spec

        // Completely frozen for more than 20 ticks and not in a vehicle
        boolean frozen = player.getDeltaMovement().lengthSqr() == 0.0
                && player.walkAnimation.speed() == 0.0f
                && player.tickCount > 20
                && !player.isPassenger();
        if (frozen) return true;

        // ── Ping check (0 ms ping is never seen on real connections) ───────
        var info = mc.getConnection().getPlayerInfo(uuid);
        if (info != null && info.getLatency() <= 0) return true;

        // ── Async Mojang verification ──────────────────────────────────────
        Boolean cached = mojangCache.get(uuid);
        if (cached != null) return !cached; // cached result available

        // Fire async lookup (result used on next call)
        scheduleMojangCheck(name, uuid);

        // Not proven to be a bot yet → benefit of the doubt
        return false;
    }

    /** Clears the Mojang cache (call on world/server disconnect). */
    public static void clearCache() {
        mojangCache.clear();
    }

    // ──────────────────────────────────────────────────────────────────────
    //  UUID lookup
    // ──────────────────────────────────────────────────────────────────────

    /**
     * Fetches a player's UUID from the Mojang API asynchronously.
     * Returns a {@link CompletableFuture} – never blocks the calling thread.
     */
    public static CompletableFuture<UUID> getUUIDAsync(String username) {
        return CompletableFuture.supplyAsync(() -> fetchUUID(username), POOL);
    }

    // ──────────────────────────────────────────────────────────────────────
    //  Username lookup
    // ──────────────────────────────────────────────────────────────────────

    /**
     * Resolves a Mojang {@link UUID} back to the player's current username
     * asynchronously.
     *
     * <p>Uses the {@code sessionserver.mojang.com} profile endpoint, which is
     * the officially supported reverse-lookup route.
     *
     * @param uuid the player UUID to look up
     * @return a future that completes with the username string, or {@code null}
     *         if the UUID is not found or the request fails
     */
    public static CompletableFuture<String> getUsernameAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> fetchUsername(uuid), POOL);
    }

    // ──────────────────────────────────────────────────────────────────────
    //  Internals
    // ──────────────────────────────────────────────────────────────────────

    private static void scheduleMojangCheck(String name, UUID entityUUID) {
        // Mark as pending so we don't fire duplicate requests
        mojangCache.put(entityUUID, null);

        POOL.submit(() -> {
            try {
                UUID mojangUUID = fetchUUID(name);
                if (mojangUUID == null) {
                    // Name not found → definitely a bot
                    mojangCache.put(entityUUID, false);
                    return;
                }
                // UUID must match what the server reported
                boolean real = mojangUUID.equals(entityUUID);
                mojangCache.put(entityUUID, real);
            } catch (Exception e) {
                // On failure leave cache entry null (retry next call)
                mojangCache.remove(entityUUID);
            }
        });
    }

    /** Blocking Mojang username → UUID fetch. Always call from a background thread. */
    private static UUID fetchUUID(String username) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5_000);
            conn.setReadTimeout(5_000);

            if (conn.getResponseCode() != 200) return null;

            JsonObject json = JsonParser
                    .parseReader(new InputStreamReader(conn.getInputStream()))
                    .getAsJsonObject();

            String raw = json.get("id").getAsString(); // 32 hex chars, no dashes
            return UUID.fromString(raw.replaceFirst(
                    "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                    "$1-$2-$3-$4-$5"
            ));
        } catch (Exception e) {
            System.out.printf("[PlayerUtils] fetchUUID(%s) failed: %s%n", username, e.getMessage());
            return null;
        }
    }

    /** Blocking Mojang UUID → username fetch. Always call from a background thread. */
    private static String fetchUsername(UUID uuid) {
        try {
            // Endpoint requires the UUID without dashes
            String trimmed = uuid.toString().replace("-", "");
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + trimmed);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5_000);
            conn.setReadTimeout(5_000);

            if (conn.getResponseCode() != 200) return null;

            JsonObject json = JsonParser
                    .parseReader(new InputStreamReader(conn.getInputStream()))
                    .getAsJsonObject();

            return json.get("name").getAsString();
        } catch (Exception e) {
            System.out.printf("[PlayerUtils] fetchUsername(%s) failed: %s%n", uuid, e.getMessage());
            return null;
        }
    }

    /**
     * Ultra-fast premium check with anti-bypass layers.
     * Returns true only if the account is a legitimate Mojang/Microsoft premium account.
     *
     * @param player The player to check (null = check local player)
     * @return true if premium account (online-mode), false if cracked/offline
     */
    public static boolean isPremium(Player player) {
        Minecraft mc = Minecraft.getInstance();
        Player target = player != null ? player : mc.player;

        if (target == null) return false;

        GameProfile profile = target.getGameProfile();

        //~if < 1.21.9 'id()' -> 'getId()'
        UUID uuid = profile.id();

        // Layer 1: UUID Version Check (v4 = online/random, v3 = offline/MD5)
        if (uuid.version() != 4) return false;

        // Layer 2: UUID Variant Check (must be RFC 4122 variant "10x")
        if ((uuid.variant() & 2) == 0) return false;

        // Layer 3: Must have GameProfile properties (textures, etc.)
        //~if < 1.21.9 'properties()' -> 'getProperties()'
        if (profile.properties().isEmpty()) return false;

        // Layer 4: Textures property must be signed by Mojang
        boolean hasSignedTextures = false;
        //~if < 1.21.9 'properties()' -> 'getProperties()'
        for (Property prop : profile.properties().get("textures")) {
            if (prop.hasSignature() && prop.value() != null) {
                hasSignedTextures = true;
                break;
            }
        }

        return hasSignedTextures;
    }

    /** Check if local player is premium */
    public static boolean isLocalPlayerPremium() {
        return isPremium(null);
    }
}