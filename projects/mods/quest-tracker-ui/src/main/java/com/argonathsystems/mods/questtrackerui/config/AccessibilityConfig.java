package com.argonathsystems.mods.questtrackerui.config;

public record AccessibilityConfig(
    String colorblindMode,
    double textScaling,
    boolean highContrastBackground,
    boolean screenReaderSupport
) {
    public static final AccessibilityConfig DEFAULT = new AccessibilityConfig(
        "tritanopia", 1.0, false, false
    );
}
