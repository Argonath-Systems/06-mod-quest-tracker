package com.argonathsystems.mods.questtrackerui.hud;

import com.argonathsystems.framework.text.Component;
import com.argonathsystems.framework.text.Style;
import com.argonathsystems.framework.text.TextColor;
import com.argonathsystems.mods.questtrackerui.api.TrackedObjective;
import com.argonathsystems.mods.questtrackerui.api.TrackedQuest;
import com.argonathsystems.mods.questtrackerui.config.TrackerConfig;
import com.argonathsystems.mods.questtrackerui.theme.Theme;
import com.argonathsystems.mods.questtrackerui.theme.ThemeRegistry;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Main HUD component for displaying tracked quests.
 */
public class QuestTrackerHUD {
    
    private static final int HEADER_HEIGHT = 18;
    private static final int PADDING = 8;
    private static final int QUEST_SPACING = 4;
    
    private final ThemeRegistry themeRegistry;
    private final QuestEntryRenderer questRenderer;
    private TrackerConfig config;
    private boolean collapsed;
    private boolean visible;
    
    // Cached state
    private List<TrackedQuest> pinnedQuests;
    private Function<TrackedObjective, Double> distanceCalculator;
    
    /**
     * Create a new quest tracker HUD.
     *
     * @param themeRegistry Theme registry for active theme
     * @param config Tracker configuration
     */
    public QuestTrackerHUD(ThemeRegistry themeRegistry, TrackerConfig config) {
        this.themeRegistry = themeRegistry;
        this.config = config;
        this.questRenderer = new QuestEntryRenderer(themeRegistry.getActiveTheme());
        this.collapsed = config.collapse().defaultCollapsed();
        this.visible = true;
        this.pinnedQuests = Collections.emptyList();
        
        // Listen for theme changes
        themeRegistry.addChangeListener((oldTheme, newTheme) -> {
            questRenderer.setTheme(newTheme);
        });
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
     * Generate XAML content for the quest tracker.
     */
    public String generateXaml() {
        StringBuilder xaml = new StringBuilder();
        
        if (pinnedQuests.isEmpty()) {
             xaml.append("<Border Background='#80000000' CornerRadius='4' Padding='10' Margin='0,0,0,5'>")
                 .append("<TextBlock Text='No Quests' Foreground='#FFD700' FontSize='18' FontWeight='Bold'/>")
                 .append("</Border>");
        } else {
            for (TrackedQuest quest : pinnedQuests) {
                xaml.append("<Border Background='#80000000' CornerRadius='4' Padding='10' Margin='0,0,0,5'><StackPanel>");
                
                // Title
                xaml.append("<StackPanel Orientation='Horizontal' Margin='0,0,0,5'>");
                xaml.append("<TextBlock Text='").append(quest.type().icon()).append("' Foreground='#FFD700' FontSize='18' Margin='0,0,5,0'/>");
                xaml.append("<TextBlock Text='").append(quest.name()).append("' Foreground='#FFD700' FontSize='18' FontWeight='Bold'/>");
                xaml.append("</StackPanel>");
                
                // Objectives
                xaml.append("<StackPanel Margin='10,0,0,0'>");
                 List<TrackedObjective> objectives = quest.objectives();
                 for (TrackedObjective obj : objectives) {
                     String color = obj.isComplete() ? "#55FF55" : "#FFFFFF";
                     String decoration = obj.isComplete() ? "Strikethrough" : "None";
                     
                     xaml.append("<TextBlock Text='- ").append(obj.description());
                     if (obj.currentProgress() > 0 && obj.requiredProgress() > 0) {
                         xaml.append(" (").append(obj.currentProgress()).append("/").append(obj.requiredProgress()).append(")");
                     }
                     xaml.append("' Foreground='").append(color).append("' TextDecorations='").append(decoration).append("' FontSize='14' Margin='0,0,0,2'/>");
                 }
                xaml.append("</StackPanel>"); // objective-list

                xaml.append("</StackPanel></Border>"); // quest-entry-stack, quest-entry-border
            }
        }
        return xaml.toString();
    }
    
    /*
    public String generateHtml() {
        // Legacy HTML generation removed
    }
    */
    
    /*
    public void render(RenderContext ctx) {
        if (!visible || !config.display().showDistance()) return;
        // Legacy render code removed
    }
    */
    
    /**
     * Set visibility of the tracker.
     *
     * @param visible Whether the tracker should be visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    /**
     * Toggle visibility of the tracker.
     */
    public void toggleVisibility() {
        this.visible = !this.visible;
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
     * Render the quest tracker HUD.
     *
     * @param ctx Render context
     */
    public void render(RenderContext ctx) {
        if (!visible || pinnedQuests.isEmpty()) {
            return;
        }
        
        Theme theme = themeRegistry.getActiveTheme();
        
        // Calculate dimensions
        int width = config.size().width();
        int contentHeight = calculateContentHeight(ctx);
        int totalHeight = Math.min(contentHeight, config.size().maxHeight());
        
        // Calculate position based on anchor
        AnchorPosition anchor = config.position().anchor();
        int x = anchor.calculateX(ctx.screenWidth(), width, config.position().offsetX());
        int y = anchor.calculateY(ctx.screenHeight(), totalHeight, config.position().offsetY());
        
        // Draw background
        drawBackground(ctx, x, y, width, totalHeight, theme);
        
        // Draw header
        int contentY = y + drawHeader(ctx, x, y, width, theme);
        
        // Draw quests (if not collapsed)
        if (!collapsed) {
            ctx.pushScissor(x, contentY, width, totalHeight - HEADER_HEIGHT);
            drawQuests(ctx, x + PADDING, contentY, width - PADDING * 2);
            ctx.popScissor();
        }
    }
    
    /**
     * Draw the background panel.
     */
    private void drawBackground(RenderContext ctx, int x, int y, int width, int height, Theme theme) {
        int bgColor = theme.colors().backgroundArgb();
        
        switch (theme.borders().style()) {
            case NONE -> ctx.fillRect(x, y, width, height, bgColor);
            case SOLID -> {
                ctx.fillRect(x, y, width, height, bgColor);
                ctx.drawRect(x, y, width, height, theme.colors().borderArgb(), theme.borders().width());
            }
            case ROUNDED -> ctx.fillRoundedRect(x, y, width, height, theme.borders().radius(), bgColor);
            case FANCY -> {
                drawFancyBorder(ctx, x, y, width, height, theme);
            }
        }
    }
    
    /**
     * Draw a decorative fancy border with corner accents and gradient-like effect.
     * Creates a medieval/fantasy style border fitting for LOTR theme.
     */
    private void drawFancyBorder(RenderContext ctx, int x, int y, int width, int height, Theme theme) {
        int bgColor = theme.colors().backgroundArgb();
        int borderColor = theme.colors().borderArgb();
        // Use quest main color (gold/primary) as the accent for decorations
        int accentColor = theme.colors().questMainArgb();
        int radius = theme.borders().radius();
        int borderWidth = theme.borders().width();
        
        // Main background with rounded corners
        ctx.fillRoundedRect(x, y, width, height, radius, bgColor);
        
        // Outer border
        ctx.drawRect(x, y, width, height, borderColor, borderWidth);
        
        // Inner accent border (offset by 2 pixels)
        int innerOffset = borderWidth + 2;
        ctx.drawRect(x + innerOffset, y + innerOffset, 
                     width - (innerOffset * 2), height - (innerOffset * 2), 
                     accentColor, 1);
        
        // Corner decorations - small filled squares at corners for a medieval look
        int cornerSize = 6;
        
        // Top-left corner
        ctx.fillRect(x, y, cornerSize, cornerSize, accentColor);
        // Top-right corner  
        ctx.fillRect(x + width - cornerSize, y, cornerSize, cornerSize, accentColor);
        // Bottom-left corner
        ctx.fillRect(x, y + height - cornerSize, cornerSize, cornerSize, accentColor);
        // Bottom-right corner
        ctx.fillRect(x + width - cornerSize, y + height - cornerSize, cornerSize, cornerSize, accentColor);
        
        // Edge decorations - small accent marks at the center of each edge
        int edgeMarkSize = 4;
        int edgeMarkLength = 12;
        
        // Top edge mark
        ctx.fillRect(x + (width / 2) - (edgeMarkLength / 2), y, edgeMarkLength, edgeMarkSize, accentColor);
        // Bottom edge mark
        ctx.fillRect(x + (width / 2) - (edgeMarkLength / 2), y + height - edgeMarkSize, edgeMarkLength, edgeMarkSize, accentColor);
        // Left edge mark
        ctx.fillRect(x, y + (height / 2) - (edgeMarkLength / 2), edgeMarkSize, edgeMarkLength, accentColor);
        // Right edge mark  
        ctx.fillRect(x + width - edgeMarkSize, y + (height / 2) - (edgeMarkLength / 2), edgeMarkSize, edgeMarkLength, accentColor);
    }
    
    /**
     * Draw the header bar.
     *
     * @return Height of the header
     */
    private int drawHeader(RenderContext ctx, int x, int y, int width, Theme theme) {
        // Title
        String title = "QUEST TRACKER";
        Style titleStyle = theme.fonts().title().merge(Style.of(TextColor.fromHex(theme.colors().textPrimary())));
        ctx.drawText(Component.text(title, titleStyle), x + PADDING, y + 4);
        
        // Settings button (gear icon)
        String settingsIcon = "⚙";
        int settingsX = x + width - PADDING - ctx.textWidth(settingsIcon) - 16;
        Style iconStyle = theme.fonts().body().merge(Style.of(TextColor.fromHex(theme.colors().textSecondary())));
        ctx.drawText(Component.text(settingsIcon, iconStyle), settingsX, y + 4);
        
        // Collapse button
        String collapseIcon = collapsed ? "+" : "−";
        int collapseX = x + width - PADDING - ctx.textWidth(collapseIcon);
        ctx.drawText(Component.text(collapseIcon, iconStyle), collapseX, y + 4);
        
        return HEADER_HEIGHT;
    }
    
    /**
     * Draw all pinned quests.
     */
    private void drawQuests(RenderContext ctx, int x, int y, int width) {
        int currentY = y;
        int maxObjectives = config.display().maxVisibleObjectives();
        boolean showBars = config.display().showProgressBars();
        
        for (TrackedQuest quest : pinnedQuests) {
            int height = questRenderer.render(
                ctx, quest, x, currentY, width,
                maxObjectives, showBars, distanceCalculator
            );
            currentY += height + QUEST_SPACING;
        }
    }
    
    /**
     * Calculate the total content height.
     */
    private int calculateContentHeight(RenderContext ctx) {
        if (collapsed) {
            return HEADER_HEIGHT;
        }
        
        int height = HEADER_HEIGHT + PADDING;
        int maxObjectives = config.display().maxVisibleObjectives();
        
        for (TrackedQuest quest : pinnedQuests) {
            height += questRenderer.calculateHeight(ctx, quest, maxObjectives);
            height += QUEST_SPACING;
        }
        
        return height;
    }
    
    /**
     * Check if a point is within the tracker bounds.
     *
     * @param ctx Render context for dimensions
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @return true if the point is within bounds
     */
    public boolean containsPoint(RenderContext ctx, int mouseX, int mouseY) {
        if (!visible || pinnedQuests.isEmpty()) {
            return false;
        }
        
        int width = config.size().width();
        int height = collapsed ? HEADER_HEIGHT : Math.min(calculateContentHeight(ctx), config.size().maxHeight());
        
        AnchorPosition anchor = config.position().anchor();
        int x = anchor.calculateX(ctx.screenWidth(), width, config.position().offsetX());
        int y = anchor.calculateY(ctx.screenHeight(), height, config.position().offsetY());
        
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
