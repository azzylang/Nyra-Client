package de.constt.nyra.client.events;

import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ClientTickEventsEvent {

    private static boolean iconSet = false;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register((minecraft) -> {
            if (!iconSet) {
                try {
                    ClientTickEventsEvent.setWindowIcon("/assets/nyra/textures/logo-64.png");
                    iconSet = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // TODO: Add version changes via stonecutter if needed
    public static void setWindowIcon(String pathInJar) throws IOException {
        try (
                InputStream inputStream = Minecraft.class.getResourceAsStream(pathInJar);
                MemoryStack stack = MemoryStack.stackPush()
        ) {
            if (inputStream == null) throw new IOException("Missing Icon: " + pathInJar);

            NativeImage image = NativeImage.read(inputStream);

            ByteBuffer pixelBuffer = MemoryUtil.memAlloc(image.getWidth() * image.getHeight() * 4);
            pixelBuffer.asIntBuffer().put(image.getPixelsABGR());

            GLFWImage.Buffer buf = GLFWImage.malloc(1, stack);
            buf.width(image.getWidth());
            buf.height(image.getHeight());
            buf.pixels(pixelBuffer);

            GLFW.glfwSetWindowIcon(
                    Minecraft.getInstance().getWindow().handle(),
                    buf
            );

            image.close();
            MemoryUtil.memFree(pixelBuffer);
        }
    }
}
