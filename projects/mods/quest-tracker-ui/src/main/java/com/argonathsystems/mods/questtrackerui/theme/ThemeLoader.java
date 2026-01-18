package com.argonathsystems.mods.questtrackerui.theme;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Loads themes from YAML configuration files.
 */
public class ThemeLoader {
    
    private final Yaml yaml;
    
    /**
     * Create a new theme loader.
     */
    public ThemeLoader() {
        this.yaml = new Yaml();
    }
    
    /**
     * Load a theme from a file path.
     *
     * @param path Path to the theme YAML file
     * @return Loaded theme
     * @throws IOException If the file cannot be read
     * @throws ThemeParseException If the theme is invalid
     */
    public Theme loadFromFile(Path path) throws IOException, ThemeParseException {
        try (Reader reader = Files.newBufferedReader(path)) {
            Map<String, Object> data = yaml.load(reader);
            return parseTheme(data, path.getFileName().toString());
        }
    }
    
    /**
     * Load a theme from an input stream.
     *
     * @param input Input stream containing YAML data
     * @param sourceName Name for error reporting
     * @return Loaded theme
     * @throws ThemeParseException If the theme is invalid
     */
    public Theme loadFromStream(InputStream input, String sourceName) throws ThemeParseException {
        Map<String, Object> data = yaml.load(input);
        return parseTheme(data, sourceName);
    }
    
    /**
     * Load a theme from a YAML string.
     *
     * @param yamlContent YAML content
     * @param sourceName Name for error reporting
     * @return Loaded theme
     * @throws ThemeParseException If the theme is invalid
     */
    public Theme loadFromString(String yamlContent, String sourceName) throws ThemeParseException {
        Map<String, Object> data = yaml.load(yamlContent);
        return parseTheme(data, sourceName);
    }
    
    @SuppressWarnings("unchecked")
    private Theme parseTheme(Map<String, Object> data, String sourceName) throws ThemeParseException {
        if (data == null) {
            throw new ThemeParseException("Empty theme file: " + sourceName);
        }
        
        Map<String, Object> themeData = (Map<String, Object>) data.get("theme");
        if (themeData == null) {
            themeData = data; // Try root level
        }
        
        try {
            String id = getString(themeData, "id", sourceName.replace(".yml", "").replace(".yaml", ""));
            String name = getString(themeData, "name", id);
            String author = getString(themeData, "author", "Unknown");
            boolean isPremium = getBoolean(themeData, "premium", false);
            
            ColorScheme colors = parseColors((Map<String, Object>) themeData.get("colors"));
            FontConfig fonts = parseFonts((Map<String, Object>) themeData.get("fonts"));
            IconConfig icons = parseIcons((Map<String, Object>) themeData.get("icons"));
            BorderConfig borders = parseBorders((Map<String, Object>) themeData.get("borders"));
            
            return new Theme(id, name, author, isPremium, colors, fonts, icons, borders);
        } catch (ClassCastException e) {
            throw new ThemeParseException("Invalid theme structure in " + sourceName + ": " + e.getMessage());
        }
    }
    
    private ColorScheme parseColors(Map<String, Object> data) {
        if (data == null) {
            return ColorScheme.DARK_FANTASY;
        }
        
        return new ColorScheme(
            getString(data, "background", "#1a1a2e"),
            getDouble(data, "background_opacity", 0.85),
            getString(data, "border", "#4a4a6a"),
            getString(data, "text_primary", "#ffffff"),
            getString(data, "text_secondary", "#aaaaaa"),
            getString(data, "text_completed", "#666666"),
            getString(data, "quest_main", "#ffd700"),
            getString(data, "quest_side", "#87ceeb"),
            getString(data, "quest_timed", "#ff6b6b"),
            getString(data, "quest_guild", "#90ee90"),
            getString(data, "quest_daily", "#ffa500"),
            getString(data, "quest_weekly", "#9370db"),
            getString(data, "progress_fill", "#4caf50"),
            getString(data, "progress_background", "#333333"),
            getString(data, "progress_complete", "#8bc34a"),
            getString(data, "timer_normal", "#ffffff"),
            getString(data, "timer_warning", "#ffeb3b"),
            getString(data, "timer_critical", "#f44336")
        );
    }
    
    private FontConfig parseFonts(Map<String, Object> data) {
        if (data == null) {
            return FontConfig.DEFAULT;
        }
        
        return new FontConfig(
            getString(data, "title", "minecraft"),
            getString(data, "body", "minecraft"),
            getInt(data, "size_title", 14),
            getInt(data, "size_body", 12),
            getInt(data, "size_small", 10)
        );
    }
    
    private IconConfig parseIcons(Map<String, Object> data) {
        if (data == null) {
            return IconConfig.DEFAULT;
        }
        
        return new IconConfig(
            getString(data, "quest_main", IconConfig.DEFAULT.questMain()),
            getString(data, "quest_side", IconConfig.DEFAULT.questSide()),
            getString(data, "quest_timed", IconConfig.DEFAULT.questTimed()),
            getString(data, "quest_guild", IconConfig.DEFAULT.questGuild()),
            getString(data, "objective_complete", IconConfig.DEFAULT.objectiveComplete()),
            getString(data, "objective_incomplete", IconConfig.DEFAULT.objectiveIncomplete()),
            getString(data, "waypoint", IconConfig.DEFAULT.waypoint()),
            getString(data, "timer", IconConfig.DEFAULT.timer()),
            getString(data, "pinned", IconConfig.DEFAULT.pinned()),
            getString(data, "settings", IconConfig.DEFAULT.settings()),
            getString(data, "collapse", IconConfig.DEFAULT.collapse())
        );
    }
    
    private BorderConfig parseBorders(Map<String, Object> data) {
        if (data == null) {
            return BorderConfig.ROUNDED;
        }
        
        String styleStr = getString(data, "style", "rounded");
        BorderConfig.BorderStyle style = switch (styleStr.toLowerCase()) {
            case "none" -> BorderConfig.BorderStyle.NONE;
            case "solid" -> BorderConfig.BorderStyle.SOLID;
            case "fancy" -> BorderConfig.BorderStyle.FANCY;
            default -> BorderConfig.BorderStyle.ROUNDED;
        };
        
        return new BorderConfig(
            style,
            getInt(data, "radius", 4),
            getInt(data, "width", 1)
        );
    }
    
    private String getString(Map<String, Object> data, String key, String defaultValue) {
        Object value = data.get(key);
        return value != null ? value.toString() : defaultValue;
    }
    
    private int getInt(Map<String, Object> data, String key, int defaultValue) {
        Object value = data.get(key);
        if (value instanceof Number num) {
            return num.intValue();
        }
        return defaultValue;
    }
    
    private double getDouble(Map<String, Object> data, String key, double defaultValue) {
        Object value = data.get(key);
        if (value instanceof Number num) {
            return num.doubleValue();
        }
        return defaultValue;
    }
    
    private boolean getBoolean(Map<String, Object> data, String key, boolean defaultValue) {
        Object value = data.get(key);
        if (value instanceof Boolean bool) {
            return bool;
        }
        return defaultValue;
    }
    
    /**
     * Exception thrown when a theme cannot be parsed.
     */
    public static class ThemeParseException extends Exception {
        public ThemeParseException(String message) {
            super(message);
        }
        
        public ThemeParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
