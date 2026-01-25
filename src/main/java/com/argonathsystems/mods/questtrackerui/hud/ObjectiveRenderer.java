package com.argonathsystems.mods.questtrackerui.hud;

import com.argonathsystems.mods.questtrackerui.api.TrackedObjective;
import com.argonathsystems.mods.questtrackerui.theme.Theme;

/**
 * Renders individual objectives within a quest entry.
 */
public class ObjectiveRenderer {
    
    private static final int ICON_SIZE = 10;
    private static final int PROGRESS_BAR_WIDTH = 60;
    private static final int PROGRESS_BAR_HEIGHT = 6;
    private static final int PADDING = 4;
    
    private Theme theme;
    
    /**
     * Create an objective renderer with the given theme.
     *
     * @param theme Theme to use for rendering
     */
    public ObjectiveRenderer(Theme theme) {
        this.theme = theme;
    }
    
    /**
     * Set the theme for rendering.
     *
     * @param theme New theme
     */
    public void setTheme(Theme theme) {
        this.theme = theme;
    }
    
    /**
     * Render an objective.
     *
     * @param ctx Render context
     * @param objective Objective to render
     * @param x X position
     * @param y Y position
     * @param width Available width
     * @param showProgressBar Whether to show progress bar
     * @param showDistance Whether to show distance to waypoint
     * @param distanceMeters Distance in meters (if applicable)
     * @return Height of the rendered objective
     */
    public int render(RenderContext ctx, TrackedObjective objective, int x, int y, int width,
                      boolean showProgressBar, boolean showDistance, double distanceMeters) {
        int currentY = y;
        
        // Determine text color based on completion
        int textColor = objective.isComplete() 
            ? theme.colors().textCompletedArgb()
            : theme.colors().textSecondaryArgb();
        
        // Draw completion icon
        String icon = objective.isComplete() ? "☑" : "☐";
        ctx.drawText(icon, x, currentY, textColor);
        
        // Draw objective description
        int textX = x + ICON_SIZE + PADDING;
        String description = objective.description();
        
        // Truncate if too long
        int maxTextWidth = width - ICON_SIZE - PADDING * 2;
        if (showProgressBar) {
            maxTextWidth -= PROGRESS_BAR_WIDTH + PADDING;
        }
        if (showDistance && objective.hasWaypoint()) {
            maxTextWidth -= 50; // Space for distance display
        }
        
        description = truncateText(ctx, description, maxTextWidth);
        ctx.drawText(description, textX, currentY, textColor);
        
        // Draw progress bar if applicable
        if (showProgressBar && objective.requiredProgress() > 1) {
            int barX = x + width - PROGRESS_BAR_WIDTH;
            renderProgressBar(ctx, objective, barX, currentY + 2);
        }
        
        // Draw distance if applicable
        if (showDistance && objective.hasWaypoint() && distanceMeters >= 0) {
            String distanceText = formatDistance(distanceMeters);
            int distanceX = x + width - ctx.textWidth(distanceText);
            ctx.drawText("→ " + distanceText, distanceX - 16, currentY, theme.colors().textSecondaryArgb());
        }
        
        return ctx.textHeight() + 2;
    }
    
    /**
     * Render the progress bar for an objective.
     *
     * @param ctx Render context
     * @param objective Objective with progress
     * @param x X position
     * @param y Y position
     */
    private void renderProgressBar(RenderContext ctx, TrackedObjective objective, int x, int y) {
        // Background
        ctx.fillRect(x, y, PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT, 
            theme.colors().parseHex(theme.colors().progressBackground()));
        
        // Fill
        double percent = objective.progressPercent();
        int fillWidth = (int) (PROGRESS_BAR_WIDTH * percent);
        
        int fillColor = objective.isComplete()
            ? theme.colors().parseHex(theme.colors().progressComplete())
            : theme.colors().parseHex(theme.colors().progressFill());
        
        if (fillWidth > 0) {
            ctx.fillRect(x, y, fillWidth, PROGRESS_BAR_HEIGHT, fillColor);
        }
    }
    
    /**
     * Truncate text to fit within a maximum width.
     *
     * @param ctx Render context for measuring
     * @param text Text to truncate
     * @param maxWidth Maximum width in pixels
     * @return Truncated text (with ... if truncated)
     */
    private String truncateText(RenderContext ctx, String text, int maxWidth) {
        if (ctx.textWidth(text) <= maxWidth) {
            return text;
        }
        
        String ellipsis = "...";
        int ellipsisWidth = ctx.textWidth(ellipsis);
        
        int end = text.length() - 1;
        while (end > 0 && ctx.textWidth(text.substring(0, end)) + ellipsisWidth > maxWidth) {
            end--;
        }
        
        return text.substring(0, end) + ellipsis;
    }
    
    /**
     * Format distance for display.
     *
     * @param meters Distance in meters
     * @return Formatted distance string
     */
    private String formatDistance(double meters) {
        if (meters < 0) {
            return "";
        }
        if (meters >= 1000) {
            return String.format("%.1fkm", meters / 1000);
        }
        return String.format("%.0fm", meters);
    }
    
    /**
     * Calculate the height needed to render an objective.
     *
     * @param ctx Render context
     * @param objective Objective to measure
     * @return Height in pixels
     */
    public int calculateHeight(RenderContext ctx, TrackedObjective objective) {
        return ctx.textHeight() + 2;
    }
}
