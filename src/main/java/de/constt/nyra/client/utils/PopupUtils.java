package de.constt.nyra.client.utils;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;

import java.util.function.Consumer;

public class PopupUtils {

    private static String currentPopupId = null;
    private static Runnable onConfirm = null;
    private static Runnable onCancel = null;

    /**
     * Shows a confirmation dialog with Yes/No buttons
     *
     * @param title The popup title
     * @param message The message to display
     * @param onYes Action to run when Yes is clicked
     */
    public static void showConfirmation(String title, String message, Runnable onYes) {
        showConfirmation(title, message, onYes, null);
    }

    /**
     * Shows a confirmation dialog with Yes/No buttons and cancel callback
     *
     * @param title The popup title
     * @param message The message to display
     * @param onYes Action to run when Yes is clicked
     * @param onNo Action to run when No is clicked (optional)
     */
    public static void showConfirmation(String title, String message, Runnable onYes, Runnable onNo) {
        currentPopupId = title;
        onConfirm = onYes;
        onCancel = onNo;
        ImGui.openPopup(title);
    }

    /**
     * Renders the confirmation popup (call this in your render loop)
     */
    public static void renderConfirmation(String title, String message) {
        renderConfirmation(title, message, "Yes", "No");
    }

    /**
     * Renders the confirmation popup with custom button labels
     */
    public static void renderConfirmation(String title, String message, String yesLabel, String noLabel) {
        ImGuiIO io = ImGui.getIO();
        ImGui.setNextWindowPos(io.getDisplaySizeX() / 2f, io.getDisplaySizeY() / 2f, ImGuiCond.Appearing, 0.5f, 0.5f);

        if(ImGui.beginPopupModal(title, ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoMove)) {
            // Split message by newlines for multi-line support
            for(String line : message.split("\n")) {
                ImGui.text(line);
            }
            ImGui.separator();

            if(ImGui.button(yesLabel, 120, 0)) {
                if(onConfirm != null) onConfirm.run();
                ImGui.closeCurrentPopup();
                cleanup();
            }

            ImGui.sameLine();

            if(ImGui.button(noLabel, 120, 0)) {
                if(onCancel != null) onCancel.run();
                ImGui.closeCurrentPopup();
                cleanup();
            }

            ImGui.endPopup();
        }
    }

    /**
     * Shows a simple notification/alert popup with just an OK button
     */
    public static void showAlert(String title, String message) {
        currentPopupId = title;
        ImGui.openPopup(title);
    }

    /**
     * Renders an alert popup
     */
    public static void renderAlert(String title, String message) {
        ImGuiIO io = ImGui.getIO();
        ImGui.setNextWindowPos(io.getDisplaySizeX() / 2f, io.getDisplaySizeY() / 2f, ImGuiCond.Appearing, 0.5f, 0.5f);

        if(ImGui.beginPopupModal(title, ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoMove)) {
            for(String line : message.split("\n")) {
                ImGui.text(line);
            }
            ImGui.separator();

            if(ImGui.button("OK", 120, 0)) {
                ImGui.closeCurrentPopup();
                cleanup();
            }

            ImGui.endPopup();
        }
    }

    /**
     * Shows a warning popup with a warning icon (⚠)
     */
    public static void showWarning(String title, String message, Runnable onOk) {
        currentPopupId = title;
        onConfirm = onOk;
        ImGui.openPopup(title);
    }

    /**
     * Renders a warning popup
     */
    public static void renderWarning(String title, String message) {
        ImGuiIO io = ImGui.getIO();
        ImGui.setNextWindowPos(io.getDisplaySizeX() / 2f, io.getDisplaySizeY() / 2f, ImGuiCond.Appearing, 0.5f, 0.5f);

        if(ImGui.beginPopupModal(title, ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoMove)) {
            ImGui.textColored(255, 255, 0, 255, "⚠ WARNING");
            ImGui.separator();

            for(String line : message.split("\n")) {
                ImGui.text(line);
            }
            ImGui.separator();

            if(ImGui.button("I Understand", 150, 0)) {
                if(onConfirm != null) onConfirm.run();
                ImGui.closeCurrentPopup();
                cleanup();
            }

            ImGui.endPopup();
        }
    }

    /**
     * Shows an error popup with an error icon (✖)
     */
    public static void showError(String title, String message) {
        currentPopupId = title;
        ImGui.openPopup(title);
    }

    /**
     * Renders an error popup
     */
    public static void renderError(String title, String message) {
        ImGuiIO io = ImGui.getIO();
        ImGui.setNextWindowPos(io.getDisplaySizeX() / 2f, io.getDisplaySizeY() / 2f, ImGuiCond.Appearing, 0.5f, 0.5f);

        if(ImGui.beginPopupModal(title, ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoMove)) {
            ImGui.textColored(255, 0, 0, 255, "✖ ERROR");
            ImGui.separator();

            for(String line : message.split("\n")) {
                ImGui.text(line);
            }
            ImGui.separator();

            if(ImGui.button("OK", 120, 0)) {
                ImGui.closeCurrentPopup();
                cleanup();
            }

            ImGui.endPopup();
        }
    }

    /**
     * Shows a success popup with a checkmark (✓)
     */
    public static void showSuccess(String title, String message) {
        currentPopupId = title;
        ImGui.openPopup(title);
    }

    /**
     * Renders a success popup
     */
    public static void renderSuccess(String title, String message) {
        ImGuiIO io = ImGui.getIO();
        ImGui.setNextWindowPos(io.getDisplaySizeX() / 2f, io.getDisplaySizeY() / 2f, ImGuiCond.Appearing, 0.5f, 0.5f);

        if(ImGui.beginPopupModal(title, ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoMove)) {
            ImGui.textColored(0, 255, 0, 255, "✓ SUCCESS");
            ImGui.separator();

            for(String line : message.split("\n")) {
                ImGui.text(line);
            }
            ImGui.separator();

            if(ImGui.button("OK", 120, 0)) {
                ImGui.closeCurrentPopup();
                cleanup();
            }

            ImGui.endPopup();
        }
    }

    /**
     * Shows a text input popup
     */
    public static void showTextInput(String title, String message, String defaultValue, Consumer<String> onSubmit) {
        currentPopupId = title;
        ImGui.openPopup(title);
    }

    private static ImString inputBuffer = new ImString("");

    /**
     * Renders a text input popup
     */
    public static void renderTextInput(String title, String message, Consumer<String> onSubmit) {
        ImGuiIO io = ImGui.getIO();
        ImGui.setNextWindowPos(io.getDisplaySizeX() / 2f, io.getDisplaySizeY() / 2f, ImGuiCond.Appearing, 0.5f, 0.5f);

        if(ImGui.beginPopupModal(title, ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoMove)) {
            ImGui.text(message);
            ImGui.separator();

            ImGui.setNextItemWidth(300);
            /* unused currently
            if(ImGui.inputText("##input", inputBuffer, 256)) {
                // Text changed
            }
            */

            ImGui.separator();

            if(ImGui.button("Submit", 120, 0)) {
                if(onSubmit != null) onSubmit.accept(String.valueOf(inputBuffer));
                inputBuffer = new ImString("");
                ImGui.closeCurrentPopup();
                cleanup();
            }

            ImGui.sameLine();

            if(ImGui.button("Cancel", 120, 0)) {
                inputBuffer = new ImString("");
                ImGui.closeCurrentPopup();
                cleanup();
            }

            ImGui.endPopup();
        }
    }

    /**
     * Shows a loading popup (no buttons, just a message)
     */
    public static void showLoading(String title, String message) {
        currentPopupId = title;
        ImGui.openPopup(title);
    }

    /**
     * Renders a loading popup
     */
    public static void renderLoading(String title, String message) {
        ImGuiIO io = ImGui.getIO();
        ImGui.setNextWindowPos(io.getDisplaySizeX() / 2f, io.getDisplaySizeY() / 2f, ImGuiCond.Appearing, 0.5f, 0.5f);

        if(ImGui.beginPopupModal(title, ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoTitleBar)) {
            ImGui.text(message);
            ImGui.sameLine();
            ImGui.text("...");
            ImGui.endPopup();
        }
    }

    /**
     * Closes any currently open popup
     */
    public static void closeCurrentPopup() {
        ImGui.closeCurrentPopup();
        cleanup();
    }

    /**
     * Cleanup internal state
     */
    private static void cleanup() {
        currentPopupId = null;
        onConfirm = null;
        onCancel = null;
    }

    /**
     * Quick helper for theme reset confirmation
     */
    public static void showThemeResetConfirmation(Runnable onConfirm) {
        showConfirmation(
                "Reset Theme?",
                "Are you sure you want to reset your theme to default?\nThis action cannot be undone.",
                onConfirm
        );
    }
}