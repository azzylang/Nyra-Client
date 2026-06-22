package de.constt.nyra.client.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;

@Mixin(SplashManager.class)
public abstract class SplashManagerMixin {
    @Unique
    private static final Random random = new Random();
    @Unique
    private final List<String> splashes = getSplashes();

    @Inject(method = "getSplash", at = @At("HEAD"), cancellable = true)
    private void onApply(CallbackInfoReturnable<SplashRenderer> cir) {
        String splash = splashes.get(random.nextInt(splashes.size()));
        cir.setReturnValue(new SplashRenderer(Component.literal(splash)));
    }

    @Unique
    private static List<String> getSplashes() {
        return List.of(
                ChatFormatting.GOLD + "Nyra Client"
        );
    }
}