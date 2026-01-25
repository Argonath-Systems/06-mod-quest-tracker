package com.argonathsystems.mods.questtrackerui.waypoint;

import com.argonathsystems.framework.accessorapi.dto.LocationData;
import com.argonathsystems.framework.text.Component;
import com.argonathsystems.framework.text.Style;
import com.argonathsystems.framework.text.TextColor;
import com.argonathsystems.mods.questtrackerui.api.QuestWaypoint;
import com.argonathsystems.mods.questtrackerui.hud.RenderContext;
import com.argonathsystems.mods.questtrackerui.theme.Theme;

/**
 * Renders waypoints on the compass/screen edge.
 */
public class CompassRenderer {
    
    private static final int INDICATOR_SIZE = 16;
    private static final int EDGE_PADDING = 20;
    
    private final WaypointConfig config;
    private Theme theme;
    
    /**
     * Create a compass renderer.
     *
     * @param config Waypoint configuration
     * @param theme Theme for colors
     */
    public CompassRenderer(WaypointConfig config, Theme theme) {
        this.config = config;
        this.theme = theme;
    }
    
    /**
     * Set the theme.
     *
     * @param theme New theme
     */
    public void setTheme(Theme theme) {
        this.theme = theme;
    }
    
    /**
     * Render a waypoint on the screen edge if off-screen.
     *
     * @param ctx Render context
     * @param waypoint Waypoint to render
     * @param playerLocation Player's current location
     * @param playerYaw Player's current horizontal rotation
     */
    public void render(RenderContext ctx, QuestWaypoint waypoint, LocationData playerLocation, float playerYaw) {
        if (!config.enabled() || !config.showOnScreen() || !waypoint.showCompass()) {
            return;
        }
        
        LocationData wpPos = waypoint.position();
        
        // Check if in same world
        if (!playerLocation.world().equals(wpPos.world())) {
            return;
        }
        
        // Calculate distance
        double distance = playerLocation.distance(wpPos);
        if (distance > config.maxDistance()) {
            return;
        }
        
        // Calculate angle to waypoint
        double dx = wpPos.x() - playerLocation.x();
        double dz = wpPos.z() - playerLocation.z();
        double angleToWaypoint = Math.toDegrees(Math.atan2(-dx, dz));
        
        // Calculate relative angle from player's view
        double relativeAngle = normalizeAngle(angleToWaypoint - playerYaw);
        
        // Calculate screen position
        Vec2 screenPos = angleToScreenPosition(relativeAngle, ctx.screenWidth(), ctx.screenHeight());
        
        // Calculate opacity
        double opacity = config.calculateOpacity(distance);
        
        // Render indicator
        renderIndicator(ctx, screenPos, waypoint, distance, opacity, isOffScreen(relativeAngle));
    }
    
    /**
     * Convert angle to screen position.
     */
    private Vec2 angleToScreenPosition(double relativeAngle, int screenWidth, int screenHeight) {
        // Field of view (approximate)
        double fov = 70.0;
        double halfFov = fov / 2;
        
        // If within FOV, calculate position on screen
        if (Math.abs(relativeAngle) <= halfFov) {
            double normalizedX = relativeAngle / halfFov; // -1 to 1
            int x = (int) ((normalizedX + 1) / 2 * screenWidth);
            return new Vec2(x, EDGE_PADDING); // Top of screen
        }
        
        // Off-screen - clamp to edge
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;
        
        if (relativeAngle > 0) {
            // Right side
            if (relativeAngle <= 90) {
                return new Vec2(screenWidth - EDGE_PADDING, EDGE_PADDING);
            } else {
                return new Vec2(screenWidth - EDGE_PADDING, screenHeight - EDGE_PADDING);
            }
        } else {
            // Left side
            if (relativeAngle >= -90) {
                return new Vec2(EDGE_PADDING, EDGE_PADDING);
            } else {
                return new Vec2(EDGE_PADDING, screenHeight - EDGE_PADDING);
            }
        }
    }
    
    /**
     * Check if an angle is off-screen.
     */
    private boolean isOffScreen(double relativeAngle) {
        return Math.abs(relativeAngle) > 35; // Half of approximate FOV
    }
    
    /**
     * Normalize angle to -180 to 180 range.
     */
    private double normalizeAngle(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }
    
    /**
     * Render the waypoint indicator.
     */
    private void renderIndicator(RenderContext ctx, Vec2 pos, QuestWaypoint waypoint,
                                  double distance, double opacity, boolean isEdge) {
        int alpha = (int) (opacity * 255);
        int color = (alpha << 24) | 0xFFFFFF;
        Style style = Style.of(TextColor.of(color));
        
        // Draw icon
        String iconPath = theme.icons().waypoint();
        ctx.drawTexture(iconPath, pos.x - INDICATOR_SIZE / 2, pos.y - INDICATOR_SIZE / 2,
                       INDICATOR_SIZE, INDICATOR_SIZE);
        
        // Draw distance
        if (config.showDistance() && waypoint.showDistance()) {
            String distanceText = formatDistance(distance);
            int textX = pos.x - ctx.textWidth(distanceText) / 2;
            int textY = pos.y + INDICATOR_SIZE / 2 + 2;
            ctx.drawText(Component.text(distanceText, theme.fonts().small().merge(style)), textX, textY);
        }
        
        // Draw label if on edge
        if (isEdge && waypoint.label() != null && !waypoint.label().isEmpty()) {
            String label = waypoint.label();
            int labelX = pos.x - ctx.textWidth(label) / 2;
            int labelY = pos.y - INDICATOR_SIZE / 2 - ctx.textHeight() - 2;
            ctx.drawText(Component.text(label, theme.fonts().body().merge(style)), labelX, labelY);
        }
    }
    
    /**
     * Format distance for display.
     */
    private String formatDistance(double meters) {
        if (meters >= 1000) {
            return String.format("%.1fkm", meters / 1000);
        }
        return String.format("%.0fm", meters);
    }
    
    /**
     * Simple 2D vector.
     */
    private record Vec2(int x, int y) {}
}
