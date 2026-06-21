package de.constt.nyra.client.mixin.imgui;

import de.constt.nyra.client.impl.ImGuiImpl;
import de.constt.nyra.client.impl.RenderInterface;
import imgui.ImGui;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    private static Logger LOGGER;

    @Inject(method = "render", at = @At("RETURN"))
    private void render(DeltaTracker deltaTracker, boolean advanceGameTime, CallbackInfo ci) {
        //~ if <26.2 'gui.screen()' -> 'screen'
        if (minecraft.gui.screen() instanceof final RenderInterface renderInterface) {
            ImGuiImpl.beginImGuiRendering();
            renderInterface.render(ImGui.getIO());
            ImGuiImpl.endImGuiRendering();
        }
    }

}