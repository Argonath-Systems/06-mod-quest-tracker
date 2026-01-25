package com.argonathsystems.mods.questtrackerui.config;

/**
 * Animation configuration.
 *
 * @param enabled Whether animations are enabled
 * @param objectiveCompleteFlash Whether to flash on objective complete
 * @param progressUpdatePulse Whether to pulse on progress update
 * @param newQuestSlideIn Whether new quests slide in
 * @param durationMs Animation duration in milliseconds
 */
public record AnimationConfig(
    boolean enabled,
    boolean objectiveCompleteFlash,
    boolean progressUpdatePulse,
    boolean newQuestSlideIn,
    int durationMs
) {
    
    /**
     * Default animation configuration.
     */
    public static final AnimationConfig DEFAULT = new AnimationConfig(
        true, true, true, true, 300
    );
    
    /**
     * Animations disabled.
     */
    public static final AnimationConfig DISABLED = new AnimationConfig(
        false, false, false, false, 0
    );
}
