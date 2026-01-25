package com.argonathsystems.mods.questtrackerui.config;

import com.argonathsystems.mods.questtrackerui.hud.AnchorPosition;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Loads and saves tracker configuration from YAML files.
 */
public class ConfigLoader {
    
    private final Yaml yaml;
    
    /**
     * Create a new config loader.
     */
    public ConfigLoader() {
        this.yaml = new Yaml();
    }
    
    /**
     * Load configuration from a file.
     *
     * @param path Path to the configuration file
     * @return Loaded configuration
     * @throws IOException If the file cannot be read
     */
    public TrackerConfig loadFromFile(Path path) throws IOException {
        if (!Files.exists(path)) {
            return TrackerConfig.DEFAULT;
        }
        
        try (Reader reader = Files.newBufferedReader(path)) {
            Map<String, Object> data = yaml.load(reader);
            return parseConfig(data);
        }
    }
    
    /**
     * Save configuration to a file.
     *
     * @param config Configuration to save
     * @param path Path to save to
     * @throws IOException If the file cannot be written
     */
    public void saveToFile(TrackerConfig config, Path path) throws IOException {
        Map<String, Object> data = serializeConfig(config);
        
        Files.createDirectories(path.getParent());
        try (Writer writer = Files.newBufferedWriter(path)) {
            yaml.dump(data, writer);
        }
    }
    
    @SuppressWarnings("unchecked")
    private TrackerConfig parseConfig(Map<String, Object> data) {
        if (data == null) {
            return TrackerConfig.DEFAULT;
        }
        
        Map<String, Object> trackerData = (Map<String, Object>) data.get("tracker");
        if (trackerData == null) {
            trackerData = data;
        }
        
        return TrackerConfig.builder()
            .position(parsePosition((Map<String, Object>) trackerData.get("position")))
            .size(parseSize((Map<String, Object>) trackerData.get("size")))
            .display(parseDisplay((Map<String, Object>) trackerData.get("display")))
            .collapse(parseCollapse((Map<String, Object>) trackerData.get("collapse")))
            .animations(parseAnimations((Map<String, Object>) trackerData.get("animations")))
            .build();
    }
    
    private PositionConfig parsePosition(Map<String, Object> data) {
        if (data == null) {
            return PositionConfig.DEFAULT;
        }
        
        return new PositionConfig(
            AnchorPosition.parse(getString(data, "anchor", "TOP_RIGHT")),
            getInt(data, "offset_x", -20),
            getInt(data, "offset_y", 50)
        );
    }
    
    private SizeConfig parseSize(Map<String, Object> data) {
        if (data == null) {
            return SizeConfig.DEFAULT;
        }
        
        return new SizeConfig(
            getInt(data, "width", 280),
            getInt(data, "max_height", 400),
            getDouble(data, "scale", 1.0)
        );
    }
    
    private DisplayConfig parseDisplay(Map<String, Object> data) {
        if (data == null) {
            return DisplayConfig.DEFAULT;
        }
        
        return new DisplayConfig(
            getInt(data, "max_pinned_quests", 3),
            getInt(data, "max_visible_objectives", 8),
            getBoolean(data, "show_completed_objectives", true),
            getInt(data, "completed_fade_seconds", 3),
            getBoolean(data, "show_progress_bars", true),
            getBoolean(data, "show_progress_text", true),
            getBoolean(data, "show_distance", true),
            getBoolean(data, "show_timer", true)
        );
    }
    
    private CollapseConfig parseCollapse(Map<String, Object> data) {
        if (data == null) {
            return CollapseConfig.DEFAULT;
        }
        
        return new CollapseConfig(
            getBoolean(data, "enabled", true),
            getBoolean(data, "default_collapsed", false),
            getBoolean(data, "collapse_completed", true),
            getBoolean(data, "collapse_during_combat", false)
        );
    }
    
    private AnimationConfig parseAnimations(Map<String, Object> data) {
        if (data == null) {
            return AnimationConfig.DEFAULT;
        }
        
        return new AnimationConfig(
            getBoolean(data, "enabled", true),
            getBoolean(data, "objective_complete_flash", true),
            getBoolean(data, "progress_update_pulse", true),
            getBoolean(data, "new_quest_slide_in", true),
            getInt(data, "duration_ms", 300)
        );
    }
    
    private Map<String, Object> serializeConfig(TrackerConfig config) {
        Map<String, Object> root = new LinkedHashMap<>();
        Map<String, Object> tracker = new LinkedHashMap<>();
        
        // Position
        Map<String, Object> position = new LinkedHashMap<>();
        position.put("anchor", config.position().anchor().name());
        position.put("offset_x", config.position().offsetX());
        position.put("offset_y", config.position().offsetY());
        tracker.put("position", position);
        
        // Size
        Map<String, Object> size = new LinkedHashMap<>();
        size.put("width", config.size().width());
        size.put("max_height", config.size().maxHeight());
        size.put("scale", config.size().scale());
        tracker.put("size", size);
        
        // Display
        Map<String, Object> display = new LinkedHashMap<>();
        display.put("max_pinned_quests", config.display().maxPinnedQuests());
        display.put("max_visible_objectives", config.display().maxVisibleObjectives());
        display.put("show_completed_objectives", config.display().showCompletedObjectives());
        display.put("completed_fade_seconds", config.display().completedFadeSeconds());
        display.put("show_progress_bars", config.display().showProgressBars());
        display.put("show_progress_text", config.display().showProgressText());
        display.put("show_distance", config.display().showDistance());
        display.put("show_timer", config.display().showTimer());
        tracker.put("display", display);
        
        // Collapse
        Map<String, Object> collapse = new LinkedHashMap<>();
        collapse.put("enabled", config.collapse().enabled());
        collapse.put("default_collapsed", config.collapse().defaultCollapsed());
        collapse.put("collapse_completed", config.collapse().collapseCompleted());
        collapse.put("collapse_during_combat", config.collapse().collapseDuringCombat());
        tracker.put("collapse", collapse);
        
        // Animations
        Map<String, Object> animations = new LinkedHashMap<>();
        animations.put("enabled", config.animations().enabled());
        animations.put("objective_complete_flash", config.animations().objectiveCompleteFlash());
        animations.put("progress_update_pulse", config.animations().progressUpdatePulse());
        animations.put("new_quest_slide_in", config.animations().newQuestSlideIn());
        animations.put("duration_ms", config.animations().durationMs());
        tracker.put("animations", animations);
        
        root.put("tracker", tracker);
        return root;
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
}
