package com.argonathsystems.mods.questtrackerui.api;

import com.argonathsystems.framework.accessorapi.dto.LocationData;

/**
 * Represents a waypoint for a quest objective.
 *
 * @param id Unique identifier for the waypoint
 * @param position World position of the waypoint
 * @param label Display label (shown on hover)
 * @param style Visual style of the waypoint marker
 * @param showDistance Whether to show distance from player
 * @param showCompass Whether to show on compass/screen edge
 * @param showWorldMarker Whether to show 3D marker in world
 */
public record QuestWaypoint(
    String id,
    LocationData position,
    String label,
    WaypointStyle style,
    boolean showDistance,
    boolean showCompass,
    boolean showWorldMarker
) {
    
    /**
     * Create a waypoint with default display settings.
     *
     * @param id Unique identifier
     * @param position World position
     * @param label Display label
     * @return Waypoint with all display options enabled
     */
    public static QuestWaypoint of(String id, LocationData position, String label) {
        return new QuestWaypoint(id, position, label, WaypointStyle.DEFAULT, true, true, true);
    }
    
    /**
     * Create a waypoint with a specific style.
     *
     * @param id Unique identifier
     * @param position World position
     * @param label Display label
     * @param style Visual style
     * @return Waypoint with specified style and all display options enabled
     */
    public static QuestWaypoint of(String id, LocationData position, String label, WaypointStyle style) {
        return new QuestWaypoint(id, position, label, style, true, true, true);
    }
    
    /**
     * Create a copy with distance display toggled.
     *
     * @param show Whether to show distance
     * @return New waypoint with updated setting
     */
    public QuestWaypoint withShowDistance(boolean show) {
        return new QuestWaypoint(id, position, label, style, show, showCompass, showWorldMarker);
    }
    
    /**
     * Create a copy with compass display toggled.
     *
     * @param show Whether to show on compass
     * @return New waypoint with updated setting
     */
    public QuestWaypoint withShowCompass(boolean show) {
        return new QuestWaypoint(id, position, label, style, showDistance, show, showWorldMarker);
    }
    
    /**
     * Create a copy with world marker toggled.
     *
     * @param show Whether to show world marker
     * @return New waypoint with updated setting
     */
    public QuestWaypoint withShowWorldMarker(boolean show) {
        return new QuestWaypoint(id, position, label, style, showDistance, showCompass, show);
    }
}
