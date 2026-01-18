package com.argonathsystems.mods.questtrackerui.waypoint;

/**
 * Configuration for waypoint display.
 *
 * @param enabled Whether waypoints are enabled
 * @param showCompass Whether to show waypoints on compass/screen edge
 * @param showWorldMarkers Whether to show 3D world markers
 * @param showDistance Whether to show distance text
 * @param maxRenderDistance Maximum distance to render waypoints
 * @param fadeStartDistance Distance at which waypoints start fading
 */
public record WaypointConfig(
    boolean enabled,
    boolean showCompass,
    boolean showWorldMarkers,
    boolean showDistance,
    int maxRenderDistance,
    int fadeStartDistance
) {
    
    /**
     * Default waypoint configuration.
     */
    public static final WaypointConfig DEFAULT = new WaypointConfig(
        true, true, true, true, 1000, 800
    );
    
    /**
     * Waypoints disabled.
     */
    public static final WaypointConfig DISABLED = new WaypointConfig(
        false, false, false, false, 0, 0
    );
    
    /**
     * Calculate the opacity for a waypoint at the given distance.
     *
     * @param distance Distance to the waypoint
     * @return Opacity from 0.0 to 1.0
     */
    public double calculateOpacity(double distance) {
        if (!enabled || distance > maxRenderDistance) {
            return 0.0;
        }
        if (distance <= fadeStartDistance) {
            return 1.0;
        }
        // Linear fade from fadeStartDistance to maxRenderDistance
        double fadeRange = maxRenderDistance - fadeStartDistance;
        double fadeProgress = (distance - fadeStartDistance) / fadeRange;
        return 1.0 - fadeProgress;
    }
}
