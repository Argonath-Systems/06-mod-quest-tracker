package com.argonathsystems.mods.questtrackerui.accessibility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Manager for accessibility settings with change notification support.
 *
 * <p>Provides centralized access to accessibility settings and notifies
 * listeners when settings change. Integrates with the configuration system.
 *
 * <p>Example usage:
 * <pre>{@code
 * AccessibilityManager manager = AccessibilityManager.getInstance();
 * 
 * // Add listener for changes
 * manager.addChangeListener(settings -> updateUI(settings));
 * 
 * // Get current settings
 * AccessibilitySettings settings = manager.getSettings();
 * 
 * // Update settings
 * manager.setHighContrastMode(true);
 * manager.setTextScale(1.5);
 * }</pre>
 *
 * @since 1.0.0
 */
public final class AccessibilityManager {
    
    private static final AccessibilityManager INSTANCE = new AccessibilityManager();
    
    private volatile AccessibilitySettings settings;
    private final List<Consumer<AccessibilitySettings>> changeListeners;
    
    private AccessibilityManager() {
        this.settings = AccessibilitySettings.defaults();
        this.changeListeners = new CopyOnWriteArrayList<>();
    }
    
    /**
     * Get the singleton instance.
     *
     * @return AccessibilityManager instance
     */
    public static AccessibilityManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * Get current accessibility settings.
     *
     * @return Current settings (immutable)
     */
    public AccessibilitySettings getSettings() {
        return settings;
    }
    
    /**
     * Update settings and notify listeners.
     *
     * @param newSettings New settings
     */
    public void setSettings(AccessibilitySettings newSettings) {
        if (newSettings == null) {
            newSettings = AccessibilitySettings.defaults();
        }
        
        if (!newSettings.equals(this.settings)) {
            this.settings = newSettings;
            notifyListeners();
        }
    }
    
    /**
     * Set high contrast mode.
     *
     * @param enabled true to enable
     */
    public void setHighContrastMode(boolean enabled) {
        if (settings.isHighContrastMode() != enabled) {
            setSettings(settings.toBuilder()
                .highContrastMode(enabled)
                .build());
        }
    }
    
    /**
     * Toggle high contrast mode.
     */
    public void toggleHighContrastMode() {
        setHighContrastMode(!settings.isHighContrastMode());
    }
    
    /**
     * Set colorblind mode.
     *
     * @param mode Colorblind mode
     */
    public void setColorblindMode(ColorblindMode mode) {
        if (mode == null) {
            mode = ColorblindMode.NONE;
        }
        if (settings.colorblindMode() != mode) {
            setSettings(settings.toBuilder()
                .colorblindMode(mode)
                .build());
        }
    }
    
    /**
     * Cycle through colorblind modes.
     */
    public void cycleColorblindMode() {
        ColorblindMode[] modes = ColorblindMode.values();
        int currentIndex = settings.colorblindMode().ordinal();
        int nextIndex = (currentIndex + 1) % modes.length;
        setColorblindMode(modes[nextIndex]);
    }
    
    /**
     * Set screen reader support.
     *
     * @param enabled true to enable
     */
    public void setScreenReaderSupport(boolean enabled) {
        if (settings.isScreenReaderSupport() != enabled) {
            setSettings(settings.toBuilder()
                .screenReaderSupport(enabled)
                .build());
        }
    }
    
    /**
     * Set text scale.
     *
     * @param scale Scale factor (0.5 to 2.0)
     */
    public void setTextScale(double scale) {
        double clamped = TextScaling.clampScale(scale);
        if (Double.compare(settings.textScale(), clamped) != 0) {
            setSettings(settings.toBuilder()
                .textScale(clamped)
                .build());
        }
    }
    
    /**
     * Increase text scale by one increment.
     */
    public void increaseTextScale() {
        setTextScale(TextScaling.increaseScale(settings.textScale()));
    }
    
    /**
     * Decrease text scale by one increment.
     */
    public void decreaseTextScale() {
        setTextScale(TextScaling.decreaseScale(settings.textScale()));
    }
    
    /**
     * Reset text scale to default.
     */
    public void resetTextScale() {
        setTextScale(TextScaling.DEFAULT_SCALE);
    }
    
    /**
     * Reset all settings to defaults.
     */
    public void resetToDefaults() {
        setSettings(AccessibilitySettings.defaults());
    }
    
    /**
     * Add a listener for settings changes.
     *
     * @param listener Listener to add
     */
    public void addChangeListener(Consumer<AccessibilitySettings> listener) {
        if (listener != null) {
            changeListeners.add(listener);
        }
    }
    
    /**
     * Remove a change listener.
     *
     * @param listener Listener to remove
     */
    public void removeChangeListener(Consumer<AccessibilitySettings> listener) {
        changeListeners.remove(listener);
    }
    
    /**
     * Load settings from configuration map.
     *
     * @param config Configuration map
     */
    public void loadFromConfig(Map<String, Object> config) {
        if (config == null) {
            return;
        }
        
        AccessibilitySettings.Builder builder = AccessibilitySettings.builder();
        
        Object highContrast = config.get("high_contrast_mode");
        if (highContrast instanceof Boolean b) {
            builder.highContrastMode(b);
        }
        
        Object colorblind = config.get("colorblind_mode");
        if (colorblind instanceof String s) {
            builder.colorblindMode(ColorblindMode.fromString(s));
        }
        
        Object screenReader = config.get("screen_reader_support");
        if (screenReader instanceof Boolean b) {
            builder.screenReaderSupport(b);
        }
        
        Object textScale = config.get("text_scale");
        if (textScale instanceof Number n) {
            builder.textScale(n.doubleValue());
        }
        
        setSettings(builder.build());
    }
    
    /**
     * Export settings to configuration map.
     *
     * @return Configuration map
     */
    public Map<String, Object> toConfigMap() {
        return Map.of(
            "high_contrast_mode", settings.isHighContrastMode(),
            "colorblind_mode", settings.colorblindMode().name().toLowerCase(),
            "screen_reader_support", settings.isScreenReaderSupport(),
            "text_scale", settings.textScale()
        );
    }
    
    private void notifyListeners() {
        AccessibilitySettings current = settings;
        for (Consumer<AccessibilitySettings> listener : changeListeners) {
            try {
                listener.accept(current);
            } catch (Exception e) {
                // Log and continue
                System.err.println("Error notifying accessibility listener: " + e.getMessage());
            }
        }
    }
    
    /**
     * Get list of available colorblind modes for UI display.
     *
     * @return List of colorblind mode info
     */
    public List<ColorblindModeInfo> getColorblindModeOptions() {
        List<ColorblindModeInfo> options = new ArrayList<>();
        for (ColorblindMode mode : ColorblindMode.values()) {
            options.add(new ColorblindModeInfo(
                mode, 
                mode.displayName(), 
                mode.description(),
                mode == settings.colorblindMode()
            ));
        }
        return options;
    }
    
    /**
     * Information about a colorblind mode option.
     *
     * @param mode The mode
     * @param displayName Display name for UI
     * @param description Description
     * @param selected Whether currently selected
     */
    public record ColorblindModeInfo(
        ColorblindMode mode,
        String displayName,
        String description,
        boolean selected
    ) {}
}
