package com.argonathsystems.mods.questtrackerui.ui.screen;

import com.argonathsystems.mods.questtrackerui.api.QuestDataProvider;
import com.argonathsystems.mods.questtrackerui.api.QuestType;
import com.argonathsystems.mods.questtrackerui.api.TrackedQuest;
import com.argonathsystems.mods.questtrackerui.ProviderRegistry;
import com.argonathsystems.mods.questtrackerui.theme.Theme;
import com.argonathsystems.mods.questtrackerui.theme.ThemeColors;
import com.argonathsystems.mods.questtrackerui.theme.ThemeRegistry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Main quest list screen showing all quests organized by category.
 * 
 * <p>Implements QT-008 from the specification.
 */
public class QuestListScreen {
    
    private static final int HEADER_HEIGHT = 40;
    private static final int FOOTER_HEIGHT = 30;
    private static final int PADDING = 12;
    private static final int SCROLLBAR_WIDTH = 8;
    private static final int TAB_HEIGHT = 28;
    private static final int TAB_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_WIDTH = 80;
    
    /**
     * Tab options for the quest list.
     */
    public enum Tab {
        ACTIVE("Active"),
        HISTORY("History");
        
        private final String displayName;
        
        Tab(String displayName) {
            this.displayName = displayName;
        }
        
        public String displayName() {
            return displayName;
        }
    }
    
    private final ThemeRegistry themeRegistry;
    private final ProviderRegistry providerRegistry;
    private final UUID playerId;
    
    // UI State
    private Tab currentTab;
    private QuestSortOption sortOption;
    private QuestFilterOption filterOption;
    private TrackedQuest selectedQuest;
    private int scrollOffset;
    private int maxScrollOffset;
    private boolean compactMode;
    
    // Category lists
    private final Map<QuestType, CategoryList> categoryLists;
    private List<CategoryList> visibleCategories;
    
    // Callbacks
    private Consumer<TrackedQuest> onQuestSelected;
    private Runnable onClose;
    
    /**
     * Create a new quest list screen.
     *
     * @param themeRegistry Theme registry
     * @param providerRegistry Quest provider registry
     * @param playerId Current player's ID
     */
    public QuestListScreen(ThemeRegistry themeRegistry, ProviderRegistry providerRegistry, UUID playerId) {
        this.themeRegistry = themeRegistry;
        this.providerRegistry = providerRegistry;
        this.playerId = playerId;
        
        this.currentTab = Tab.ACTIVE;
        this.sortOption = QuestSortOption.PRIORITY;
        this.filterOption = QuestFilterOption.ALL;
        this.scrollOffset = 0;
        this.compactMode = false;
        
        this.categoryLists = new EnumMap<>(QuestType.class);
        this.visibleCategories = new ArrayList<>();
        
        initializeCategoryLists();
        refreshQuests();
    }
    
    private void initializeCategoryLists() {
        Theme theme = themeRegistry.getActiveTheme();
        
        for (QuestType type : QuestType.values()) {
            CategoryList category = new CategoryList(type, type.displayName(), theme);
            category.onQuestSelect(quest -> {
                selectedQuest = quest;
                if (onQuestSelected != null) {
                    onQuestSelected.accept(quest);
                }
            });
            category.onQuestPin(quest -> {
                togglePin(quest);
            });
            categoryLists.put(type, category);
        }
    }
    
    /**
     * Refresh quests from all providers.
     */
    public void refreshQuests() {
        // Gather all quests from all providers
        List<TrackedQuest> allQuests = new ArrayList<>();
        for (QuestDataProvider provider : providerRegistry.getProviders()) {
            List<TrackedQuest> quests = currentTab == Tab.ACTIVE 
                ? provider.getTrackedQuests(playerId)
                : provider.getCompletedQuests(playerId);
            allQuests.addAll(quests);
        }
        
        // Apply filter
        List<TrackedQuest> filtered = allQuests.stream()
            .filter(filterOption.predicate())
            .toList();
        
        // Sort
        List<TrackedQuest> sorted = new ArrayList<>(filtered);
        sorted.sort(sortOption.comparator());
        
        // Group by category
        Map<QuestType, List<TrackedQuest>> byType = new EnumMap<>(QuestType.class);
        for (QuestType type : QuestType.values()) {
            byType.put(type, new ArrayList<>());
        }
        for (TrackedQuest quest : sorted) {
            byType.get(quest.type()).add(quest);
        }
        
        // Update category lists
        visibleCategories.clear();
        for (QuestType type : QuestType.values()) {
            CategoryList category = categoryLists.get(type);
            category.setQuests(byType.get(type));
            if (!category.isEmpty()) {
                visibleCategories.add(category);
            }
        }
        
        // Reset scroll if needed
        calculateMaxScroll();
    }
    
    private void calculateMaxScroll() {
        int totalHeight = 0;
        for (CategoryList category : visibleCategories) {
            totalHeight += category.getHeight() + 4;
        }
        // This will be adjusted when render dimensions are known
        maxScrollOffset = Math.max(0, totalHeight);
    }
    
    /**
     * Set the callback for when a quest is selected.
     *
     * @param onQuestSelected Selection callback
     */
    public void setOnQuestSelected(Consumer<TrackedQuest> onQuestSelected) {
        this.onQuestSelected = onQuestSelected;
    }
    
    /**
     * Set the callback for when the screen is closed.
     *
     * @param onClose Close callback
     */
    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }
    
    /**
     * Render the quest list screen.
     *
     * @param ctx Screen context
     */
    public void render(ScreenContext ctx) {
        int screenWidth = ctx.screenWidth();
        int screenHeight = ctx.screenHeight();
        Theme theme = themeRegistry.getActiveTheme();
        ThemeColors colors = new ThemeColors(theme.colors());
        
        // Calculate dimensions
        int windowWidth = Math.min(600, screenWidth - 40);
        int windowHeight = Math.min(500, screenHeight - 40);
        int windowX = (screenWidth - windowWidth) / 2;
        int windowY = (screenHeight - windowHeight) / 2;
        
        // Draw background overlay
        ctx.fillRect(0, 0, screenWidth, screenHeight, 0x80000000);
        
        // Draw window background
        renderWindowBackground(ctx, theme, colors, windowX, windowY, windowWidth, windowHeight);
        
        // Draw header
        renderHeader(ctx, theme, colors, windowX, windowY, windowWidth);
        
        // Draw tabs
        renderTabs(ctx, theme, colors, windowX, windowY + HEADER_HEIGHT, windowWidth);
        
        // Draw quest list
        int listY = windowY + HEADER_HEIGHT + TAB_HEIGHT;
        int listHeight = windowHeight - HEADER_HEIGHT - TAB_HEIGHT - FOOTER_HEIGHT;
        renderQuestList(ctx, theme, colors, windowX, listY, windowWidth, listHeight);
        
        // Draw footer
        renderFooter(ctx, theme, colors, windowX, windowY + windowHeight - FOOTER_HEIGHT, windowWidth);
    }
    
    private void renderWindowBackground(ScreenContext ctx, Theme theme, ThemeColors colors, int x, int y, int width, int height) {
        // Background
        if (theme.borders().style().equals("rounded")) {
            ctx.fillRoundedRect(x, y, width, height, theme.borders().radius() * 2, colors.background());
        } else {
            ctx.fillRect(x, y, width, height, colors.background());
        }
        
        // Border
        ctx.drawRect(x, y, width, height, colors.border(), theme.borders().width());
    }
    
    private void renderHeader(ScreenContext ctx, Theme theme, ThemeColors colors, int x, int y, int width) {
        // Title
        String title = "QUEST LOG";
        int titleX = x + PADDING;
        int titleY = y + (HEADER_HEIGHT - theme.fonts().titleSize()) / 2;
        ctx.drawText(title, titleX, titleY, colors.textPrimary());
        
        // Close button
        int closeSize = 16;
        int closeX = x + width - PADDING - closeSize;
        int closeY = y + (HEADER_HEIGHT - closeSize) / 2;
        boolean closeHovered = ctx.isMouseOver(closeX, closeY, closeSize, closeSize);
        ctx.drawText("×", closeX, closeY, closeHovered ? colors.textPrimary() : colors.textSecondary());
    }
    
    private void renderTabs(ScreenContext ctx, Theme theme, ThemeColors colors, int x, int y, int width) {
        int tabX = x + PADDING;
        
        for (Tab tab : Tab.values()) {
            boolean selected = tab == currentTab;
            boolean hovered = ctx.isMouseOver(tabX, y, TAB_WIDTH, TAB_HEIGHT);
            
            // Tab background
            int bgColor = selected ? colors.progressFill() 
                : (hovered ? withAlpha(colors.border(), 128) : 0);
            if (bgColor != 0) {
                ctx.fillRect(tabX, y, TAB_WIDTH, TAB_HEIGHT, bgColor);
            }
            
            // Tab text
            int textColor = selected ? colors.textPrimary() : colors.textSecondary();
            int textX = tabX + (TAB_WIDTH - ctx.textWidth(tab.displayName())) / 2;
            int textY = y + (TAB_HEIGHT - theme.fonts().bodySize()) / 2;
            ctx.drawText(tab.displayName(), textX, textY, textColor);
            
            // Underline if selected
            if (selected) {
                ctx.fillRect(tabX, y + TAB_HEIGHT - 2, TAB_WIDTH, 2, colors.questMain());
            }
            
            tabX += TAB_WIDTH + 4;
        }
        
        // Separator line
        ctx.fillRect(x, y + TAB_HEIGHT - 1, width, 1, colors.border());
    }
    
    private void renderQuestList(ScreenContext ctx, Theme theme, ThemeColors colors, int x, int y, int width, int height) {
        int contentWidth = width - PADDING * 2 - SCROLLBAR_WIDTH;
        int contentX = x + PADDING;
        
        // Calculate max scroll
        int totalContentHeight = 0;
        for (CategoryList category : visibleCategories) {
            totalContentHeight += category.getHeight() + 4;
        }
        maxScrollOffset = Math.max(0, totalContentHeight - height);
        
        // Enable scissor for clipping
        ctx.pushScissor(x, y, width, height);
        
        // Render categories
        int categoryY = y - scrollOffset;
        for (CategoryList category : visibleCategories) {
            category.render(ctx, contentX, categoryY, contentWidth, selectedQuest);
            categoryY += category.getHeight() + 4;
        }
        
        ctx.popScissor();
        
        // Render scrollbar if needed
        if (maxScrollOffset > 0) {
            double scrollPercent = (double) scrollOffset / maxScrollOffset;
            double visiblePercent = (double) height / totalContentHeight;
            ctx.drawScrollbar(x + width - SCROLLBAR_WIDTH - PADDING / 2, y, height, scrollPercent, visiblePercent);
        }
        
        // Empty state
        if (visibleCategories.isEmpty()) {
            String emptyText = filterOption == QuestFilterOption.ALL 
                ? "No quests available" 
                : "No quests match filter";
            int textX = x + (width - ctx.textWidth(emptyText)) / 2;
            int textY = y + height / 2;
            ctx.drawText(emptyText, textX, textY, colors.textSecondary());
        }
    }
    
    private void renderFooter(ScreenContext ctx, Theme theme, ThemeColors colors, int x, int y, int width) {
        // Separator line
        ctx.fillRect(x, y, width, 1, colors.border());
        
        // Quest count
        int totalQuests = visibleCategories.stream().mapToInt(CategoryList::getQuestCount).sum();
        int maxQuests = 25; // Configurable
        String countText = String.format("Active: %d/%d", totalQuests, maxQuests);
        ctx.drawText(countText, x + PADDING, y + (FOOTER_HEIGHT - theme.fonts().smallSize()) / 2, 
            colors.textSecondary());
        
        // Sort button
        int buttonX = x + width - PADDING - BUTTON_WIDTH;
        boolean sortHovered = ctx.isMouseOver(buttonX, y + 5, BUTTON_WIDTH, BUTTON_HEIGHT);
        ctx.drawButton(buttonX, y + 5, BUTTON_WIDTH, BUTTON_HEIGHT, 
            "Sort: " + sortOption.displayName(), true, sortHovered);
        
        // Filter button
        buttonX -= BUTTON_WIDTH + 8;
        boolean filterHovered = ctx.isMouseOver(buttonX, y + 5, BUTTON_WIDTH, BUTTON_HEIGHT);
        ctx.drawButton(buttonX, y + 5, BUTTON_WIDTH, BUTTON_HEIGHT,
            "Filter: " + filterOption.displayName(), true, filterHovered);
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
        
        int windowWidth = Math.min(600, screenWidth - 40);
        int windowHeight = Math.min(500, screenHeight - 40);
        int windowX = (screenWidth - windowWidth) / 2;
        int windowY = (screenHeight - windowHeight) / 2;
        
        // Check if outside window - close
        if (mouseX < windowX || mouseX >= windowX + windowWidth 
            || mouseY < windowY || mouseY >= windowY + windowHeight) {
            if (onClose != null) {
                onClose.run();
            }
            ctx.closeScreen();
            return true;
        }
        
        // Check close button
        int closeSize = 16;
        int closeX = windowX + windowWidth - PADDING - closeSize;
        int closeY = windowY + (HEADER_HEIGHT - closeSize) / 2;
        if (ctx.isMouseOver(closeX, closeY, closeSize, closeSize)) {
            if (onClose != null) {
                onClose.run();
            }
            ctx.closeScreen();
            ctx.playClickSound();
            return true;
        }
        
        // Check tabs
        int tabX = windowX + PADDING;
        int tabY = windowY + HEADER_HEIGHT;
        for (Tab tab : Tab.values()) {
            if (ctx.isMouseOver(tabX, tabY, TAB_WIDTH, TAB_HEIGHT)) {
                if (button == 0 && currentTab != tab) {
                    currentTab = tab;
                    refreshQuests();
                    ctx.playClickSound();
                }
                return true;
            }
            tabX += TAB_WIDTH + 4;
        }
        
        // Check footer buttons
        int footerY = windowY + windowHeight - FOOTER_HEIGHT;
        int buttonX = windowX + windowWidth - PADDING - BUTTON_WIDTH;
        if (ctx.isMouseOver(buttonX, footerY + 5, BUTTON_WIDTH, BUTTON_HEIGHT)) {
            sortOption = sortOption.next();
            refreshQuests();
            ctx.playClickSound();
            return true;
        }
        
        buttonX -= BUTTON_WIDTH + 8;
        if (ctx.isMouseOver(buttonX, footerY + 5, BUTTON_WIDTH, BUTTON_HEIGHT)) {
            filterOption = filterOption.next();
            refreshQuests();
            ctx.playClickSound();
            return true;
        }
        
        // Check quest list
        int listY = windowY + HEADER_HEIGHT + TAB_HEIGHT;
        int listHeight = windowHeight - HEADER_HEIGHT - TAB_HEIGHT - FOOTER_HEIGHT;
        int contentX = windowX + PADDING;
        int contentWidth = windowWidth - PADDING * 2 - SCROLLBAR_WIDTH;
        
        if (mouseY >= listY && mouseY < listY + listHeight) {
            int categoryY = listY - scrollOffset;
            for (CategoryList category : visibleCategories) {
                if (category.onClick(ctx, contentX, categoryY, contentWidth, mouseX, mouseY, button)) {
                    return true;
                }
                categoryY += category.getHeight() + 4;
            }
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
            if (onClose != null) {
                onClose.run();
            }
            ctx.closeScreen();
            return true;
        }
        return false;
    }
    
    private void togglePin(TrackedQuest quest) {
        for (QuestDataProvider provider : providerRegistry.getProviders()) {
            // Try to pin/unpin through each provider
            try {
                provider.setPinned(playerId, quest.id(), !quest.isPinned());
                refreshQuests();
                break;
            } catch (Exception ignored) {
                // Not handled by this provider
            }
        }
    }
    
    private int withAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }
    
    /**
     * Get the currently selected quest.
     *
     * @return Selected quest or null
     */
    public TrackedQuest getSelectedQuest() {
        return selectedQuest;
    }
    
    /**
     * Get the current sort option.
     *
     * @return Current sort option
     */
    public QuestSortOption getSortOption() {
        return sortOption;
    }
    
    /**
     * Set the sort option.
     *
     * @param sortOption New sort option
     */
    public void setSortOption(QuestSortOption sortOption) {
        this.sortOption = sortOption;
        refreshQuests();
    }
    
    /**
     * Get the current filter option.
     *
     * @return Current filter option
     */
    public QuestFilterOption getFilterOption() {
        return filterOption;
    }
    
    /**
     * Set the filter option.
     *
     * @param filterOption New filter option
     */
    public void setFilterOption(QuestFilterOption filterOption) {
        this.filterOption = filterOption;
        refreshQuests();
    }
    
    /**
     * Set compact mode for entries.
     *
     * @param compact Whether to use compact display
     */
    public void setCompactMode(boolean compact) {
        this.compactMode = compact;
        for (CategoryList category : categoryLists.values()) {
            category.setCompact(compact);
        }
    }
}
