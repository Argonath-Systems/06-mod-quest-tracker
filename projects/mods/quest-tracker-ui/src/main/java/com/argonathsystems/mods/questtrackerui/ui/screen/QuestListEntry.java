package com.argonathsystems.mods.questtrackerui.ui.screen;

import com.argonathsystems.mods.questtrackerui.api.QuestType;
import com.argonathsystems.mods.questtrackerui.api.TrackedQuest;
import com.argonathsystems.mods.questtrackerui.theme.Theme;
import com.argonathsystems.mods.questtrackerui.theme.ThemeColors;

import java.util.function.Consumer;

/**
 * Renders a single quest entry in the quest list.
 */
public class QuestListEntry {
    
    /**
     * Default entry height in pixels.
     */
    public static final int ENTRY_HEIGHT = 52;
    
    /**
     * Compact entry height in pixels.
     */
    public static final int COMPACT_HEIGHT = 28;
    
    private static final int PADDING = 8;
    private static final int ICON_SIZE = 16;
    private static final int PIN_ICON_SIZE = 12;
    private static final int PROGRESS_BAR_HEIGHT = 6;
    
    private final TrackedQuest quest;
    private final Theme theme;
    private final ThemeColors colors;
    private final boolean compact;
    
    private Consumer<TrackedQuest> onSelect;
    private Consumer<TrackedQuest> onPin;
    
    /**
     * Create a new quest list entry.
     *
     * @param quest The quest to display
     * @param theme Current theme
     * @param compact Whether to use compact display
     */
    public QuestListEntry(TrackedQuest quest, Theme theme, boolean compact) {
        this.quest = quest;
        this.theme = theme;
        this.colors = new ThemeColors(theme.colors());
        this.compact = compact;
    }
    
    /**
     * Set callback for when entry is selected.
     *
     * @param onSelect Selection callback
     * @return This entry for chaining
     */
    public QuestListEntry onSelect(Consumer<TrackedQuest> onSelect) {
        this.onSelect = onSelect;
        return this;
    }
    
    /**
     * Set callback for when pin button is clicked.
     *
     * @param onPin Pin callback
     * @return This entry for chaining
     */
    public QuestListEntry onPin(Consumer<TrackedQuest> onPin) {
        this.onPin = onPin;
        return this;
    }
    
    /**
     * Get the height of this entry.
     *
     * @return Entry height in pixels
     */
    public int getHeight() {
        return compact ? COMPACT_HEIGHT : ENTRY_HEIGHT;
    }
    
    /**
     * Render the quest entry.
     *
     * @param ctx Screen context
     * @param x Left edge
     * @param y Top edge
     * @param width Available width
     * @param selected Whether this entry is selected
     * @param hovered Whether this entry is hovered
     */
    public void render(ScreenContext ctx, int x, int y, int width, boolean selected, boolean hovered) {
        int height = getHeight();
        
        // Background
        int bgColor = selected 
            ? colors.progressFill() 
            : (hovered ? withAlpha(colors.border(), 128) : colors.background());
        
        if (theme.borders().style().equals("rounded")) {
            ctx.fillRoundedRect(x, y, width, height, theme.borders().radius(), bgColor);
        } else {
            ctx.fillRect(x, y, width, height, bgColor);
        }
        
        // Border if selected
        if (selected) {
            ctx.drawRect(x, y, width, height, colors.border(), theme.borders().width());
        }
        
        // Quest type icon
        int iconX = x + PADDING;
        int iconY = y + (height - ICON_SIZE) / 2;
        ctx.drawIcon(getQuestTypeIcon(), iconX, iconY, ICON_SIZE, ICON_SIZE, getQuestTypeColor());
        
        // Quest name
        int textX = iconX + ICON_SIZE + PADDING;
        int textY = compact ? y + (height - theme.fonts().bodySize()) / 2 : y + PADDING;
        int textColor = quest.isComplete() ? colors.textCompleted() : colors.textPrimary();
        ctx.drawText(quest.name(), textX, textY, textColor);
        
        if (!compact) {
            // Progress text
            String progressText = String.format("%d/%d objectives", 
                quest.completedObjectiveCount(), quest.objectives().size());
            int progressY = textY + theme.fonts().bodySize() + 4;
            ctx.drawText(progressText, textX, progressY, colors.textSecondary());
            
            // Progress bar
            int barX = textX;
            int barY = progressY + theme.fonts().smallSize() + 4;
            int barWidth = width - barX - PADDING - (quest.isPinned() ? PIN_ICON_SIZE + PADDING : 0) - PADDING;
            renderProgressBar(ctx, barX, barY, barWidth, PROGRESS_BAR_HEIGHT);
            
            // Timer if timed
            if (quest.isTimed()) {
                String timeText = formatTime(quest.timeRemaining().toSeconds());
                int timeColor = getTimerColor(quest.timeRemaining().toSeconds());
                int timeWidth = ctx.textWidth(timeText);
                ctx.drawText(timeText, x + width - PADDING - timeWidth - (quest.isPinned() ? PIN_ICON_SIZE + PADDING : 0), 
                    textY, timeColor);
            }
        }
        
        // Pin indicator
        if (quest.isPinned()) {
            int pinX = x + width - PADDING - PIN_ICON_SIZE;
            int pinY = y + (height - PIN_ICON_SIZE) / 2;
            ctx.drawIcon("pin", pinX, pinY, PIN_ICON_SIZE, PIN_ICON_SIZE, colors.questMain());
        }
    }
    
    /**
     * Handle mouse click on this entry.
     *
     * @param ctx Screen context
     * @param x Entry left edge
     * @param y Entry top edge
     * @param width Entry width
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param button Mouse button (0 = left, 1 = right, 2 = middle)
     * @return true if click was handled
     */
    public boolean onClick(ScreenContext ctx, int x, int y, int width, int mouseX, int mouseY, int button) {
        int height = getHeight();
        
        // Check if within bounds
        if (mouseX < x || mouseX >= x + width || mouseY < y || mouseY >= y + height) {
            return false;
        }
        
        // Check pin button click (middle mouse or click on pin icon)
        if (button == 2) {
            if (onPin != null) {
                onPin.accept(quest);
                ctx.playClickSound();
            }
            return true;
        }
        
        // Check if clicking pin icon
        int pinX = x + width - PADDING - PIN_ICON_SIZE;
        int pinY = y + (height - PIN_ICON_SIZE) / 2;
        if (mouseX >= pinX && mouseX < pinX + PIN_ICON_SIZE 
            && mouseY >= pinY && mouseY < pinY + PIN_ICON_SIZE) {
            if (onPin != null) {
                onPin.accept(quest);
                ctx.playClickSound();
            }
            return true;
        }
        
        // Left click selects
        if (button == 0 && onSelect != null) {
            onSelect.accept(quest);
            ctx.playClickSound();
            return true;
        }
        
        return false;
    }
    
    private void renderProgressBar(ScreenContext ctx, int x, int y, int width, int height) {
        // Background
        ctx.fillRoundedRect(x, y, width, height, height / 2, colors.progressBackground());
        
        // Fill
        double progress = quest.progressPercent();
        if (progress > 0) {
            int fillWidth = Math.max(height, (int) (width * progress));
            int fillColor = quest.isComplete() ? colors.progressComplete() : colors.progressFill();
            ctx.fillRoundedRect(x, y, fillWidth, height, height / 2, fillColor);
        }
    }
    
    private String getQuestTypeIcon() {
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
    
    private int getQuestTypeColor() {
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
    
    private int getTimerColor(long seconds) {
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
    
    private int withAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }
    
    /**
     * Get the quest for this entry.
     *
     * @return The tracked quest
     */
    public TrackedQuest getQuest() {
        return quest;
    }
}
