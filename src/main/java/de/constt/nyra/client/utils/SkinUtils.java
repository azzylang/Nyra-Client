package de.constt.nyra.client.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SkinUtils {

    public static final int PENDING = -1;
    public static final int FAILED  = -2;

    private static final int HEAD_X = 8, HEAD_Y = 8, HEAD_W = 8, HEAD_H = 8;

    private static final class TextureEntry {
        volatile int id;
        volatile boolean valid;
        TextureEntry(int id, boolean valid) {
            this.id = id;
            this.valid = valid;
        }
    }

    private static final Map<UUID, TextureEntry> textureCache = new ConcurrentHashMap<>();

    private static final ExecutorService POOL = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "SkinUtils-Worker");
        t.setDaemon(true);
        return t;
    });

    public static int getHeadTextureId(UUID uuid) {
        TextureEntry entry = textureCache.get(uuid);

        if (entry != null) {
            if (!entry.valid) return FAILED;
            return entry.id;
        }

        textureCache.put(uuid, new TextureEntry(PENDING, true));
        POOL.submit(() -> fetchAndUpload(uuid));
        return PENDING;
    }

    public static void clearCache() {
        Minecraft mc = Minecraft.getInstance();

        mc.execute(() -> {
            for (TextureEntry entry : textureCache.values()) {
                if (entry.valid && entry.id > 0) {
                    GL11.glDeleteTextures(entry.id);
                    entry.valid = false;
                }
            }
            textureCache.clear();
        });
    }

    private static void fetchAndUpload(UUID uuid) {

        TextureEntry entry = textureCache.get(uuid);
        if (entry == null) return;

        try {

            String skinUrl = fetchSkinUrl(uuid);
            if (skinUrl == null) {
                entry.id = FAILED;
                entry.valid = false;
                return;
            }

            BufferedImage skin = downloadImage(skinUrl);
            if (skin == null) {
                entry.id = FAILED;
                entry.valid = false;
                return;
            }

            BufferedImage head = cropHead(skin);

            Minecraft.getInstance().execute(() -> {

                TextureEntry current = textureCache.get(uuid);
                if (current == null || !current.valid) return;

                try {

                    int id = uploadTexture(head);

                    current.id = id;
                    current.valid = true;

                } catch (Exception e) {

                    current.id = FAILED;
                    current.valid = false;

                    System.err.printf("[SkinUtils] GL upload failed for %s: %s%n", uuid, e.getMessage());

                }

            });

        } catch (Exception e) {

            entry.id = FAILED;
            entry.valid = false;

            System.err.printf("[SkinUtils] fetch failed for %s: %s%n", uuid, e.getMessage());

        }
    }

    private static BufferedImage cropHead(BufferedImage skin) {

        BufferedImage head = new BufferedImage(HEAD_W, HEAD_H, BufferedImage.TYPE_INT_ARGB);

        head.getGraphics().drawImage(
                skin.getSubimage(HEAD_X, HEAD_Y, HEAD_W, HEAD_H),
                0,
                0,
                null
        );

        return head;
    }

    private static String fetchSkinUrl(UUID uuid) {

        try {

            String trimmed = uuid.toString().replace("-", "");

            URL url = URI.create(
                    "https://sessionserver.mojang.com/session/minecraft/profile/" + trimmed
            ).toURL();

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() != 200) return null;

            JsonObject profile = JsonParser.parseReader(
                    new java.io.InputStreamReader(conn.getInputStream())
            ).getAsJsonObject();

            String encoded = profile
                    .getAsJsonArray("properties")
                    .get(0).getAsJsonObject()
                    .get("value").getAsString();

            String decoded = new String(Base64.getDecoder().decode(encoded));

            JsonObject textures = JsonParser.parseString(decoded)
                    .getAsJsonObject()
                    .getAsJsonObject("textures");

            return textures
                    .getAsJsonObject("SKIN")
                    .get("url").getAsString();

        } catch (Exception e) {

            System.err.printf("[SkinUtils] fetchSkinUrl failed for %s: %s%n", uuid, e.getMessage());

            return null;

        }
    }

    private static BufferedImage downloadImage(String skinUrl) {

        try {

            HttpURLConnection conn =
                    (HttpURLConnection) URI.create(skinUrl).toURL().openConnection();

            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() != 200) return null;

            try (InputStream is = conn.getInputStream()) {

                return ImageIO.read(is);

            }

        } catch (Exception e) {

            System.err.printf("[SkinUtils] download failed (%s): %s%n", skinUrl, e.getMessage());

            return null;

        }
    }

    private static int uploadTexture(BufferedImage image) {

        int width = image.getWidth();
        int height = image.getHeight();

        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

        ByteBuffer buffer =
                ByteBuffer.allocateDirect(width * height * 4)
                        .order(ByteOrder.nativeOrder());

        for (int argb : pixels) {

            buffer.put((byte)((argb >> 16) & 0xFF));
            buffer.put((byte)((argb >> 8) & 0xFF));
            buffer.put((byte)(argb & 0xFF));
            buffer.put((byte)((argb >> 24) & 0xFF));

        }

        buffer.flip();

        int id = GL11.glGenTextures();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                GL11.GL_RGBA,
                width,
                height,
                0,
                GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE,
                buffer
        );

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        return id;
    }

}