package de.constt.nyra.client.roots.implementations.settings;

import de.constt.nyra.client.roots.implementations.SettingImplementation;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.stream.Collectors;

public final class EntitySettingImplementation extends SettingImplementation<EntityType<?>> {

    public enum Mode {
        ALL,
        SINGLE,
        GROUP
    }

    public enum EntityGroup {
        HOSTILE,
        PASSIVE,
        PLAYERS,
        BOSSES,
        MISC;

        public boolean matches(Entity e) {
            return switch (this) {
                case HOSTILE -> e instanceof Monster;
                case PASSIVE -> e instanceof Animal;
                case PLAYERS -> e instanceof Player;
                case BOSSES  -> e instanceof EnderDragon || e instanceof WitherBoss;
                case MISC    -> e instanceof Mob && !(e instanceof Monster) && !(e instanceof Animal);
            };
        }
    }

    private Mode mode = Mode.ALL;
    private EntityGroup group = EntityGroup.HOSTILE;

    private final ImString searchBuffer;
    private volatile List<EntityType<?>> filteredEntities;
    private boolean requestOpenPopup = false;
    private String lastSearch = "";

    public EntitySettingImplementation(String name, EntityType<?> defaultValue) {
        super(name, defaultValue);
        this.searchBuffer = new ImString(256);
        updateFilteredEntities("");
    }

    private static String safe(String s) {
        if (s == null) return "";
        return s.indexOf('%') == -1 ? s : s.replace("%", "%%");
    }

    private static String entityName(EntityType<?> entityType) {
        var key = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        return key.getNamespace().equals("minecraft") ? key.getPath() : key.toString();
    }

    @Override
    public void renderImGui() {

        ImGui.text(getName() + ":");
        ImGui.sameLine();

        if (ImGui.beginCombo("##mode_" + getName(), mode.name())) {
            for (Mode m : Mode.values()) {
                boolean selected = m == mode;
                if (ImGui.selectable(m.name(), selected)) {
                    mode = m;
                }
                if (selected) ImGui.setItemDefaultFocus();
            }
            ImGui.endCombo();
        }

        if (mode == Mode.GROUP) {
            if (ImGui.beginCombo("Group##group_" + getName(), group.name())) {
                for (EntityGroup g : EntityGroup.values()) {
                    boolean selected = g == group;
                    if (ImGui.selectable(g.name(), selected)) {
                        group = g;
                    }
                    if (selected) ImGui.setItemDefaultFocus();
                }
                ImGui.endCombo();
            }
            return;
        }

        if (mode == Mode.SINGLE) {

            String displayName = entityName(value);

            if (ImGui.button(safe(displayName) + "##btn_" + safe(getName()))) {
                requestOpenPopup = true;
                searchBuffer.set("");
                lastSearch = "";
                updateFilteredEntities("");
            }

            if (requestOpenPopup) {
                ImGui.openPopup("Select Entity##entity_selector_" + safe(getName()));
                requestOpenPopup = false;
            }

            ImGui.setNextWindowSize(340, 480);
            if (ImGui.beginPopupModal(
                    "Select Entity##entity_selector_" + safe(getName()),
                    ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove)) {

                ImGui.setNextItemWidth(320);
                if (ImGui.inputTextWithHint("##search_" + safe(getName()), "Search entities...", searchBuffer)) {
                    String current = searchBuffer.get();
                    if (!current.equals(lastSearch)) {
                        lastSearch = current;
                        updateFilteredEntities(current);
                    }
                }

                ImGui.separator();

                if (ImGui.beginChild("entity_list_" + safe(getName()), 320, 370, false)) {
                    List<EntityType<?>> localList = filteredEntities;

                    if (localList != null) {
                        for (EntityType<?> entityType : localList) {
                            String name = entityName(entityType);
                            String label = safe(name) + "##sel_" + safe(name);
                            boolean selected = entityType == value;

                            if (selected) {
                                ImGui.pushStyleColor(imgui.flag.ImGuiCol.Text, 0xFF55FF55);
                            }

                            if (ImGui.selectable(label, selected)) {
                                value = entityType;
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
    }

    private void updateFilteredEntities(String search) {
        if (search == null || search.isEmpty()) {
            filteredEntities = BuiltInRegistries.ENTITY_TYPE.stream()
                    .sorted((a, b) -> entityName(a).compareTo(entityName(b)))
                    .collect(Collectors.toList());
            return;
        }

        String lower = search.toLowerCase();
        filteredEntities = BuiltInRegistries.ENTITY_TYPE.stream()
                .filter(e -> entityName(e).toLowerCase().contains(lower))
                .sorted((a, b) -> entityName(a).compareTo(entityName(b)))
                .collect(Collectors.toList());
    }

    public boolean matches(Entity entity) {
        return switch (mode) {
            case ALL -> true;
            case SINGLE -> entity.getType() == value;
            case GROUP -> group.matches(entity);
        };
    }
}