package de.constt.nyra.client.utils;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ThemeUtils {
    protected static int accent    = 0xFF6969FF;
    protected static int secondary = 0xFF505050;
    protected static int bg        = 0xE0262626;
    protected static int text      = 0xFFFFFFFF;

    // ── Background animation palette ──────────────────────────────────────────
    public static int[] backgroundPalette = {
            0xFF1C1C1C, // charcoal black
            0xFF2E0D0D, // dark burgundy
            0xFF3A3A3A, // ash gray
            0xFF7A1A1A, // blood red
            0xFF4A0E0E, // dark wine
            0xFF242424, // dark graphite
    };

    public static String animationStyle = "Marble Swirl";

    // ── Defaults (kept for reset) ─────────────────────────────────────────────
    private static final int[] DEFAULT_PALETTE = {
            0xFF1C1C1C,
            0xFF2E0D0D,
            0xFF3A3A3A,
            0xFF7A1A1A,
            0xFF4A0E0E,
            0xFF242424,
    };
    private static final String DEFAULT_ANIMATION = "Marble Swirl";

    // ── File paths ────────────────────────────────────────────────────────────
    private static final Path THEMES_DIR = Path.of(
            FabricLoader.getInstance().getConfigDir() + "/scalare/themes"
    );
    private static final Path THEME_PATH = THEMES_DIR.resolve("default.cfg");

    private static boolean loaded = false;

    /**
     * The name of the theme that was last loaded via {@link #loadNamedTheme}.
     * "default" means no custom theme is active.
     * Every {@link #saveTheme} call will also mirror changes back into this
     * file so edits made in the Design section are persisted to the right place.
     */
    private static String activeThemeName = "default";

    // ── Public getters ────────────────────────────────────────────────────────

    public static int getBackgroundColor()  { ensureLoaded(); return bg;        }
    public static int getSecondaryColor()   { ensureLoaded(); return secondary;  }
    public static int getAccentColor()      { ensureLoaded(); return accent;     }
    public static int getTextColor()        { ensureLoaded(); return text;       }

    /** Returns a live reference — callers should not cache this array. */
    public static int[] getBackgroundPalette() { ensureLoaded(); return backgroundPalette; }
    public static String getAnimationStyle()    { ensureLoaded(); return animationStyle;   }

    // ── Public setters (each saves immediately) ───────────────────────────────

    public static void setBackgroundColor(int color)  { bg        = color; saveTheme(); }
    public static void setSecondaryColor(int color)   { secondary = color; saveTheme(); }
    public static void setAccentColor(int color)      { accent    = color; saveTheme(); }
    public static void setTextColor(int color)        { text      = color; saveTheme(); }

    public static void setBackgroundPalette(int[] palette) {
        if (palette != null && palette.length == backgroundPalette.length) {
            System.arraycopy(palette, 0, backgroundPalette, 0, palette.length);
        }
        saveTheme();
    }

    public static void setPaletteColor(int index, int color) {
        if (index >= 0 && index < backgroundPalette.length) {
            backgroundPalette[index] = color;
            saveTheme();
        }
    }

    public static void setAnimationStyle(String style) {
        animationStyle = style;
        saveTheme();
    }

    // ── Named theme management ────────────────────────────────────────────────

    /**
     * Returns all theme names found in the themes directory.
     * "default" is always first; custom themes follow in alphabetical order.
     */
    public static List<String> listThemes() {
        ensureLoaded();
        List<String> names = new ArrayList<>();
        names.add("default");
        try {
            if (Files.exists(THEMES_DIR)) {
                try (Stream<Path> stream = Files.list(THEMES_DIR)) {
                    stream.filter(p -> p.toString().endsWith(".cfg"))
                            .map(p -> p.getFileName().toString().replace(".cfg", ""))
                            .filter(n -> !n.equals("default"))
                            .sorted()
                            .forEach(names::add);
                }
            }
        } catch (IOException e) {
            System.err.println("[ThemeUtils] Failed to list themes: " + e.getMessage());
        }
        return names;
    }

    /**
     * Saves the currently active colors and animation style to
     * {@code themes/<name>.cfg}.  The name "default" is silently redirected
     * to the existing default save path so behaviour stays consistent.
     */
    /**
     * Saves the currently active colors and animation style to
     * {@code themes/<n>.cfg}.
     * The name "default" is reserved and cannot be overwritten.
     *
     * @return {@code true} if written successfully, {@code false} if the name
     *         was blank, reserved ("default"), or an IO error occurred
     */
    public static boolean saveThemeAs(String name) {
        if (name == null || name.isBlank()) return false;
        // "default" is a read-only built-in — never overwrite it
        if ("default".equalsIgnoreCase(name.trim())) return false;
        Path path = THEMES_DIR.resolve(sanitiseName(name) + ".cfg");
        try {
            FileUtils.createFile(path, buildThemeContent());
            return true;
        } catch (Exception e) {
            System.err.println("[ThemeUtils] Failed to save theme '" + name + "': " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads a named theme from {@code themes/<name>.cfg}, applies it to the
     * in-memory state, and persists it as the active default.cfg so the next
     * game launch also uses it.
     *
     * @return {@code true} if the file was found and loaded successfully
     */
    public static boolean loadNamedTheme(String name) {
        if (name == null || name.isBlank()) return false;
        // "default" always restores the hardcoded defaults — never reads from file
        if ("default".equalsIgnoreCase(name.trim())) {
            resetToDefault();
            return true;
        }
        Path path = THEMES_DIR.resolve(sanitiseName(name) + ".cfg");
        if (!FileUtils.checkIfFileExist(path)) return false;
        try {
            Map<String, String> raw = parseFile(path);
            applyRawMap(raw);
            activeThemeName = name.trim(); // track which theme is active
            saveTheme(); // persist as the active state for next launch
            return true;
        } catch (Exception e) {
            System.err.println("[ThemeUtils] Failed to load theme '" + name + "': " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes the named theme file.  The built-in "default" theme cannot be
     * deleted (the file is reset to defaults instead of removed, and this
     * method returns {@code false}).
     *
     * @return {@code true} if the file was deleted
     */
    public static boolean deleteTheme(String name) {
        if (name == null || name.isBlank()) return false;
        if ("default".equalsIgnoreCase(name.trim())) return false; // protected
        Path path = THEMES_DIR.resolve(sanitiseName(name) + ".cfg");
        try {
            FileUtils.removeFile(path);
            return true;
        } catch (Exception e) {
            System.err.println("[ThemeUtils] Failed to delete theme '" + name + "': " + e.getMessage());
            return false;
        }
    }

    // ── Load / Save ───────────────────────────────────────────────────────────

    private static void ensureLoaded() {
        if (!loaded) {
            loadTheme();
            loaded = true;
        }
    }

    private static void loadTheme() {
        try {
            if (FileUtils.checkIfFileExist(THEME_PATH)) {
                applyRawMap(parseFile(THEME_PATH));
            } else {
                createDefaultTheme();
            }
        } catch (Exception e) {
            System.err.println("[ThemeUtils] Failed to load theme: " + e.getMessage());
        }
    }

    public static void saveTheme() {
        String content = buildThemeContent();
        try {
            // Always keep default.cfg up-to-date as the active startup state
            FileUtils.createFile(THEME_PATH, content);
        } catch (Exception e) {
            System.err.println("[ThemeUtils] Failed to save default.cfg: " + e.getMessage());
        }
        // Also mirror changes back into the named theme file so edits made
        // via the color pickers are not lost when the user reloads the theme.
        if (!"default".equalsIgnoreCase(activeThemeName)) {
            Path activePath = THEMES_DIR.resolve(sanitiseName(activeThemeName) + ".cfg");
            try {
                FileUtils.createFile(activePath, content);
            } catch (Exception e) {
                System.err.println("[ThemeUtils] Failed to mirror save to '" + activeThemeName + "'.cfg: " + e.getMessage());
            }
        }
    }

    private static void createDefaultTheme() {
        try {
            Files.createDirectories(THEME_PATH.getParent());
            saveTheme();
        } catch (Exception e) {
            System.err.println("[ThemeUtils] Failed to create default theme: " + e.getMessage());
        }
    }

    public static void reloadTheme() {
        loaded = false;
        ensureLoaded();
    }

    public static void resetToDefault() {
        bg        = 0xE0262626;
        secondary = 0xFF505050;
        accent    = 0xFF6969FF;
        text      = 0xFFFFFFFF;
        System.arraycopy(DEFAULT_PALETTE, 0, backgroundPalette, 0, DEFAULT_PALETTE.length);
        animationStyle = DEFAULT_ANIMATION;
        activeThemeName = "default"; // no longer tracking a custom theme
        saveTheme();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /** Applies a raw key→value map to the in-memory color/animation fields. */
    private static void applyRawMap(Map<String, String> raw) {
        bg        = parseColor(raw.get("backgroundColor"),  bg);
        secondary = parseColor(raw.get("secondaryColor"),   secondary);
        accent    = parseColor(raw.get("accentColor"),      accent);
        text      = parseColor(raw.get("textColor"),        text);
        for (int i = 0; i < backgroundPalette.length; i++) {
            backgroundPalette[i] = parseColor(raw.get("paletteColor" + i), backgroundPalette[i]);
        }
        if (raw.containsKey("animationStyle")) {
            animationStyle = raw.get("animationStyle");
        }
    }

    /** Serialises the current state to a .cfg string. */
    private static String buildThemeContent() {
        StringBuilder sb = new StringBuilder();
        sb.append("backgroundColor=0x").append(toHex(bg)).append('\n');
        sb.append("secondaryColor=0x" ).append(toHex(secondary)).append('\n');
        sb.append("accentColor=0x"    ).append(toHex(accent)).append('\n');
        sb.append("textColor=0x"      ).append(toHex(text)).append('\n');
        for (int i = 0; i < backgroundPalette.length; i++) {
            sb.append("paletteColor").append(i)
                    .append("=0x").append(toHex(backgroundPalette[i])).append('\n');
        }
        sb.append("animationStyle=").append(animationStyle).append('\n');
        return sb.toString();
    }

    /** Strips characters that are unsafe in file names. */
    private static String sanitiseName(String name) {
        return name.trim().replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }

    // ── Parsing helpers ───────────────────────────────────────────────────────

    /**
     * Parses every "key=value" line from an arbitrary Path and returns a
     * raw string map.  Shared by both the default load path and named themes.
     */
    private static Map<String, String> parseFile(Path path) throws IOException {
        Map<String, String> map = new HashMap<>();
        String content = Files.readString(path);
        for (String line : content.split("\n")) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            int eq = line.indexOf('=');
            if (eq < 1) continue;
            map.put(line.substring(0, eq).trim(), line.substring(eq + 1).trim());
        }
        return map;
    }

    /** Parses a hex/decimal color string, returns {@code fallback} on any error. */
    private static int parseColor(String raw, int fallback) {
        if (raw == null) return fallback;
        try {
            if (raw.startsWith("0x") || raw.startsWith("0X")) {
                return (int) Long.parseLong(raw.substring(2), 16);
            }
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            System.err.println("[ThemeUtils] Bad color value: " + raw);
            return fallback;
        }
    }

    private static String toHex(int color) {
        return String.format("%08X", color);
    }

    // ── Color utilities ───────────────────────────────────────────────────────

    public static int getColorWithAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }

    public static int blendColors(int color1, int color2, float ratio) {
        ratio = Math.max(0, Math.min(1, ratio));
        int a = (int)((color1 >> 24 & 0xFF) * (1 - ratio) + (color2 >> 24 & 0xFF) * ratio);
        int r = (int)((color1 >> 16 & 0xFF) * (1 - ratio) + (color2 >> 16 & 0xFF) * ratio);
        int g = (int)((color1 >>  8 & 0xFF) * (1 - ratio) + (color2 >>  8 & 0xFF) * ratio);
        int b = (int)((color1       & 0xFF) * (1 - ratio) + (color2       & 0xFF) * ratio);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int darkenColor(int color, float percentage) {
        percentage = Math.max(0, Math.min(1, percentage));
        int a = (color >> 24) & 0xFF;
        int r = (int)((color >> 16 & 0xFF) * (1 - percentage));
        int g = (int)((color >>  8 & 0xFF) * (1 - percentage));
        int b = (int)((color       & 0xFF) * (1 - percentage));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int lightenColor(int color, float percentage) {
        percentage = Math.max(0, Math.min(1, percentage));
        int a = (color >> 24) & 0xFF;
        int r = (int)((color >> 16 & 0xFF) + (255 - (color >> 16 & 0xFF)) * percentage);
        int g = (int)((color >>  8 & 0xFF) + (255 - (color >>  8 & 0xFF)) * percentage);
        int b = (int)((color       & 0xFF) + (255 - (color       & 0xFF)) * percentage);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int getHoverColor(int color)   { return lightenColor(color, 0.15f); }
    public static int getPressedColor(int color) { return darkenColor(color,  0.20f); }

    public static boolean isDarkColor(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >>  8) & 0xFF;
        int b =  color        & 0xFF;
        return (r * 0.299 + g * 0.587 + b * 0.114) < 128;
    }

    public static int getContrastingTextColor(int bg) {
        return isDarkColor(bg) ? 0xFFFFFFFF : 0xFF000000;
    }
}