package com.argonathsystems.mods.questtrackerui.api;

import com.argonathsystems.framework.accessorapi.dto.LocationData;

import java.util.Locale;

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
     * Create a styled waypoint (alias for of).
     *
     * @param id Unique identifier
     * @param position World position
     * @param label Display label
     * @param style Visual style
     * @return Waypoint with specified style
     */
    public static QuestWaypoint styled(String id, LocationData position, String label, WaypointStyle style) {
        return of(id, position, label, style);
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

    /**
     * Calculate distance from this waypoint to another location.
     *
     * @param other Target location
     * @return Euclidean distance in blocks/meters
     */
    public double distanceFrom(LocationData other) {
        double dx = position.x() - other.x();
        double dy = position.y() - other.y();
        double dz = position.z() - other.z();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Format a distance value into a short string (e.g. "120m", "1.5km").
     *
     * @param distance Distance in meters
     * @return Formatted string
     */
    public static String formatDistance(double distance) {
        if (distance >= 1000) {
            return String.format(Locale.ROOT, "%.1fkm", distance / 1000.0);
        } else {
            return String.format(Locale.ROOT, "%.0fm", distance);
        }
    }

    /**
     * @deprecated Use static {@link #formatDistance(double)} instead.
     */
    @Deprecated
    public String formatDistance() {
        return "Unknown";
    }
}
