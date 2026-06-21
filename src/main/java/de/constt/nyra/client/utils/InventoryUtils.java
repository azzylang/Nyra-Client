package de.constt.nyra.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

public class InventoryUtils {

    public static boolean itemWithNameExists(String itemName, boolean lookInInventory) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;

        int maxSlot = lookInInventory ? 36 : 9; // 0-8 hotbar only, 0-35 full inventory

        for (int i = 0; i < maxSlot; i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getHoverName().getString().contains(itemName)) {
                return true;
            }
        }

        return false;
    }

    public static int getHotbarSlot(String itemName) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getHoverName().getString().contains(itemName)) {
                return i;
            }
        }

        return -1; // not found in hotbar
    }
}