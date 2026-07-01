package de.constt.nyra.client.roots.modules.render;

import de.constt.nyra.client.annotations.ModuleInfoAnnotation;
import de.constt.nyra.client.roots.implementations.CategoryImplementation;
import de.constt.nyra.client.roots.implementations.ModuleImplementation;
import de.constt.nyra.client.roots.implementations.settings.DoubleSettingImplementation;
import net.minecraft.client.Minecraft;


@ModuleInfoAnnotation(
        name = "Fullbright",
        description = "Changes the gamma to be always max bright",
        category = CategoryImplementation.Categories.RENDER,
        internalModuleName = "fullbright"
)
public class FullbrightModule extends ModuleImplementation {
    private final DoubleSettingImplementation gammaSetting;

    public FullbrightModule() {
        gammaSetting = new DoubleSettingImplementation("Gamma", 0.5F, 0.0F, 1F);

        registerSetting(gammaSetting);
    }

    @Override
    public void onEnable() {
        setGamma(gammaSetting.get());
        super.onEnable();
    }

    @Override
    public void onDisable() {
        setGamma(getDefaultGamma());
        super.onDisable();
    }

    private static double getDefaultGamma() {
        return 0.5F;
    }

    private static void setGamma(double gamma) {
        if (Minecraft.getInstance().options != null) {
            Minecraft.getInstance().options.gamma().set(gamma);
        }
    }
}