package com.argonathsystems.mods.questtrackerui.theme;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Registry for managing available themes.
 */
public class ThemeRegistry {
    
    private final Map<String, Theme> themes;
    private final ThemeLoader loader;
    private Theme activeTheme;
    private final List<ThemeChangeListener> listeners;
    
    /**
     * Create a new theme registry with default themes.
     */
    public ThemeRegistry() {
        this.themes = new ConcurrentHashMap<>();
        this.loader = new ThemeLoader();
        this.listeners = new ArrayList<>();
        
        // Register default themes
        register(Theme.DARK_FANTASY);
        register(Theme.LIGHT_MINIMAL);
        
        // Set default active theme
        this.activeTheme = Theme.DARK_FANTASY;
    }
    
    /**
     * Register a theme.
     *
     * @param theme Theme to register
     */
    public void register(Theme theme) {
        themes.put(theme.id(), theme);
    }
    
    /**
     * Unregister a theme.
     *
     * @param themeId Theme ID to remove
     * @return true if the theme was removed
     */
    public boolean unregister(String themeId) {
        // Cannot unregister the active theme
        if (activeTheme != null && activeTheme.id().equals(themeId)) {
            return false;
        }
        return themes.remove(themeId) != null;
    }
    
    /**
     * Get a theme by ID.
     *
     * @param themeId Theme ID
     * @return Optional containing the theme if found
     */
    public Optional<Theme> get(String themeId) {
        return Optional.ofNullable(themes.get(themeId));
    }
    
    /**
     * Get all registered themes.
     *
     * @return Unmodifiable collection of themes
     */
    public Collection<Theme> getAll() {
        return Collections.unmodifiableCollection(themes.values());
    }
    
    /**
     * Get all free (non-premium) themes.
     *
     * @return List of free themes
     */
    public List<Theme> getFreeThemes() {
        return themes.values().stream()
            .filter(t -> !t.isPremium())
            .toList();
    }
    
    /**
     * Get all premium themes.
     *
     * @return List of premium themes
     */
    public List<Theme> getPremiumThemes() {
        return themes.values().stream()
            .filter(Theme::isPremium)
            .toList();
    }
    
    /**
     * Get the currently active theme.
     *
     * @return Active theme
     */
    public Theme getActiveTheme() {
        return activeTheme;
    }
    
    /**
     * Set the active theme by ID.
     *
     * @param themeId Theme ID to activate
     * @return true if the theme was activated
     */
    public boolean setActiveTheme(String themeId) {
        Theme theme = themes.get(themeId);
        if (theme != null) {
            Theme oldTheme = this.activeTheme;
            this.activeTheme = theme;
            notifyListeners(oldTheme, theme);
            return true;
        }
        return false;
    }
    
    /**
     * Load themes from a directory.
     *
     * @param themesDirectory Directory containing theme YAML files
     * @return Number of themes loaded
     * @throws IOException If the directory cannot be read
     */
    public int loadFromDirectory(Path themesDirectory) throws IOException {
        if (!Files.isDirectory(themesDirectory)) {
            return 0;
        }
        
        int loaded = 0;
        try (Stream<Path> files = Files.list(themesDirectory)) {
            for (Path file : files.toList()) {
                if (isThemeFile(file)) {
                    try {
                        Theme theme = loader.loadFromFile(file);
                        register(theme);
                        loaded++;
                    } catch (ThemeLoader.ThemeParseException e) {
                        // Log and continue
                        System.err.println("Failed to load theme " + file + ": " + e.getMessage());
                    }
                }
            }
        }
        
        return loaded;
    }
    
    /**
     * Reload all themes from a directory.
     * 
     * <p>Preserves built-in themes and active theme if still available.
     *
     * @param themesDirectory Directory containing theme YAML files
     * @return Number of themes loaded
     * @throws IOException If the directory cannot be read
     */
    public int reloadFromDirectory(Path themesDirectory) throws IOException {
        String activeId = activeTheme != null ? activeTheme.id() : Theme.DARK_FANTASY.id();
        
        // Clear custom themes but keep built-ins
        themes.clear();
        register(Theme.DARK_FANTASY);
        register(Theme.LIGHT_MINIMAL);
        
        int loaded = loadFromDirectory(themesDirectory);
        
        // Restore active theme or fall back to default
        if (!setActiveTheme(activeId)) {
            setActiveTheme(Theme.DARK_FANTASY.id());
        }
        
        return loaded;
    }
    
    private boolean isThemeFile(Path file) {
        String name = file.getFileName().toString().toLowerCase();
        return (name.endsWith(".yml") || name.endsWith(".yaml")) && Files.isRegularFile(file);
    }
    
    /**
     * Add a listener for theme changes.
     *
     * @param listener Listener to add
     */
    public void addChangeListener(ThemeChangeListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Remove a theme change listener.
     *
     * @param listener Listener to remove
     */
    public void removeChangeListener(ThemeChangeListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyListeners(Theme oldTheme, Theme newTheme) {
        for (ThemeChangeListener listener : listeners) {
            try {
                listener.onThemeChanged(oldTheme, newTheme);
            } catch (Exception e) {
                // Log and continue
                System.err.println("Theme change listener error: " + e.getMessage());
            }
        }
    }
    
    /**
     * Listener for theme change events.
     */
    @FunctionalInterface
    public interface ThemeChangeListener {
        /**
         * Called when the active theme changes.
         *
         * @param oldTheme Previous theme (may be null)
         * @param newTheme New active theme
         */
        void onThemeChanged(Theme oldTheme, Theme newTheme);
    }
}
