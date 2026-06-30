package de.constt.nyra.client.mixins;

import de.constt.nyra.client.screens.BaseScreen;
import de.constt.nyra.client.screens.ExampleScreen;
import de.constt.nyra.client.screens.ModulesScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

    @Shadow
    @Final
    private static Logger LOGGER;

    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void render(CallbackInfo ci) {
        this.addRenderableWidget(Button.builder(Component.literal("Modules Screen"), (button) -> {
            Minecraft.getInstance().setScreenAndShow(new ModulesScreen());
        }).bounds(this.width - 75 - 3, 3, 70, 20).build());
    }
}