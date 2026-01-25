package com.argonathsystems.mods.questtrackerui.config;

import java.util.List;

public record FilteringConfig(
    boolean autoPinNew,
    boolean autoUnpinCompleted,
    List<String> prioritySorting
) {
    public static final FilteringConfig DEFAULT = new FilteringConfig(
        true,
        true,
        List.of("MAIN_STORY", "TIMED", "SIDE", "DISTANCE")
    );
}
