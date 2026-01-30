package com.argonathsystems.mods.questtrackerui.config;

/**
 * Configuration for immersive/context-aware tracker behavior per QT-L2-003 specification.
 * 
 * <p>Controls automatic hiding of the quest tracker during specific game contexts
 * like combat or dialogue to reduce visual clutter and improve immersion.
 * 
 * <h2>Specification Reference (QT-L2-003)</h2>
 * <ul>
 *   <li>Combat Hide: Option to auto-hide tracker during combat</li>
 *   <li>Dialogue Hide: Auto-hide during NPC conversations</li>
 *   <li>Idle Opacity: Reduce opacity when not updating or hovered</li>
 * </ul>
 * 
 * @param enabled Whether immersive mode is enabled
 * @param hideInCombat Hide tracker when player enters combat
 * @param hideInDialogue Hide tracker during NPC dialogue
 * @param hideInCutscene Hide tracker during cutscenes
 * @param opacityIdle Opacity when tracker is idle (0.0-1.0)
 * @param opacityActive Opacity when tracker is active/updated (0.0-1.0)
 * @param idleTimeoutMs Time in ms before tracker is considered idle
 * 
 * @author Argonath Systems
 * @version 1.1.0
 * @since 1.1.0
 */
public record ImmersiveConfig(
    boolean enabled,
    boolean hideInCombat,
    boolean hideInDialogue,
    boolean hideInCutscene,
    double opacityIdle,
    double opacityActive,
    long idleTimeoutMs
) {
    
    /**
     * Default configuration with immersive features enabled.
     */
    public static final ImmersiveConfig DEFAULT = new ImmersiveConfig(
        true,       // enabled
        false,      // hideInCombat - disabled by default per spec
        true,       // hideInDialogue
        true,       // hideInCutscene
        0.8,        // opacityIdle
        1.0,        // opacityActive
        5000        // idleTimeoutMs (5 seconds)
    );
    
    /**
     * Configuration with all immersive features disabled.
     */
    public static final ImmersiveConfig DISABLED = new ImmersiveConfig(
        false, false, false, false, 1.0, 1.0, 0
    );
    
    /**
     * Validate configuration values.
     */
    public ImmersiveConfig {
        opacityIdle = Math.max(0.0, Math.min(1.0, opacityIdle));
        opacityActive = Math.max(0.0, Math.min(1.0, opacityActive));
        idleTimeoutMs = Math.max(0, idleTimeoutMs);
    }
    
    /**
     * Check if tracker should be hidden for current context.
     *
     * @param inCombat Whether player is in combat
     * @param inDialogue Whether player is in dialogue
     * @param inCutscene Whether a cutscene is playing
     * @return true if tracker should be hidden
     */
    public boolean shouldHide(boolean inCombat, boolean inDialogue, boolean inCutscene) {
        if (!enabled) {
            return false;
        }
        
        if (hideInCombat && inCombat) {
            return true;
        }
        
        if (hideInDialogue && inDialogue) {
            return true;
        }
        
        if (hideInCutscene && inCutscene) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Get the opacity for current state.
     *
     * @param isIdle Whether tracker is currently idle
     * @return Opacity value (0.0-1.0)
     */
    public double getOpacity(boolean isIdle) {
        if (!enabled) {
            return 1.0;
        }
        return isIdle ? opacityIdle : opacityActive;
    }
    
    /**
     * Builder for creating custom immersive configurations.
     */
    public static class Builder {
        private boolean enabled = DEFAULT.enabled();
        private boolean hideInCombat = DEFAULT.hideInCombat();
        private boolean hideInDialogue = DEFAULT.hideInDialogue();
        private boolean hideInCutscene = DEFAULT.hideInCutscene();
        private double opacityIdle = DEFAULT.opacityIdle();
        private double opacityActive = DEFAULT.opacityActive();
        private long idleTimeoutMs = DEFAULT.idleTimeoutMs();
        
        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }
        
        public Builder hideInCombat(boolean hide) {
            this.hideInCombat = hide;
            return this;
        }
        
        public Builder hideInDialogue(boolean hide) {
            this.hideInDialogue = hide;
            return this;
        }
        
        public Builder hideInCutscene(boolean hide) {
            this.hideInCutscene = hide;
            return this;
        }
        
        public Builder opacityIdle(double opacity) {
            this.opacityIdle = opacity;
            return this;
        }
        
        public Builder opacityActive(double opacity) {
            this.opacityActive = opacity;
            return this;
        }
        
        public Builder idleTimeoutMs(long timeout) {
            this.idleTimeoutMs = timeout;
            return this;
        }
        
        public ImmersiveConfig build() {
            return new ImmersiveConfig(
                enabled, hideInCombat, hideInDialogue, hideInCutscene,
                opacityIdle, opacityActive, idleTimeoutMs
            );
        }
    }
    
    /**
     * Create a new builder.
     *
     * @return Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Create a copy with immersive mode enabled/disabled.
     *
     * @param enabled New enabled state
     * @return New configuration
     */
    public ImmersiveConfig withEnabled(boolean enabled) {
        return new ImmersiveConfig(enabled, hideInCombat, hideInDialogue, hideInCutscene,
            opacityIdle, opacityActive, idleTimeoutMs);
    }
}
