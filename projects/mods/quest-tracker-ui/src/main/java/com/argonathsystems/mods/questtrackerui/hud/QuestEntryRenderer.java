package com.argonathsystems.mods.questtrackerui.hud;

import com.argonathsystems.mods.questtrackerui.api.TrackedObjective;
import com.argonathsystems.mods.questtrackerui.api.TrackedQuest;
import com.argonathsystems.mods.questtrackerui.theme.Theme;

import java.util.List;
import java.util.function.Function;

/**
 * Renders a single quest entry in the HUD.
 */
public class QuestEntryRenderer {
    
    private static final int PADDING = 6;
    private static final int OBJECTIVE_INDENT = 12;
    private static final int ICON_SIZE = 12;
    
    private final ObjectiveRenderer objectiveRenderer;
    private Theme theme;
    
    /**
     * Create a quest entry renderer with the given theme.
     *
     * @param theme Theme to use for rendering
     */
    public QuestEntryRenderer(Theme theme) {
        this.theme = theme;
        this.objectiveRenderer = new ObjectiveRenderer(theme);
    }
    
    /**
     * Set the theme for rendering.
     *
     * @param theme New theme
     */
    public void setTheme(Theme theme) {
        this.theme = theme;
        this.objectiveRenderer.setTheme(theme);
    }
    
    /**
     * Render a quest entry.
     *
     * @param ctx Render context
     * @param quest Quest to render
     * @param x X position
     * @param y Y position
     * @param width Available width
     * @param maxObjectives Maximum number of objectives to show
     * @param showProgressBars Whether to show progress bars
     * @param distanceCalculator Function to calculate distance for objectives
     * @return Height of the rendered entry
     */
    public int render(RenderContext ctx, TrackedQuest quest, int x, int y, int width,
                      int maxObjectives, boolean showProgressBars,
                      Function<TrackedObjective, Double> distanceCalculator) {
        int currentY = y;
        
        // Draw quest title with icon
        int titleColor = getQuestColor(quest);
        String icon = quest.type().icon();
        
        ctx.drawTextWithShadow(icon, x, currentY, titleColor);
        ctx.drawTextWithShadow(" " + quest.name(), x + ctx.textWidth(icon), currentY, titleColor);
        
        // Draw timer if timed quest
        if (quest.isTimed()) {
            String timerText = "⏱ " + quest.formattedTimeRemaining();
            int timerColor = getTimerColor(quest);
            int timerX = x + width - ctx.textWidth(timerText);
            ctx.drawText(timerText, timerX, currentY, timerColor);
        }
        
        currentY += ctx.textHeight() + 2;
        
        // Draw objectives
        List<TrackedObjective> objectives = quest.objectives();
        int shownObjectives = Math.min(objectives.size(), maxObjectives);
        
        for (int i = 0; i < shownObjectives; i++) {
            TrackedObjective objective = objectives.get(i);
            double distance = distanceCalculator != null ? distanceCalculator.apply(objective) : -1;
            
            int objHeight = objectiveRenderer.render(
                ctx, objective,
                x + OBJECTIVE_INDENT, currentY,
                width - OBJECTIVE_INDENT,
                showProgressBars, objective.hasWaypoint(), distance
            );
            currentY += objHeight;
        }
        
        // Show "and X more..." if objectives are hidden
        if (objectives.size() > maxObjectives) {
            int remaining = objectives.size() - maxObjectives;
            String moreText = "... and " + remaining + " more";
            ctx.drawText(moreText, x + OBJECTIVE_INDENT, currentY, theme.colors().textSecondaryArgb());
            currentY += ctx.textHeight();
        }
        
        return currentY - y + PADDING;
    }
    
    /**
     * Get the color for a quest type.
     *
     * @param quest Quest to get color for
     * @return ARGB color
     */
    private int getQuestColor(TrackedQuest quest) {
        String hexColor = switch (quest.type()) {
            case MAIN -> theme.colors().questMain();
            case SIDE -> theme.colors().questSide();
            case TIMED -> theme.colors().questTimed();
            case GUILD -> theme.colors().questGuild();
            case DAILY -> theme.colors().questDaily();
            case WEEKLY -> theme.colors().questWeekly();
            case EVENT -> theme.colors().questMain(); // Use main color for events
        };
        return theme.colors().parseHex(hexColor);
    }
    
    /**
     * Get the timer color based on remaining time.
     *
     * @param quest Timed quest
     * @return ARGB color
     */
    private int getTimerColor(TrackedQuest quest) {
        if (quest.timeRemaining() == null) {
            return theme.colors().textPrimaryArgb();
        }
        
        long secondsRemaining = quest.timeRemaining().toSeconds();
        
        if (secondsRemaining <= 60) {
            return theme.colors().parseHex(theme.colors().timerCritical());
        } else if (secondsRemaining <= 300) {
            return theme.colors().parseHex(theme.colors().timerWarning());
        }
        return theme.colors().parseHex(theme.colors().timerNormal());
    }
    
    /**
     * Calculate the height needed to render a quest entry.
     *
     * @param ctx Render context
     * @param quest Quest to measure
     * @param maxObjectives Maximum objectives to show
     * @return Height in pixels
     */
    public int calculateHeight(RenderContext ctx, TrackedQuest quest, int maxObjectives) {
        int height = ctx.textHeight() + 2; // Title
        
        int objectives = Math.min(quest.objectives().size(), maxObjectives);
        height += objectives * (ctx.textHeight() + 2);
        
        if (quest.objectives().size() > maxObjectives) {
            height += ctx.textHeight(); // "and X more" text
        }
        
        return height + PADDING;
    }
}
