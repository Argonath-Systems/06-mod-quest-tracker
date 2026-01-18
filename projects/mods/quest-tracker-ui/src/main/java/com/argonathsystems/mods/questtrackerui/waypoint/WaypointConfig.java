package com.argonathsystems.mods.questtrackerui.waypoint;

/**
 * Configuration for waypoint display.
 *
 * @param enabled Whether waypoints are enabled
 * @param showDistance Whether to show distance text
 * @param showOnScreen Whether to show waypoints on compass/screen edge
 * @param showWorldMarkers Whether to show 3D world markers
 * @param maxWaypoints Maximum number of waypoints to track
 * @param maxDistance Maximum distance to render waypoints
 * @param fadeStartPercent Percentage of max distance at which waypoints start fading (0.0 to 1.0)
 */
public record WaypointConfig(
    boolean enabled,
    boolean showDistance,
    boolean showOnScreen,
    boolean showWorldMarkers,
    int maxWaypoints,
    double maxDistance,
    double fadeStartPercent
) {
    
    /**
     * Default waypoint configuration.
     */
    public static final WaypointConfig DEFAULT = new WaypointConfig(
        true, true, true, true, 5, 500.0, 0.7
    );
    
    /**
     * Compact style (no distance text, fewer waypoints).
     */
    public static final WaypointConfig COMPACT = new WaypointConfig(
        true, false, true, true, 3, 300.0, 0.8
    );
    
    /**
     * Waypoints disabled.
     */
    public static final WaypointConfig DISABLED = new WaypointConfig(
        false, false, false, false, 0, 0.0, 0.0
    );
    
    /**
     * Calculate the opacity for a waypoint at the given distance.
     *
     * @param distance Distance to the waypoint
     * @return Opacity from 0.0 to 1.0
     */
    public double calculateOpacity(double distance) {
        if (!enabled || distance > maxDistance) {
            return 0.0;
        }
        
        double fadeStart = maxDistance * fadeStartPercent;
        
        if (distance <= fadeStart) {
            return 1.0;
        }
        
        // Linear fade from fadeStart to maxDistance
        double fadeRange = maxDistance - fadeStart;
        double fadeProgress = (distance - fadeStart) / fadeRange;
        return Math.max(0.0, Math.min(1.0, 1.0 - fadeProgress));
    }
}
