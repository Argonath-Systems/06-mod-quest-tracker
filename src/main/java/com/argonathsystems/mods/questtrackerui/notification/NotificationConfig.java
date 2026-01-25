package com.argonathsystems.mods.questtrackerui.notification;

import java.util.List;

/**
 * Configuration for notifications.
 *
 * @param enabled Whether notifications are enabled
 * @param position Position on screen
 * @param questAccepted Whether to show quest accepted notifications
 * @param questCompleted Whether to show quest completed notifications
 * @param objectiveCompleted Whether to show objective completed notifications
 * @param timerWarnings List of seconds at which to show timer warnings
 * @param sounds Whether to play sounds
 */
public record NotificationConfig(
    boolean enabled,
    NotificationPosition position,
    boolean questAccepted,
    boolean questCompleted,
    boolean objectiveCompleted,
    List<Integer> timerWarnings,
    boolean sounds
) {
    
    /**
     * Default notification configuration.
     */
    public static final NotificationConfig DEFAULT = new NotificationConfig(
        true,
        NotificationPosition.TOP_CENTER,
        true,
        true,
        true,
        List.of(300, 60, 30),
        true
    );
    
    /**
     * Notifications disabled.
     */
    public static final NotificationConfig DISABLED = new NotificationConfig(
        false,
        NotificationPosition.TOP_CENTER,
        false,
        false,
        false,
        List.of(),
        false
    );
    
    /**
     * Screen position for notifications.
     */
    public enum NotificationPosition {
        TOP_LEFT,
        TOP_CENTER,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_CENTER,
        BOTTOM_RIGHT
    }
}
