package com.argonathsystems.mods.questtrackerui.config;

/**
 * Complete tracker configuration.
 *
 * @param position Position configuration
 * @param size Size configuration
 * @param display Display configuration
 * @param collapse Collapse configuration
 * @param animations Animation configuration
 */
public record TrackerConfig(
    PositionConfig position,
    SizeConfig size,
    DisplayConfig display,
    CollapseConfig collapse,
    AnimationConfig animations
) {
    
    /**
     * Default tracker configuration.
     */
    public static final TrackerConfig DEFAULT = new TrackerConfig(
        PositionConfig.DEFAULT,
        SizeConfig.DEFAULT,
        DisplayConfig.DEFAULT,
        CollapseConfig.DEFAULT,
        AnimationConfig.DEFAULT
    );
    
    /**
     * Create a builder starting from default configuration.
     *
     * @return Builder with default values
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Fluent builder for TrackerConfig.
     */
    public static class Builder {
        private PositionConfig position = PositionConfig.DEFAULT;
        private SizeConfig size = SizeConfig.DEFAULT;
        private DisplayConfig display = DisplayConfig.DEFAULT;
        private CollapseConfig collapse = CollapseConfig.DEFAULT;
        private AnimationConfig animations = AnimationConfig.DEFAULT;
        
        public Builder position(PositionConfig position) {
            this.position = position;
            return this;
        }
        
        public Builder size(SizeConfig size) {
            this.size = size;
            return this;
        }
        
        public Builder display(DisplayConfig display) {
            this.display = display;
            return this;
        }
        
        public Builder collapse(CollapseConfig collapse) {
            this.collapse = collapse;
            return this;
        }
        
        public Builder animations(AnimationConfig animations) {
            this.animations = animations;
            return this;
        }
        
        public TrackerConfig build() {
            return new TrackerConfig(position, size, display, collapse, animations);
        }
    }
}
