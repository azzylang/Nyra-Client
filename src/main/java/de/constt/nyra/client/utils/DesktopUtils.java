package eu.scalare.client.utils;

import java.awt.*;
import java.io.IOException;

public class DesktopUtils {

    public static void sendOSNotification(String title, String message) {
        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (os.contains("win")) {
                sendWindows(title, message);
            } else if (os.contains("mac")) {
                sendMac(title, message);
            } else {
                sendLinux(title, message);
            }
        } catch (IOException | AWTException e) {
            e.printStackTrace();
        }
    }

    private static void sendWindows(String title, String message) throws IOException, AWTException {
        // Legacy SystemTray
        if (!java.awt.SystemTray.isSupported()) return;

        var tray = java.awt.SystemTray.getSystemTray();
        var image = java.awt.Toolkit.getDefaultToolkit().createImage(new byte[0]);
        var trayIcon = new java.awt.TrayIcon(image, "Minecraft Mod");
        trayIcon.setImageAutoSize(true);
        tray.add(trayIcon);
        trayIcon.displayMessage(title, message, java.awt.TrayIcon.MessageType.INFO);
        tray.remove(trayIcon);
    }

    private static void sendMac(String title, String message) throws IOException {
        Runtime.getRuntime().exec(new String[]{
                "osascript", "-e",
                "display notification \"" + message + "\" with title \"" + title + "\""
        });
    }

    private static void sendLinux(String title, String message) throws IOException {
        Runtime.getRuntime().exec(new String[]{
                "notify-send", title, message
        });
    }
}
