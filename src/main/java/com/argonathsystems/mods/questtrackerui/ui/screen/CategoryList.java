package com.argonathsystems.mods.questtrackerui.ui.screen;

import com.argonathsystems.mods.questtrackerui.api.QuestType;
import com.argonathsystems.mods.questtrackerui.api.TrackedQuest;
import com.argonathsystems.mods.questtrackerui.theme.Theme;
import com.argonathsystems.mods.questtrackerui.theme.ThemeColors;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A collapsible category list for grouping quests.
 */
public class CategoryList {
    
    /**
     * Header height in pixels.
     */
    public static final int HEADER_HEIGHT = 24;
    
    private static final int PADDING = 8;
    private static final int INDENT = 16;
    private static final int ARROW_SIZE = 8;
    
    private final QuestType category;
    private final String displayName;
    private final Theme theme;
    private final ThemeColors colors;
    private final List<QuestListEntry> entries;
    private boolean expanded;
    private boolean compactMode;
    
    private Consumer<TrackedQuest> onQuestSelect;
    private Consumer<TrackedQuest> onQuestPin;
    
    /**
     * Create a new category list.
     *
     * @param category Quest type category
     * @param displayName Display name for the category
     * @param theme Current theme
     */
    public CategoryList(QuestType category, String displayName, Theme theme) {
        this.category = category;
        this.displayName = displayName;
        this.theme = theme;
        this.colors = new ThemeColors(theme.colors());
        this.entries = new ArrayList<>();
        this.expanded = true;
        this.compactMode = false;
    }
    
    /**
     * Set the callback for quest selection.
     *
     * @param onQuestSelect Selection callback
     * @return This for chaining
     */
    public CategoryList onQuestSelect(Consumer<TrackedQuest> onQuestSelect) {
        this.onQuestSelect = onQuestSelect;
        updateEntryCallbacks();
        return this;
    }
    
    /**
     * Set the callback for quest pinning.
     *
     * @param onQuestPin Pin callback
     * @return This for chaining
     */
    public CategoryList onQuestPin(Consumer<TrackedQuest> onQuestPin) {
        this.onQuestPin = onQuestPin;
        updateEntryCallbacks();
        return this;
    }
    
    /**
     * Set compact mode.
     *
     * @param compact Whether to use compact display
     * @return This for chaining
     */
    public CategoryList setCompact(boolean compact) {
        this.compactMode = compact;
        rebuildEntries();
        return this;
    }
    
    /**
     * Set the quests for this category.
     *
     * @param quests List of quests
     */
    public void setQuests(List<TrackedQuest> quests) {
        entries.clear();
        for (TrackedQuest quest : quests) {
            QuestListEntry entry = new QuestListEntry(quest, theme, compactMode);
            entry.onSelect(onQuestSelect);
            entry.onPin(onQuestPin);
            entries.add(entry);
        }
    }
    
    private void updateEntryCallbacks() {
        for (QuestListEntry entry : entries) {
            entry.onSelect(onQuestSelect);
            entry.onPin(onQuestPin);
        }
    }
    
    private void rebuildEntries() {
        List<TrackedQuest> quests = entries.stream()
            .map(QuestListEntry::getQuest)
            .toList();
        setQuests(quests);
    }
    
    /**
     * Get the total height of this category list.
     *
     * @return Height in pixels
     */
    public int getHeight() {
        if (!expanded || entries.isEmpty()) {
            return HEADER_HEIGHT;
        }
        int height = HEADER_HEIGHT;
        for (QuestListEntry entry : entries) {
            height += entry.getHeight() + 2;
        }
        return height;
    }
    
    /**
     * Get the quest type for this category.
     *
     * @return Quest type
     */
    public QuestType getCategory() {
        return category;
    }
    
    /**
     * Get the number of quests in this category.
     *
     * @return Quest count
     */
    public int getQuestCount() {
        return entries.size();
    }
    
    /**
     * Check if this category is empty.
     *
     * @return true if no quests
     */
    public boolean isEmpty() {
        return entries.isEmpty();
    }
    
    /**
     * Toggle expanded state.
     */
    public void toggleExpanded() {
        expanded = !expanded;
    }
    
    /**
     * Check if expanded.
     *
     * @return true if expanded
     */
    public boolean isExpanded() {
        return expanded;
    }
    
    /**
     * Set expanded state.
     *
     * @param expanded Whether to expand
     */
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
    
    /**
     * Render the category list.
     *
     * @param ctx Screen context
     * @param x Left edge
     * @param y Top edge
     * @param width Available width
     * @param selectedQuest Currently selected quest (or null)
     */
    public void render(ScreenContext ctx, int x, int y, int width, TrackedQuest selectedQuest) {
        // Render header
        renderHeader(ctx, x, y, width);
        
        // Render entries if expanded
        if (expanded && !entries.isEmpty()) {
            int entryY = y + HEADER_HEIGHT;
            int entryX = x + INDENT;
            int entryWidth = width - INDENT;
            
            for (QuestListEntry entry : entries) {
                boolean selected = selectedQuest != null && selectedQuest.id().equals(entry.getQuest().id());
                boolean hovered = ctx.isMouseOver(entryX, entryY, entryWidth, entry.getHeight());
                entry.render(ctx, entryX, entryY, entryWidth, selected, hovered);
                entryY += entry.getHeight() + 2;
            }
        }
    }
    
    private void renderHeader(ScreenContext ctx, int x, int y, int width) {
        boolean hovered = ctx.isMouseOver(x, y, width, HEADER_HEIGHT);
        
        // Background on hover
        if (hovered) {
            ctx.fillRect(x, y, width, HEADER_HEIGHT, withAlpha(colors.border(), 64));
        }
        
        // Arrow indicator
        int arrowX = x + PADDING;
        int arrowY = y + (HEADER_HEIGHT - ARROW_SIZE) / 2;
        String arrow = expanded ? "▼" : "▶";
        ctx.drawText(arrow, arrowX, arrowY, colors.textSecondary());
        
        // Category name
        int textX = arrowX + ARROW_SIZE + PADDING;
        int textY = y + (HEADER_HEIGHT - theme.fonts().bodySize()) / 2;
        String text = String.format("%s (%d)", displayName, entries.size());
        ctx.drawText(text, textX, textY, colors.textPrimary());
    }
    
    /**
     * Handle mouse click.
     *
     * @param ctx Screen context
     * @param x Left edge
     * @param y Top edge
     * @param width Available width
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param button Mouse button
     * @return true if click was handled
     */
    public boolean onClick(ScreenContext ctx, int x, int y, int width, int mouseX, int mouseY, int button) {
        // Check header click
        if (mouseY >= y && mouseY < y + HEADER_HEIGHT && mouseX >= x && mouseX < x + width) {
            if (button == 0) {
                toggleExpanded();
                ctx.playClickSound();
                return true;
            }
        }
        
        // Check entry clicks if expanded
        if (expanded && !entries.isEmpty()) {
            int entryY = y + HEADER_HEIGHT;
            int entryX = x + INDENT;
            int entryWidth = width - INDENT;
            
            for (QuestListEntry entry : entries) {
                if (entry.onClick(ctx, entryX, entryY, entryWidth, mouseX, mouseY, button)) {
                    return true;
                }
                entryY += entry.getHeight() + 2;
            }
        }
        
        return false;
    }
    
    private int withAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }
}
