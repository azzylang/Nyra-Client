package de.constt.nyra.client.roots.modules.render;

import de.constt.nyra.client.annotations.ModuleInfoAnnotation;
import de.constt.nyra.client.roots.implementations.CategoryImplementation;
import de.constt.nyra.client.roots.implementations.ModuleImplementation;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;


@ModuleInfoAnnotation(
        name = "Fullbright",
        description = "Changes the gamma to be always max bright",
        category = CategoryImplementation.Categories.RENDER,
        internalModuleName = "fullbright"
)
public class FullbrightModule extends ModuleImplementation {
    @Override
    public void onEnable() {
        enableNightVision();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        disableNightVision();
        super.onDisable();
    }

    public static void enableNightVision() {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            client.player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 1_000_000, 0, false, false, false));
        }
    }

    public static void disableNightVision() {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            client.player.removeEffect(MobEffects.NIGHT_VISION);
        }
    }
}