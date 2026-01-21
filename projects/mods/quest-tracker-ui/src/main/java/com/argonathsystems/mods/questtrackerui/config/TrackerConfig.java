package com.argonathsystems.mods.questtrackerui.config;

/**
 * Complete tracker configuration.
 * Maps to src/main/resources/quest-tracker-ui.yml
 */
public record TrackerConfig(
    PositionConfig position,
    SizeConfig size,
    DisplayConfig display,
    CollapseConfig collapse,
    AnimationConfig animations,
    FilteringConfig filtering,
    ImmersiveModeConfig immersiveMode,
    IndicatorsConfig indicators,
    AccessibilityConfig accessibility
) {
    
    /**
     * Default tracker configuration.
     */
    public static final TrackerConfig DEFAULT = new TrackerConfig(
        PositionConfig.DEFAULT,
        SizeConfig.DEFAULT,
        DisplayConfig.DEFAULT,
        CollapseConfig.DEFAULT,
        AnimationConfig.DEFAULT,
        FilteringConfig.DEFAULT,
        ImmersiveModeConfig.DEFAULT,
        IndicatorsConfig.DEFAULT,
        AccessibilityConfig.DEFAULT
    );
    
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
        private FilteringConfig filtering = FilteringConfig.DEFAULT;
        private ImmersiveModeConfig immersiveMode = ImmersiveModeConfig.DEFAULT;
        private IndicatorsConfig indicators = IndicatorsConfig.DEFAULT;
        private AccessibilityConfig accessibility = AccessibilityConfig.DEFAULT;
        
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

        public Builder filtering(FilteringConfig filtering) {
            this.filtering = filtering;
            return this;
        }

        public Builder immersiveMode(ImmersiveModeConfig immersiveMode) {
            this.immersiveMode = immersiveMode;
            return this;
        }

        public Builder indicators(IndicatorsConfig indicators) {
            this.indicators = indicators;
            return this;
        }

        public Builder accessibility(AccessibilityConfig accessibility) {
            this.accessibility = accessibility;
            return this;
        }
        
        public TrackerConfig build() {
            return new TrackerConfig(
                position, size, display, collapse, animations,
                filtering, immersiveMode, indicators, accessibility
            );
        }
    }
}
