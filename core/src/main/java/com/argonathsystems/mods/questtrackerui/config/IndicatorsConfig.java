package com.argonathsystems.mods.questtrackerui.config;

public record IndicatorsConfig(
    WorldMarkersConfig worldMarkers,
    CompassBarConfig compassBar,
    boolean minimapIntegration
) {
    public static final IndicatorsConfig DEFAULT = new IndicatorsConfig(
        WorldMarkersConfig.DEFAULT,
        CompassBarConfig.DEFAULT,
        true
    );

    public record WorldMarkersConfig(
        boolean enabled,
        boolean showDistance,
        int maxDistance,
        boolean clampToScreenEdge
    ) {
        public static final WorldMarkersConfig DEFAULT = new WorldMarkersConfig(true, true, 2000, true);
    }

    public record CompassBarConfig(
        boolean showQuestIcons
    ) {
        public static final CompassBarConfig DEFAULT = new CompassBarConfig(true);
    }
}
