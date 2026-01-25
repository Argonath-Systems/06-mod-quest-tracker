package com.argonathsystems.mods.questtrackerui.config;

public record ImmersiveModeConfig(
    boolean enabled,
    boolean hideInCombat,
    boolean hideInDialogue,
    boolean hideOnMount,
    double opacityIdle,
    double opacityActive
) {
    public static final ImmersiveModeConfig DEFAULT = new ImmersiveModeConfig(
        true, false, true, false, 0.8, 1.0
    );
}
