package com.argonathsystems.mods.questtrackerui.hud;

import com.argonathsystems.mods.questtrackerui.api.TrackedObjective;
import com.argonathsystems.mods.questtrackerui.api.TrackedQuest;
import com.argonathsystems.mods.questtrackerui.config.TrackerConfig;
import com.argonathsystems.mods.questtrackerui.theme.Theme;
import com.argonathsystems.mods.questtrackerui.theme.ThemeRegistry;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * HYUIML-based HUD component for displaying tracked quests.
 * 
 * <p>This implementation replaces programmatic rendering with template-based
 * HYUIML generation that can be hot-reloaded during development.
 * 
 * <p>Usage:
 * <pre>{@code
 * HyuimlQuestTrackerHUD hud = new HyuimlQuestTrackerHUD(themeRegistry, config);
 * hud.setTemplateSupplier(UnifiedUIManager.getInstance()
 *     .createUISupplier("quest-tracker", "config/ui/huds/quest-tracker.hyuiml"));
 * 
 * String html = hud.generateHtml();
 * HudBuilder.forPlayer(playerRef).fromHtml(html).build(store);
 * }</pre>
 * 
 * @author Argonath Systems
 * @since 1.1.0
 */
public class HyuimlQuestTrackerHUD {
    
    private final ThemeRegistry themeRegistry;
    private TrackerConfig config;
    private boolean collapsed;
    private boolean visible;
    
    // Template supplier (for hot reload support)
    private Supplier<String> templateSupplier;
    
    // Cached state
    private List<TrackedQuest> pinnedQuests;
    private Function<TrackedObjective, Double> distanceCalculator;
    
    /**
     * Create a new HYUIML-based quest tracker HUD.
     *
     * @param themeRegistry Theme registry for active theme
     * @param config Tracker configuration
     */
    public HyuimlQuestTrackerHUD(ThemeRegistry themeRegistry, TrackerConfig config) {
        this.themeRegistry = themeRegistry;
        this.config = config;
        this.collapsed = config.collapse().defaultCollapsed();
        this.visible = true;
        this.pinnedQuests = Collections.emptyList();
    }
    
    /**
     * Set the template supplier for hot reload support.
     * 
     * @param templateSupplier Supplier providing the base HYUIML template
     */
    public void setTemplateSupplier(Supplier<String> templateSupplier) {
        this.templateSupplier = templateSupplier;
    }
    
    /**
     * Update the configuration.
     *
     * @param config New configuration
     */
    public void setConfig(TrackerConfig config) {
        this.config = config;
    }
    
    /**
     * Set the pinned quests to display.
     *
     * @param quests List of pinned quests
     */
    public void setPinnedQuests(List<TrackedQuest> quests) {
        int maxQuests = config.display().maxPinnedQuests();
        if (quests.size() > maxQuests) {
            this.pinnedQuests = quests.subList(0, maxQuests);
        } else {
            this.pinnedQuests = quests;
        }
    }
    
    /**
     * Get the currently pinned quests.
     *
     * @return List of pinned quests
     */
    public List<TrackedQuest> getPinnedQuests() {
        return pinnedQuests;
    }
    
    /**
     * Set the function for calculating distances to objectives.
     *
     * @param calculator Distance calculator function
     */
    public void setDistanceCalculator(Function<TrackedObjective, Double> calculator) {
        this.distanceCalculator = calculator;
    }
    
    /**
     * Toggle visibility of the tracker.
     */
    public void toggleVisibility() {
        this.visible = !this.visible;
    }
    
    /**
     * Set visibility of the tracker.
     *
     * @param visible Whether the tracker should be visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    /**
     * Check if the tracker is visible.
     *
     * @return true if visible
     */
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Toggle collapsed state.
     */
    public void toggleCollapsed() {
        if (config.collapse().enabled()) {
            this.collapsed = !this.collapsed;
        }
    }
    
    /**
     * Check if the tracker is collapsed.
     *
     * @return true if collapsed
     */
    public boolean isCollapsed() {
        return collapsed;
    }
    
    /**
     * Generate the complete HYUIML for the quest tracker HUD.
     * 
     * <p>This method processes the template and injects current quest data.
     * Call this each time the HUD needs to be refreshed.
     *
     * @return Complete HYUIML string ready for HudBuilder
     */
    public String generateHtml() {
        if (!visible) {
            return "<!-- Quest Tracker Hidden -->";
        }
        
        Theme theme = themeRegistry.getActiveTheme();
        StringBuilder html = new StringBuilder();
        
        // Start container with theme styles
        html.append(generateContainerStart(theme));
        
        // Header
        html.append(generateHeader(theme));
        
        // Content (if not collapsed)
        if (!collapsed) {
            html.append(generateContent(theme));
        }
        
        // Footer
        html.append(generateFooter(theme));
        
        // Close container
        html.append("</div>");
        
        return html.toString();
    }
    
    /**
     * Generate HTML using the hot-reloadable template with data injection.
     * 
     * <p>Preferred method when using hot reload during development.
     *
     * @return Processed HYUIML with quest data injected
     */
    public String generateFromTemplate() {
        if (templateSupplier == null) {
            // Fall back to programmatic generation
            return generateHtml();
        }
        
        String template = templateSupplier.get();
        if (template == null || template.isEmpty()) {
            return generateHtml();
        }
        
        // Process template variables
        return processTemplate(template);
    }
    
    // ========== Template Processing ==========
    
    private String processTemplate(String template) {
        Theme theme = themeRegistry.getActiveTheme();
        
        // Replace visibility
        template = template.replace("{{$visible}}", String.valueOf(visible));
        template = template.replace("{{$collapsed}}", String.valueOf(collapsed));
        template = template.replace("{{$collapseIcon}}", collapsed ? "+" : "−");
        
        // Replace counts
        template = template.replace("{{$trackedCount}}", String.valueOf(pinnedQuests.size()));
        template = template.replace("{{$maxTracked}}", String.valueOf(config.display().maxPinnedQuests()));
        
        // Process quest entries
        String questEntriesHtml = generateQuestEntries(theme);
        template = replaceSection(template, "{{#each trackedQuests}}", "{{/each}}", questEntriesHtml);
        
        // Replace theme colors
        template = template.replace("{{$colorPrimary}}", theme.colors().textPrimary());
        template = template.replace("{{$colorSecondary}}", theme.colors().textSecondary());
        template = template.replace("{{$colorBackground}}", theme.colors().background());
        template = template.replace("{{$colorBorder}}", theme.colors().border());
        
        return template;
    }
    
    private String replaceSection(String template, String startMarker, String endMarker, String replacement) {
        int startIdx = template.indexOf(startMarker);
        int endIdx = template.indexOf(endMarker);
        
        if (startIdx >= 0 && endIdx > startIdx) {
            return template.substring(0, startIdx) + replacement + template.substring(endIdx + endMarker.length());
        }
        
        return template;
    }
    
    private String generateQuestEntries(Theme theme) {
        if (pinnedQuests.isEmpty()) {
            return """
                <div class="empty-state">
                    <p class="empty-text">No quests tracked</p>
                    <p class="empty-hint">Use /quest track or click the + button</p>
                </div>
                """;
        }
        
        StringBuilder html = new StringBuilder();
        int maxObjectives = config.display().maxVisibleObjectives();
        
        for (TrackedQuest quest : pinnedQuests) {
            html.append(generateQuestEntry(quest, theme, maxObjectives));
        }
        
        return html.toString();
    }
    
    // ========== Programmatic Generation ==========
    
    private String generateContainerStart(Theme theme) {
        return String.format("""
            <div class="hud-quest-tracker" data-hyui-layer="overlay" 
                 style="anchor-top: %d; anchor-right: %d; anchor-width: %d; 
                        background-color: %s; visibility: %s;">
            """,
            config.position().offsetY(),
            config.position().offsetX(),
            config.size().width(),
            theme.colors().background(),
            visible ? "visible" : "hidden"
        );
    }
    
    private String generateHeader(Theme theme) {
        return String.format("""
            <div class="tracker-header" style="layout-mode: Left; gap: 8; margin-bottom: 8;">
                <p class="tracker-title" style="font-size: 14; font-weight: bold; color: %s; flex-weight: 1;">
                    📜 QUEST TRACKER
                </p>
                <button id="btn-collapse" class="tracker-btn" 
                        style="anchor-width: 24; anchor-height: 24; font-size: 12; padding: 0;">
                    %s
                </button>
                <button id="btn-settings" class="tracker-btn"
                        style="anchor-width: 24; anchor-height: 24; font-size: 12; padding: 0;">
                    ⚙
                </button>
            </div>
            """,
            theme.colors().questMain(),
            collapsed ? "+" : "−"
        );
    }
    
    private String generateContent(Theme theme) {
        StringBuilder html = new StringBuilder();
        html.append("<div class=\"tracker-content\" style=\"layout-mode: Top; gap: 12;\">");
        
        if (pinnedQuests.isEmpty()) {
            html.append("""
                <div class="empty-state" style="padding: 16; text-align: center;">
                    <p style="color: #888888; font-size: 12;">No quests tracked</p>
                    <p style="color: #666666; font-size: 10;">Press J to open Quest Book</p>
                </div>
                """);
        } else {
            int maxObjectives = config.display().maxVisibleObjectives();
            for (TrackedQuest quest : pinnedQuests) {
                html.append(generateQuestEntry(quest, theme, maxObjectives));
            }
        }
        
        html.append("</div>");
        return html.toString();
    }
    
    private String generateQuestEntry(TrackedQuest quest, Theme theme, int maxObjectives) {
        String questClass = getQuestCssClass(quest);
        String questColor = getQuestColor(quest, theme);
        
        StringBuilder html = new StringBuilder();
        html.append(String.format("""
            <div class="quest-entry %s" data-quest-id="%s" 
                 style="layout-mode: Top; background-color: rgba(30, 30, 40, 0.8); 
                        padding: 8; gap: 4; border-left: 3px solid %s;">
            """, questClass, quest.id(), questColor));
        
        // Quest header
        html.append(String.format("""
                <div class="quest-header" style="layout-mode: Left; gap: 6;">
                    <p class="quest-name" style="flex-weight: 1; font-size: 12; font-weight: bold; color: %s;">
                        %s %s
                    </p>
            """, questColor, quest.type().icon(), escapeHtml(quest.name())));
        
        // Timer for timed quests
        if (quest.isTimed() && quest.timeRemaining() != null) {
            String timerColor = getTimerColor(quest, theme);
            html.append(String.format("""
                    <span class="quest-timer" style="font-size: 10; color: %s;">
                        ⏱ %s
                    </span>
                """, timerColor, quest.formattedTimeRemaining()));
        }
        
        // Action buttons
        html.append(String.format("""
                    <button id="btn-waypoint-%s" class="quest-btn" 
                            style="anchor-width: 20; anchor-height: 20; font-size: 10; padding: 0; background-color: transparent;"
                            data-hyui-tooltiptext="Set Waypoint">📍</button>
                    <button id="btn-untrack-%s" class="quest-btn"
                            style="anchor-width: 20; anchor-height: 20; font-size: 10; padding: 0; background-color: transparent;"
                            data-hyui-tooltiptext="Stop Tracking">×</button>
                </div>
            """, quest.id(), quest.id()));
        
        // Objectives
        html.append("<div class=\"quest-objectives\" style=\"layout-mode: Top; padding-left: 8; gap: 2;\">");
        
        List<TrackedObjective> objectives = quest.objectives();
        int shownCount = Math.min(objectives.size(), maxObjectives);
        
        for (int i = 0; i < shownCount; i++) {
            html.append(generateObjective(objectives.get(i), theme));
        }
        
        // "And X more" text
        if (objectives.size() > maxObjectives) {
            int remaining = objectives.size() - maxObjectives;
            html.append(String.format("""
                <p class="more-objectives" style="font-size: 10; color: #666666; padding-left: 18;">
                    ... and %d more
                </p>
                """, remaining));
        }
        
        html.append("</div>"); // Close objectives
        html.append("</div>"); // Close quest entry
        
        return html.toString();
    }
    
    private String generateObjective(TrackedObjective objective, Theme theme) {
        String markerClass = objective.isComplete() ? "complete" : "";
        String marker = objective.isComplete() ? "●" : "○";
        String textColor = objective.isComplete() 
            ? theme.colors().textCompleted() 
            : theme.colors().textSecondary();
        String textStyle = objective.isComplete() ? "text-decoration: line-through;" : "";
        
        StringBuilder html = new StringBuilder();
        html.append(String.format("""
            <div class="objective" style="layout-mode: Left; gap: 6;">
                <span class="objective-marker %s" style="anchor-width: 12; font-size: 10; color: %s;">%s</span>
                <p class="objective-text" style="flex-weight: 1; font-size: 11; color: %s; %s">
                    %s
                </p>
            """, markerClass, objective.isComplete() ? "#90ee90" : "#888888", marker, 
                 textColor, textStyle, escapeHtml(objective.description())));
        
        // Progress indicator
        if (objective.requiredProgress() > 1) {
            html.append(String.format("""
                <span class="objective-progress" style="font-size: 10; color: #888888;">
                    %d/%d
                </span>
                """, objective.currentProgress(), objective.requiredProgress()));
        }
        
        // Distance indicator
        if (objective.hasWaypoint() && distanceCalculator != null) {
            Double distance = distanceCalculator.apply(objective);
            if (distance != null && distance >= 0) {
                html.append(String.format("""
                    <span class="objective-distance" style="font-size: 10; color: #888888;">
                        %s →
                    </span>
                    """, formatDistance(distance)));
            }
        }
        
        html.append("</div>");
        return html.toString();
    }
    
    private String generateFooter(Theme theme) {
        return String.format("""
            <div class="tracker-footer" style="layout-mode: Left; margin-top: 8; padding-top: 8; 
                                                border-top: 1px solid #333333;">
                <p class="tracked-count" style="flex-weight: 1; font-size: 10; color: #888888;">
                    Tracked: %d/%d
                </p>
                <button id="btn-add-quest" class="btn-add-quest" style="font-size: 10; padding: 4 8;">
                    + Quest
                </button>
            </div>
            """, pinnedQuests.size(), config.display().maxPinnedQuests());
    }
    
    // ========== Helpers ==========
    
    private String getQuestCssClass(TrackedQuest quest) {
        return switch (quest.type()) {
            case MAIN -> "main-quest";
            case SIDE -> "side-quest";
            case DAILY -> "daily-quest";
            case WEEKLY -> "weekly-quest";
            case GUILD -> "guild-quest";
            case TIMED -> "timed-quest";
            case EVENT -> "event-quest";
        };
    }
    
    private String getQuestColor(TrackedQuest quest, Theme theme) {
        return switch (quest.type()) {
            case MAIN -> theme.colors().questMain();
            case SIDE -> theme.colors().questSide();
            case DAILY -> theme.colors().questDaily();
            case WEEKLY -> theme.colors().questWeekly();
            case GUILD -> theme.colors().questGuild();
            case TIMED -> theme.colors().questTimed();
            case EVENT -> theme.colors().questMain();
        };
    }
    
    private String getTimerColor(TrackedQuest quest, Theme theme) {
        Duration remaining = quest.timeRemaining();
        if (remaining == null) {
            return theme.colors().textPrimary();
        }
        
        long seconds = remaining.toSeconds();
        if (seconds <= 60) {
            return theme.colors().timerCritical();
        } else if (seconds <= 300) {
            return theme.colors().timerWarning();
        }
        return theme.colors().timerNormal();
    }
    
    private String formatDistance(double meters) {
        if (meters >= 1000) {
            return String.format("%.1f km", meters / 1000);
        }
        return String.format("%.0f m", meters);
    }
    
    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;");
    }
}
