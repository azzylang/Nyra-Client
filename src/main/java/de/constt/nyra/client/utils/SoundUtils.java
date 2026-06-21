package de.constt.nyra.client.utils;


import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class SoundUtils {
    private static final Minecraft mc = Minecraft.getInstance();

    public static void playSound(SoundEvent sound, float pitch, boolean isSilent) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                mc.player.playSound(sound, 1.0f, pitch);
            }
    }

    // ----------------------
    // Custom client-side sound from path
    // ----------------------
    /**
     * Play a custom client-side sound from your assets' folder.
     *
     * @param path relative path in assets/scalare/, e.g., "sounds/warning.ogg"
     * @param pitch 0.5F - 2.0F
     */
    public static void playCustomSound(String path, float pitch) {
        Minecraft mc = Minecraft.getInstance();

        SoundEvent soundEvent = SoundEvent.createFixedRangeEvent(Identifier.fromNamespaceAndPath("scalare", path), 1.0F);
        mc.getSoundManager().play(SimpleSoundInstance.forUI(soundEvent, pitch, 1.0F));

    }
}
