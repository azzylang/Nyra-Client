package de.constt.nyra.client.roots.implementations.settings;

import de.constt.nyra.client.roots.implementations.SettingImplementation;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.stream.Collectors;

public final class BlockSettingImplementation extends SettingImplementation<Block> {

    private final ImString searchBuffer;
    private volatile List<Block> filteredBlocks;
    private boolean requestOpenPopup = false;
    private String lastSearch = "";

    public BlockSettingImplementation(String name, Block defaultValue) {
        super(name, defaultValue);
        this.searchBuffer = new ImString(256);
        updateFilteredBlocks("");
    }

    private static String safe(String s) {
        if (s == null) return "";
        return s.indexOf('%') == -1 ? s : s.replace("%", "%%");
    }

    private static String blockName(Block block) {
        var key = BuiltInRegistries.BLOCK.getKey(block);
        if (key == null) return "unknown";
        return key.getNamespace().equals("minecraft") ? key.getPath() : key.toString();
    }

    @Override
    public void renderImGui() {
        String displayName = blockName(value);

        ImGui.text(safe(getName()) + ":");
        ImGui.sameLine();

        if (ImGui.button(safe(displayName) + "##btn_" + safe(getName()))) {
            requestOpenPopup = true;
            searchBuffer.set("");
            lastSearch = "";
            updateFilteredBlocks("");
        }

        if (requestOpenPopup) {
            ImGui.openPopup("Select Block##block_selector_" + safe(getName()));
            requestOpenPopup = false;
        }

        ImGui.setNextWindowSize(340, 480);
        if (ImGui.beginPopupModal(
                "Select Block##block_selector_" + safe(getName()),
                ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove)) {

            ImGui.setNextItemWidth(320);
            if (ImGui.inputTextWithHint("##search_" + safe(getName()), "Search blocks...", searchBuffer)) {
                String current = searchBuffer.get();
                if (!current.equals(lastSearch)) {
                    lastSearch = current;
                    updateFilteredBlocks(current);
                }
            }

            ImGui.separator();

            if (ImGui.beginChild("block_list_" + safe(getName()), 320, 370, false)) {
                List<Block> localList = filteredBlocks;

                if (localList != null) {
                    for (Block block : localList) {
                        String label = safe(blockName(block)) + "##sel_" + safe(blockName(block)) + "_" + safe(getName());
                        boolean selected = block == value;

                        if (selected) {
                            ImGui.pushStyleColor(imgui.flag.ImGuiCol.Text, 0xFF55FF55);
                        }

                        if (ImGui.selectable(label, selected)) {
                            value = block;
                            ImGui.closeCurrentPopup();
                        }

                        if (selected) {
                            ImGui.popStyleColor();
                        }
                    }
                }

                ImGui.endChild();
            }

            ImGui.separator();

            if (ImGui.button("Cancel##cancel_" + safe(getName()), 320, 0)) {
                ImGui.closeCurrentPopup();
            }

            ImGui.endPopup();
        }
    }

    private void updateFilteredBlocks(String search) {
        if (search == null || search.isEmpty()) {
            filteredBlocks = BuiltInRegistries.BLOCK.stream()
                    .filter(b -> b != Blocks.AIR)
                    .sorted((a, b) -> blockName(a).compareTo(blockName(b)))
                    .collect(Collectors.toList());
            return;
        }

        String lower = search.toLowerCase();
        filteredBlocks = BuiltInRegistries.BLOCK.stream()
                .filter(b -> b != Blocks.AIR)
                .filter(b -> blockName(b).toLowerCase().contains(lower))
                .sorted((a, b) -> blockName(a).compareTo(blockName(b)))
                .collect(Collectors.toList());
    }

    public Block getBlock() { return value; }

    public int getBlockId() { return BuiltInRegistries.BLOCK.getId(value); }
}