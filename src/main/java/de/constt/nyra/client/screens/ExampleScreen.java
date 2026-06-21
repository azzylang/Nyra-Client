package de.constt.nyra.client.screens;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ExampleScreen extends Screen {

    ExampleScreen(Component title) {
        super(title);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Only relevant in singleplayer
    }

}