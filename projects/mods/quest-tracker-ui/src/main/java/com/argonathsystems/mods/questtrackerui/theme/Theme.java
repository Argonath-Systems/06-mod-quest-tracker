package com.argonathsystems.mods.questtrackerui.theme;

/**
 * Complete theme definition for the Quest Tracker UI.
 *
 * @param id Unique theme identifier
 * @param name Display name
 * @param author Theme author
 * @param isPremium Whether this is a premium theme
 * @param colors Color scheme
 * @param fonts Font configuration
 * @param icons Icon configuration
 * @param borders Border configuration
 */
public record Theme(
    String id,
    String name,
    String author,
    boolean isPremium,
    ColorScheme colors,
    FontConfig fonts,
    IconConfig icons,
    BorderConfig borders
) {
    
    /**
     * Default dark fantasy theme.
     */
    public static final Theme DARK_FANTASY = new Theme(
        "dark-fantasy",
        "Dark Fantasy",
        "LordOfTheTales",
        false,
        ColorScheme.DARK_FANTASY,
        FontConfig.DEFAULT,
        IconConfig.DEFAULT,
        BorderConfig.ROUNDED
    );
    
    /**
     * Light minimal theme.
     */
    public static final Theme LIGHT_MINIMAL = new Theme(
        "light-minimal",
        "Light Minimal",
        "LordOfTheTales",
        false,
        ColorScheme.LIGHT_MINIMAL,
        FontConfig.DEFAULT,
        IconConfig.DEFAULT,
        BorderConfig.SOLID
    );
    
    /**
     * Create a theme builder starting from a base theme.
     *
     * @param base Base theme to copy from
     * @return Builder initialized with base theme values
     */
    public static Builder builder(Theme base) {
        return new Builder()
            .id(base.id)
            .name(base.name)
            .author(base.author)
            .isPremium(base.isPremium)
            .colors(base.colors)
            .fonts(base.fonts)
            .icons(base.icons)
            .borders(base.borders);
    }
    
    /**
     * Create a new theme builder.
     *
     * @return Empty builder
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Fluent builder for Theme.
     */
    public static class Builder {
        private String id = "custom";
        private String name = "Custom Theme";
        private String author = "Unknown";
        private boolean isPremium = false;
        private ColorScheme colors = ColorScheme.DARK_FANTASY;
        private FontConfig fonts = FontConfig.DEFAULT;
        private IconConfig icons = IconConfig.DEFAULT;
        private BorderConfig borders = BorderConfig.ROUNDED;
        
        public Builder id(String id) {
            this.id = id;
            return this;
        }
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder author(String author) {
            this.author = author;
            return this;
        }
        
        public Builder isPremium(boolean isPremium) {
            this.isPremium = isPremium;
            return this;
        }
        
        public Builder colors(ColorScheme colors) {
            this.colors = colors;
            return this;
        }
        
        public Builder fonts(FontConfig fonts) {
            this.fonts = fonts;
            return this;
        }
        
        public Builder icons(IconConfig icons) {
            this.icons = icons;
            return this;
        }
        
        public Builder borders(BorderConfig borders) {
            this.borders = borders;
            return this;
        }
        
        public Theme build() {
            return new Theme(id, name, author, isPremium, colors, fonts, icons, borders);
        }
    }
}
