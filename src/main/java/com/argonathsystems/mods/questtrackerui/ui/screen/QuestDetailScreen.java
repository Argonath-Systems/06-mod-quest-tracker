package com.argonathsystems.mods.questtrackerui.ui.screen;

import com.argonathsystems.mods.questtrackerui.api.QuestDataProvider;
import com.argonathsystems.mods.questtrackerui.api.QuestDetails;
import com.argonathsystems.mods.questtrackerui.api.Reward;
import com.argonathsystems.mods.questtrackerui.api.TrackedObjective;
import com.argonathsystems.mods.questtrackerui.api.TrackedQuest;
import com.argonathsystems.mods.questtrackerui.ProviderRegistry;
import com.argonathsystems.mods.questtrackerui.theme.Theme;
import com.argonathsystems.mods.questtrackerui.theme.ThemeColors;
import com.argonathsystems.mods.questtrackerui.theme.ThemeRegistry;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Quest detail screen showing full quest information.
 * 
 * <p>Implements QT-009 from the specification.
 */
public class QuestDetailScreen {
    
    private static final int PADDING = 16;
    private static final int SECTION_SPACING = 16;
    private static final int LINE_SPACING = 4;
    private static final int ICON_SIZE = 20;
    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 24;
    private static final int CHECKBOX_SIZE = 16;
    private static final int PROGRESS_BAR_HEIGHT = 8;
    
    private final ThemeRegistry themeRegistry;
    private final ProviderRegistry providerRegistry;
    private final UUID playerId;
    private final TrackedQuest quest;
    private final QuestDetails details;
    
    // UI State
    private int scrollOffset;
    private int maxScrollOffset;
    
    // Callbacks
    private Runnable onClose;
    private Consumer<TrackedQuest> onShowOnMap;
    private Consumer<TrackedQuest> onShareQuest;
    private Consumer<TrackedQuest> onAbandonQuest;
    private Consumer<TrackedQuest> onPinToggle;
    
    /**
     * Create a new quest detail screen.
     *
     * @param themeRegistry Theme registry
     * @param providerRegistry Quest provider registry
     * @param playerId Current player's ID
     * @param quest Quest to display
     */
    public QuestDetailScreen(ThemeRegistry themeRegistry, ProviderRegistry providerRegistry, 
            UUID playerId, TrackedQuest quest) {
        this.themeRegistry = themeRegistry;
        this.providerRegistry = providerRegistry;
        this.playerId = playerId;
        this.quest = quest;
        this.details = loadQuestDetails(quest.id());
        this.scrollOffset = 0;
    }
    
    private QuestDetails loadQuestDetails(String questId) {
        for (QuestDataProvider provider : providerRegistry.getProviders()) {
            QuestDetails details = provider.getQuestDetails(questId).orElse(null);
            if (details != null) {
                return details;
            }
        }
        return QuestDetails.fromQuest(quest);
    }
    
    /**
     * Set callback for when screen is closed.
     *
     * @param onClose Close callback
     */
    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }
    
    /**
     * Set callback for "Show on Map" action.
     *
     * @param onShowOnMap Map callback
     */
    public void setOnShowOnMap(Consumer<TrackedQuest> onShowOnMap) {
        this.onShowOnMap = onShowOnMap;
    }
    
    /**
     * Set callback for "Share Quest" action.
     *
     * @param onShareQuest Share callback
     */
    public void setOnShareQuest(Consumer<TrackedQuest> onShareQuest) {
        this.onShareQuest = onShareQuest;
    }
    
    /**
     * Set callback for "Abandon Quest" action.
     *
     * @param onAbandonQuest Abandon callback
     */
    public void setOnAbandonQuest(Consumer<TrackedQuest> onAbandonQuest) {
        this.onAbandonQuest = onAbandonQuest;
    }
    
    /**
     * Set callback for pin toggle.
     *
     * @param onPinToggle Pin callback
     */
    public void setOnPinToggle(Consumer<TrackedQuest> onPinToggle) {
        this.onPinToggle = onPinToggle;
    }
    
    /**
     * Render the quest detail screen.
     *
     * @param ctx Screen context
     */
    public void render(ScreenContext ctx) {
        int screenWidth = ctx.screenWidth();
        int screenHeight = ctx.screenHeight();
        Theme theme = themeRegistry.getActiveTheme();
        ThemeColors colors = new ThemeColors(theme.colors());
        
        // Calculate dimensions
        int windowWidth = Math.min(500, screenWidth - 60);
        int windowHeight = Math.min(450, screenHeight - 60);
        int windowX = (screenWidth - windowWidth) / 2;
        int windowY = (screenHeight - windowHeight) / 2;
        
        // Draw background overlay
        ctx.fillRect(0, 0, screenWidth, screenHeight, 0x80000000);
        
        // Draw window
        renderWindow(ctx, theme, colors, windowX, windowY, windowWidth, windowHeight);
    }
    
    private void renderWindow(ScreenContext ctx, Theme theme, ThemeColors colors, int x, int y, int width, int height) {
        // Background
        if (theme.borders().style().equals("rounded")) {
            ctx.fillRoundedRect(x, y, width, height, theme.borders().radius() * 2, colors.background());
        } else {
            ctx.fillRect(x, y, width, height, colors.background());
        }
        
        // Border
        ctx.drawRect(x, y, width, height, colors.border(), theme.borders().width());
        
        // Header
        int headerHeight = 40;
        renderHeader(ctx, theme, colors, x, y, width, headerHeight);
        
        // Content area with scrolling
        int contentY = y + headerHeight;
        int footerHeight = 50;
        int contentHeight = height - headerHeight - footerHeight;
        renderContent(ctx, theme, colors, x, contentY, width, contentHeight);
        
        // Footer with buttons
        int footerY = y + height - footerHeight;
        renderFooter(ctx, colors, x, footerY, width, footerHeight);
    }
    
    private void renderHeader(ScreenContext ctx, Theme theme, ThemeColors colors, int x, int y, int width, int height) {
        // Quest type icon
        int iconX = x + PADDING;
        int iconY = y + (height - ICON_SIZE) / 2;
        ctx.drawIcon(getQuestTypeIcon(theme), iconX, iconY, ICON_SIZE, ICON_SIZE, getQuestTypeColor(colors));
        
        // Quest title
        int titleX = iconX + ICON_SIZE + PADDING / 2;
        int titleY = y + (height - theme.fonts().titleSize()) / 2;
        ctx.drawText(quest.name().toUpperCase(), titleX, titleY, getQuestTypeColor(colors));
        
        // Close button
        int closeSize = 16;
        int closeX = x + width - PADDING - closeSize;
        int closeY = y + (height - closeSize) / 2;
        boolean closeHovered = ctx.isMouseOver(closeX, closeY, closeSize, closeSize);
        ctx.drawText("×", closeX, closeY, closeHovered ? colors.textPrimary() : colors.textSecondary());
        
        // Pin button
        int pinX = closeX - closeSize - 8;
        int pinY = y + (height - closeSize) / 2;
        boolean pinHovered = ctx.isMouseOver(pinX, pinY, closeSize, closeSize);
        String pinIcon = quest.isPinned() ? "📌" : "📍";
        int pinColor = quest.isPinned() ? colors.questMain() 
            : (pinHovered ? colors.textPrimary() : colors.textSecondary());
        ctx.drawText(pinIcon, pinX, pinY, pinColor);
        
        // Separator
        ctx.fillRect(x, y + height - 1, width, 1, colors.border());
    }
    
    private void renderContent(ScreenContext ctx, Theme theme, ThemeColors colors, int x, int y, int width, int height) {
        ctx.pushScissor(x, y, width, height);
        
        int contentX = x + PADDING;
        int contentWidth = width - PADDING * 2;
        int currentY = y + PADDING - scrollOffset;
        
        // Description
        currentY = renderDescription(ctx, theme, colors, contentX, currentY, contentWidth);
        
        // Separator
        currentY += SECTION_SPACING / 2;
        ctx.fillRect(contentX, currentY, contentWidth, 1, colors.border());
        currentY += SECTION_SPACING / 2;
        
        // Objectives
        currentY = renderObjectives(ctx, theme, colors, contentX, currentY, contentWidth);
        
        // Separator
        currentY += SECTION_SPACING / 2;
        ctx.fillRect(contentX, currentY, contentWidth, 1, colors.border());
        currentY += SECTION_SPACING / 2;
        
        // Rewards
        currentY = renderRewards(ctx, theme, colors, contentX, currentY, contentWidth);
        
        // Separator
        currentY += SECTION_SPACING / 2;
        ctx.fillRect(contentX, currentY, contentWidth, 1, colors.border());
        currentY += SECTION_SPACING / 2;
        
        // Quest info
        currentY = renderQuestInfo(ctx, theme, colors, contentX, currentY, contentWidth);
        
        // Calculate max scroll
        maxScrollOffset = Math.max(0, currentY + scrollOffset - y - height + PADDING);
        
        ctx.popScissor();
    }
    
    private int renderDescription(ScreenContext ctx, Theme theme, ThemeColors colors, int x, int y, int width) {
        // Quote-style description
        String desc = quest.description();
        if (!desc.isEmpty()) {
            ctx.drawText("\"" + desc + "\"", x + 8, y, colors.textSecondary());
            y += theme.fonts().bodySize() * 2 + LINE_SPACING;
        }
        return y;
    }
    
    private int renderObjectives(ScreenContext ctx, Theme theme, ThemeColors colors, int x, int y, int width) {
        // Section header
        ctx.drawText("OBJECTIVES", x, y, colors.textPrimary());
        y += theme.fonts().bodySize() + SECTION_SPACING / 2;
        
        for (TrackedObjective objective : quest.objectives()) {
            // Checkbox
            ctx.drawCheckbox(x, y, objective.isComplete(), true, false);
            
            // Description
            int textX = x + CHECKBOX_SIZE + 8;
            int textColor = objective.isComplete() ? colors.textCompleted() : colors.textPrimary();
            ctx.drawText(objective.description(), textX, y, textColor);
            
            // Progress on same line if has progress
            if (objective.requiredProgress() > 1) {
                String progress = String.format("(%d/%d)", objective.currentProgress(), objective.requiredProgress());
                int progressX = x + width - ctx.textWidth(progress);
                ctx.drawText(progress, progressX, y, colors.textSecondary());
            }
            
            y += CHECKBOX_SIZE + LINE_SPACING;
            
            // Progress bar below if incomplete
            if (!objective.isComplete() && objective.requiredProgress() > 1) {
                int barWidth = width - CHECKBOX_SIZE - 8;
                renderProgressBar(ctx, colors, textX, y, barWidth, PROGRESS_BAR_HEIGHT, objective);
                y += PROGRESS_BAR_HEIGHT + LINE_SPACING;
            }
            
            // Distance indicator
            if (objective.waypoint() != null) {
                String distance = "→ " + objective.waypoint().formatDistance();
                ctx.drawText(distance, textX, y, colors.questTimed());
                y += theme.fonts().smallSize() + LINE_SPACING;
            }
        }
        
        return y;
    }
    
    private void renderProgressBar(ScreenContext ctx, ThemeColors colors, int x, int y, int width, int height, 
            TrackedObjective objective) {
        // Background
        ctx.fillRoundedRect(x, y, width, height, height / 2, colors.progressBackground());
        
        // Fill
        double progress = objective.progressPercent();
        if (progress > 0) {
            int fillWidth = Math.max(height, (int) (width * progress));
            int fillColor = objective.isComplete() ? colors.progressComplete() : colors.progressFill();
            ctx.fillRoundedRect(x, y, fillWidth, height, height / 2, fillColor);
        }
    }
    
    private int renderRewards(ScreenContext ctx, Theme theme, ThemeColors colors, int x, int y, int width) {
        if (details.rewards().isEmpty()) {
            return y;
        }
        
        // Section header
        ctx.drawText("REWARDS", x, y, colors.textPrimary());
        y += theme.fonts().bodySize() + SECTION_SPACING / 2;
        
        // Render rewards in a row
        int rewardX = x;
        for (Reward reward : details.rewards()) {
            String rewardText = reward.icon() + " " + reward.formattedAmount();
            ctx.drawText(rewardText, rewardX, y, reward.color());
            rewardX += ctx.textWidth(rewardText) + PADDING;
        }
        
        y += theme.fonts().bodySize() + LINE_SPACING;
        return y;
    }
    
    private int renderQuestInfo(ScreenContext ctx, Theme theme, ThemeColors colors, int x, int y, int width) {
        // Quest giver
        if (!details.questGiver().isEmpty()) {
            ctx.drawText("Quest Giver: ", x, y, colors.textSecondary());
            ctx.drawText(details.questGiver(), x + ctx.textWidth("Quest Giver: "), y, colors.textPrimary());
            y += theme.fonts().bodySize() + LINE_SPACING;
        }
        
        // Recommended level
        if (details.recommendedLevel() > 0) {
            ctx.drawText("Recommended Level: ", x, y, colors.textSecondary());
            ctx.drawText(String.valueOf(details.recommendedLevel()), 
                x + ctx.textWidth("Recommended Level: "), y, colors.textPrimary());
            y += theme.fonts().bodySize() + LINE_SPACING;
        }
        
        // Category
        if (!details.category().isEmpty()) {
            ctx.drawText("Category: ", x, y, colors.textSecondary());
            ctx.drawText(details.category(), x + ctx.textWidth("Category: "), y, colors.textPrimary());
            y += theme.fonts().bodySize() + LINE_SPACING;
        }
        
        // Timer if timed
        if (quest.isTimed()) {
            long seconds = quest.timeRemaining().toSeconds();
            String timeText = formatTime(seconds);
            int timeColor = getTimerColor(colors, seconds);
            ctx.drawText("Time Remaining: ", x, y, colors.textSecondary());
            ctx.drawText(timeText, x + ctx.textWidth("Time Remaining: "), y, timeColor);
            y += theme.fonts().bodySize() + LINE_SPACING;
        }
        
        return y;
    }
    
    private void renderFooter(ScreenContext ctx, ThemeColors colors, int x, int y, int width, int height) {
        // Separator
        ctx.fillRect(x, y, width, 1, colors.border());
        
        int buttonY = y + (height - BUTTON_HEIGHT) / 2;
        int buttonSpacing = 8;
        int totalButtonWidth = BUTTON_WIDTH * 3 + buttonSpacing * 2;
        int buttonX = x + (width - totalButtonWidth) / 2;
        
        // Show on Map button
        boolean mapHovered = ctx.isMouseOver(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        ctx.drawButton(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, "Show on Map", true, mapHovered);
        buttonX += BUTTON_WIDTH + buttonSpacing;
        
        // Share Quest button
        boolean shareHovered = ctx.isMouseOver(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        boolean shareEnabled = details.canShare();
        ctx.drawButton(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, "Share Quest", shareEnabled, shareHovered);
        buttonX += BUTTON_WIDTH + buttonSpacing;
        
        // Abandon Quest button
        boolean abandonHovered = ctx.isMouseOver(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        boolean abandonEnabled = details.canAbandon();
        ctx.drawButton(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, "Abandon", abandonEnabled, abandonHovered);
    }
    
    /**
     * Handle mouse click.
     *
     * @param ctx Screen context
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param button Mouse button (0 = left, 1 = right, 2 = middle)
     * @return true if click was handled
     */
    public boolean onClick(ScreenContext ctx, int mouseX, int mouseY, int button) {
        int screenWidth = ctx.screenWidth();
        int screenHeight = ctx.screenHeight();
        Theme theme = themeRegistry.getActiveTheme();
        
        int windowWidth = Math.min(500, screenWidth - 60);
        int windowHeight = Math.min(450, screenHeight - 60);
        int windowX = (screenWidth - windowWidth) / 2;
        int windowY = (screenHeight - windowHeight) / 2;
        
        // Check if outside window - close
        if (mouseX < windowX || mouseX >= windowX + windowWidth 
            || mouseY < windowY || mouseY >= windowY + windowHeight) {
            close(ctx);
            return true;
        }
        
        int headerHeight = 40;
        int footerHeight = 50;
        
        // Check close button
        int closeSize = 16;
        int closeX = windowX + windowWidth - PADDING - closeSize;
        int closeY = windowY + (headerHeight - closeSize) / 2;
        if (ctx.isMouseOver(closeX, closeY, closeSize, closeSize)) {
            close(ctx);
            ctx.playClickSound();
            return true;
        }
        
        // Check pin button
        int pinX = closeX - closeSize - 8;
        int pinY = windowY + (headerHeight - closeSize) / 2;
        if (ctx.isMouseOver(pinX, pinY, closeSize, closeSize)) {
            if (onPinToggle != null) {
                onPinToggle.accept(quest);
            }
            ctx.playClickSound();
            return true;
        }
        
        // Check footer buttons
        int buttonY = windowY + windowHeight - footerHeight + (footerHeight - BUTTON_HEIGHT) / 2;
        int buttonSpacing = 8;
        int totalButtonWidth = BUTTON_WIDTH * 3 + buttonSpacing * 2;
        int buttonX = windowX + (windowWidth - totalButtonWidth) / 2;
        
        // Show on Map
        if (ctx.isMouseOver(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT)) {
            if (onShowOnMap != null) {
                onShowOnMap.accept(quest);
            }
            ctx.playClickSound();
            return true;
        }
        buttonX += BUTTON_WIDTH + buttonSpacing;
        
        // Share Quest
        if (ctx.isMouseOver(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT) && details.canShare()) {
            if (onShareQuest != null) {
                onShareQuest.accept(quest);
            }
            ctx.playClickSound();
            return true;
        }
        buttonX += BUTTON_WIDTH + buttonSpacing;
        
        // Abandon Quest
        if (ctx.isMouseOver(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT) && details.canAbandon()) {
            if (onAbandonQuest != null) {
                onAbandonQuest.accept(quest);
            }
            ctx.playClickSound();
            return true;
        }
        
        return false;
    }
    
    /**
     * Handle mouse scroll.
     *
     * @param amount Scroll amount (positive = up, negative = down)
     */
    public void onScroll(double amount) {
        int scrollDelta = (int) (-amount * 20);
        scrollOffset = Math.max(0, Math.min(maxScrollOffset, scrollOffset + scrollDelta));
    }
    
    /**
     * Handle key press.
     *
     * @param ctx Screen context
     * @param keyCode Key code
     * @return true if key was handled
     */
    public boolean onKeyPress(ScreenContext ctx, int keyCode) {
        // Escape to close
        if (keyCode == 256) { // GLFW_KEY_ESCAPE
            close(ctx);
            return true;
        }
        return false;
    }
    
    private void close(ScreenContext ctx) {
        if (onClose != null) {
            onClose.run();
        }
        ctx.closeScreen();
    }
    
    private String getQuestTypeIcon(Theme theme) {
        return switch (quest.type()) {
            case MAIN -> theme.icons().questMain();
            case SIDE -> theme.icons().questSide();
            case TIMED -> theme.icons().questTimed();
            case DAILY -> theme.icons().questDaily();
            case GUILD -> theme.icons().questGuild();
            case WEEKLY -> theme.icons().questWeekly();
            case EVENT -> theme.icons().questEvent();
        };
    }
    
    private int getQuestTypeColor(ThemeColors colors) {
        return switch (quest.type()) {
            case MAIN -> colors.questMain();
            case SIDE -> colors.questSide();
            case TIMED -> colors.questTimed();
            case DAILY -> colors.questDaily();
            case GUILD -> colors.questGuild();
            case WEEKLY -> colors.questWeekly();
            case EVENT -> colors.questEvent();
        };
    }
    
    private int getTimerColor(ThemeColors colors, long seconds) {
        if (seconds < 60) {
            return colors.timerCritical();
        } else if (seconds < 300) {
            return colors.timerWarning();
        } else {
            return colors.timerNormal();
        }
    }
    
    private String formatTime(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }
    
    /**
     * Get the quest being displayed.
     *
     * @return The quest
     */
    public TrackedQuest getQuest() {
        return quest;
    }
    
    /**
     * Get the quest details.
     *
     * @return Quest details
     */
    public QuestDetails getDetails() {
        return details;
    }
}
