package com.argonathsystems.mods.questtrackerui.menu;

import com.argonathsystems.framework.accessorapi.data.DataValue;
import com.argonathsystems.framework.ui.menu.MenuTab;
import com.argonathsystems.framework.ui.menu.MenuTabHandler;
import com.argonathsystems.mods.questtrackerui.ProviderRegistry;
import com.argonathsystems.mods.questtrackerui.api.QuestType;
import com.argonathsystems.mods.questtrackerui.api.TrackedObjective;
import com.argonathsystems.mods.questtrackerui.api.TrackedQuest;
import com.argonathsystems.mods.questtrackerui.theme.Theme;
import com.argonathsystems.mods.questtrackerui.theme.ThemeRegistry;
import com.argonathsystems.mods.questtrackerui.ui.screen.QuestFilterOption;
import com.argonathsystems.mods.questtrackerui.ui.screen.QuestSortOption;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Tab handler for the Quests tab in the main menu.
 * 
 * <p>Generates the quest journal/log UI content when the player opens
 * the quests tab via /quests, /menu quests, or pressing J.
 * 
 * @author Argonath Systems
 * @version 1.0.0
 * @since 1.0.0
 */
public class QuestsTabHandler implements MenuTabHandler {
    
    private final ProviderRegistry providerRegistry;
    private final ThemeRegistry themeRegistry;
    
    // Per-player UI state
    private final Map<UUID, PlayerUIState> playerStates = new ConcurrentHashMap<>();
    
    public QuestsTabHandler(ProviderRegistry providerRegistry, ThemeRegistry themeRegistry) {
        this.providerRegistry = providerRegistry;
        this.themeRegistry = themeRegistry;
    }
    
    private PlayerUIState getState(UUID playerId) {
        return playerStates.computeIfAbsent(playerId, id -> new PlayerUIState());
    }
    
    @Override
    public String generateContent(UUID playerId, MenuTab tab) {
        List<TrackedQuest> allQuests = providerRegistry.getAllTrackedQuests(playerId);
        PlayerUIState state = getState(playerId);
        Theme theme = themeRegistry.getActiveTheme();
        
        StringBuilder html = new StringBuilder();
        
        // Add content styles
        html.append("<style>");
        html.append(getQuestTabStyles(theme));
        html.append("</style>");
        
        // Main content container
        html.append("<div class=\"quest-tab-content\">");
        
        // Header with filters
        html.append(generateHeader(allQuests.size(), state));
        
        // Two-panel layout: Quest list + Details
        html.append("<div class=\"quest-panels\">");
        
        // Left panel: Quest list
        html.append("<div class=\"quest-list-panel\">");
        html.append(generateQuestList(allQuests, playerId, state));
        html.append("</div>");
        
        // Right panel: Quest details
        html.append("<div class=\"quest-detail-panel\">");
        html.append(generateQuestDetails(playerId, state));
        html.append("</div>");
        
        html.append("</div>"); // quest-panels
        html.append("</div>"); // quest-tab-content
        
        return html.toString();
    }
    
    private String generateHeader(int questCount, PlayerUIState state) {
        return """
            <div class="quest-header">
                <div class="quest-title">
                    <span class="quest-icon">📜</span>
                    <span class="quest-title-text">Quest Journal</span>
                    <span class="quest-count">(%d active)</span>
                </div>
                <div class="quest-filters">
                    <select class="filter-dropdown" data-event="filter-change">
                        <option value="ALL"%s>All Quests</option>
                        <option value="MAIN_STORY"%s>Main Story</option>
                        <option value="SIDE_QUESTS"%s>Side Quests</option>
                        <option value="DAILY"%s>Daily</option>
                        <option value="GUILD"%s>Guild</option>
                        <option value="PINNED"%s>Pinned</option>
                        <option value="INCOMPLETE"%s>Incomplete</option>
                    </select>
                    <select class="sort-dropdown" data-event="sort-change">
                        <option value="PRIORITY"%s>By Priority</option>
                        <option value="NAME"%s>By Name</option>
                        <option value="PROGRESS"%s>By Progress</option>
                        <option value="TYPE"%s>By Type</option>
                        <option value="PINNED"%s>By Pinned</option>
                    </select>
                </div>
            </div>
            """.formatted(
                questCount,
                state.filterOption == QuestFilterOption.ALL ? " selected" : "",
                state.filterOption == QuestFilterOption.MAIN_STORY ? " selected" : "",
                state.filterOption == QuestFilterOption.SIDE_QUESTS ? " selected" : "",
                state.filterOption == QuestFilterOption.DAILY ? " selected" : "",
                state.filterOption == QuestFilterOption.GUILD ? " selected" : "",
                state.filterOption == QuestFilterOption.PINNED ? " selected" : "",
                state.filterOption == QuestFilterOption.INCOMPLETE ? " selected" : "",
                state.sortOption == QuestSortOption.PRIORITY ? " selected" : "",
                state.sortOption == QuestSortOption.NAME ? " selected" : "",
                state.sortOption == QuestSortOption.PROGRESS ? " selected" : "",
                state.sortOption == QuestSortOption.TYPE ? " selected" : "",
                state.sortOption == QuestSortOption.PINNED ? " selected" : ""
            );
    }
    
    private String generateQuestList(List<TrackedQuest> quests, UUID playerId, PlayerUIState state) {
        if (quests.isEmpty()) {
            return """
                <div class="quest-list-empty">
                    <span class="empty-icon">📋</span>
                    <span class="empty-text">No active quests</span>
                    <span class="empty-hint">Talk to NPCs with quest markers to find quests!</span>
                </div>
                """;
        }
        
        // Apply filter
        List<TrackedQuest> filtered = quests.stream()
            .filter(state.filterOption.predicate())
            .collect(Collectors.toList());
        
        // Apply sort
        filtered.sort(state.sortOption.comparator());
        
        StringBuilder list = new StringBuilder();
        list.append("<div class=\"quest-list\">");
        
        for (TrackedQuest quest : filtered) {
            String typeIcon = quest.type().icon();
            String selected = quest.id().equals(state.selectedQuestId) ? " selected" : "";
            String pinned = quest.isPinned() ? " pinned" : "";
            int progress = calculateProgress(quest);
            
            list.append(String.format("""
                <div class="quest-item%s%s" data-quest-id="%s" data-event="select-quest">
                    <div class="quest-item-icon">%s</div>
                    <div class="quest-item-info">
                        <div class="quest-item-name">%s</div>
                        <div class="quest-item-progress">
                            <div class="progress-bar">
                                <div class="progress-fill" style="width: %d%%"></div>
                            </div>
                            <span class="progress-text">%d/%d</span>
                        </div>
                    </div>
                    <div class="quest-item-actions">
                        %s
                    </div>
                </div>
                """,
                selected, pinned,
                quest.id(),
                typeIcon,
                quest.name(),
                progress,
                quest.completedObjectiveCount(), quest.objectives().size(),
                quest.isPinned() ? "📌" : ""
            ));
        }
        
        list.append("</div>");
        return list.toString();
    }
    
    private String generateQuestDetails(UUID playerId, PlayerUIState state) {
        if (state.selectedQuestId == null) {
            return """
                <div class="quest-detail-empty">
                    <span class="detail-icon">📖</span>
                    <span class="detail-text">Select a quest to view details</span>
                </div>
                """;
        }
        
        // Find the selected quest
        TrackedQuest quest = providerRegistry.getAllTrackedQuests(playerId).stream()
            .filter(q -> q.id().equals(state.selectedQuestId))
            .findFirst()
            .orElse(null);
        
        if (quest == null) {
            state.selectedQuestId = null;
            return generateQuestDetails(playerId, state);
        }
        
        StringBuilder details = new StringBuilder();
        details.append("<div class=\"quest-detail\">");
        
        // Quest title and type
        details.append(String.format("""
            <div class="detail-header">
                <div class="detail-type">%s %s</div>
                <div class="detail-title">%s</div>
            </div>
            """,
            quest.type().icon(),
            quest.type().displayName(),
            quest.name()
        ));
        
        // Quest description
        String description = quest.description() != null && !quest.description().isEmpty() 
            ? quest.description() 
            : "No description available.";
        details.append(String.format("""
            <div class="detail-description">%s</div>
            """, description));
        
        // Time remaining for timed quests
        if (quest.isTimed() && quest.timeRemaining() != null) {
            long minutes = quest.timeRemaining().toMinutes();
            String timeText = minutes > 60 
                ? String.format("%dh %dm", minutes / 60, minutes % 60)
                : String.format("%dm", minutes);
            details.append(String.format("""
                <div class="detail-timer">⏱️ Time remaining: %s</div>
                """, timeText));
        }
        
        // Objectives
        details.append("<div class=\"detail-objectives\">");
        details.append("<div class=\"objectives-title\">Objectives</div>");
        
        for (TrackedObjective obj : quest.objectives()) {
            String checkmark = obj.isComplete() ? "✓" : "○";
            String complete = obj.isComplete() ? " complete" : "";
            details.append(String.format("""
                <div class="objective-item%s">
                    <span class="objective-check">%s</span>
                    <span class="objective-desc">%s</span>
                    <span class="objective-progress">%d/%d</span>
                </div>
                """,
                complete,
                checkmark,
                obj.description(),
                obj.currentProgress(), obj.requiredProgress()
            ));
        }
        details.append("</div>"); // objectives
        
        // Action buttons
        String pinText = quest.isPinned() ? "📌 Unpin" : "📌 Pin";
        details.append(String.format("""
            <div class="detail-actions">
                <button class="action-btn primary" data-event="toggle-pin">%s</button>
                <button class="action-btn" data-event="show-map">🗺️ Show on Map</button>
                <button class="action-btn danger" data-event="abandon-quest">❌ Abandon</button>
            </div>
            """, pinText));
        
        details.append("</div>"); // quest-detail
        return details.toString();
    }
    
    @Override
    public void handleEvent(UUID playerId, MenuTab tab, String eventId, DataValue data) {
        PlayerUIState state = getState(playerId);
        
        switch (eventId) {
            case "select-quest" -> {
                if (data != null) {
                    data.asString().ifPresent(id -> state.selectedQuestId = id);
                }
            }
            case "filter-change" -> {
                if (data != null) {
                    data.asString().ifPresent(filterName -> {
                        try {
                            state.filterOption = QuestFilterOption.valueOf(filterName);
                        } catch (IllegalArgumentException e) {
                            state.filterOption = QuestFilterOption.ALL;
                        }
                    });
                }
            }
            case "sort-change" -> {
                if (data != null) {
                    data.asString().ifPresent(sortName -> {
                        try {
                            state.sortOption = QuestSortOption.valueOf(sortName);
                        } catch (IllegalArgumentException e) {
                            state.sortOption = QuestSortOption.PRIORITY;
                        }
                    });
                }
            }
            case "toggle-pin" -> {
                // Note: Pinning would need to be implemented in the QuestDataProvider
                // For now, this is a placeholder
                System.out.println("[QuestsTabHandler] Toggle pin for quest: " + state.selectedQuestId);
            }
            case "show-map" -> {
                // Would trigger minimap/world map integration
                System.out.println("[QuestsTabHandler] Show on map for quest: " + state.selectedQuestId);
            }
            case "abandon-quest" -> {
                if (state.selectedQuestId != null) {
                    // Would trigger quest abandon via provider
                    System.out.println("[QuestsTabHandler] Abandon quest: " + state.selectedQuestId);
                    state.selectedQuestId = null;
                }
            }
            default -> { /* ignore unknown events */ }
        }
    }
    
    // Helper methods
    
    private int calculateProgress(TrackedQuest quest) {
        if (quest.objectives().isEmpty()) return 0;
        return (int)(100.0 * quest.completedObjectiveCount() / quest.objectives().size());
    }
    
    private String getQuestTabStyles(Theme theme) {
        // Use theme colors if available, otherwise defaults
        String bgColor = "#1a1a2e";
        String panelColor = "#16213e";
        String accentColor = "#e94560";
        String textColor = "#ffffff";
        String mutedColor = "#888888";
        
        return """
            .quest-tab-content {
                layout-mode: Top;
                anchor-left: 0; anchor-right: 0;
                anchor-top: 0; anchor-bottom: 0;
                padding: 16;
                background: %s;
            }
            .quest-header {
                layout-mode: Left;
                height: 48;
                padding: 8 16;
                background: %s;
                border-radius: 8;
                margin-bottom: 12;
            }
            .quest-title {
                layout-mode: Left;
                flex: 1;
            }
            .quest-icon {
                font-size: 24;
                margin-right: 8;
            }
            .quest-title-text {
                font-size: 20;
                font-weight: bold;
                color: %s;
            }
            .quest-count {
                font-size: 14;
                color: %s;
                margin-left: 8;
            }
            .quest-filters {
                layout-mode: Right;
            }
            .filter-dropdown, .sort-dropdown {
                padding: 6 12;
                margin-left: 8;
                background: %s;
                border: 1px solid %s;
                border-radius: 4;
                color: %s;
            }
            .quest-panels {
                layout-mode: Left;
                flex: 1;
            }
            .quest-list-panel {
                width: 40%%;
                padding-right: 12;
            }
            .quest-detail-panel {
                flex: 1;
                background: %s;
                border-radius: 8;
                padding: 16;
            }
            .quest-list {
                layout-mode: Top;
            }
            .quest-item {
                layout-mode: Left;
                padding: 12;
                margin-bottom: 8;
                background: %s;
                border-radius: 6;
                cursor: pointer;
            }
            .quest-item:hover {
                background: %s;
            }
            .quest-item.selected {
                border: 2px solid %s;
            }
            .quest-item.pinned .quest-item-name {
                color: %s;
            }
            .quest-item-icon {
                font-size: 20;
                width: 32;
            }
            .quest-item-info {
                flex: 1;
            }
            .quest-item-name {
                font-weight: bold;
                color: %s;
            }
            .progress-bar {
                height: 4;
                background: #333;
                border-radius: 2;
                margin-top: 4;
            }
            .progress-fill {
                height: 100%%;
                background: %s;
                border-radius: 2;
            }
            .progress-text {
                font-size: 12;
                color: %s;
            }
            .quest-list-empty, .quest-detail-empty {
                layout-mode: Top;
                align-items: center;
                justify-content: center;
                padding: 32;
                color: %s;
            }
            .empty-icon, .detail-icon {
                font-size: 48;
                margin-bottom: 16;
            }
            .detail-header {
                margin-bottom: 16;
            }
            .detail-type {
                font-size: 14;
                color: %s;
                margin-bottom: 4;
            }
            .detail-title {
                font-size: 24;
                font-weight: bold;
                color: %s;
            }
            .detail-description {
                color: %s;
                line-height: 1.5;
                margin-bottom: 16;
            }
            .detail-timer {
                color: #ff6b6b;
                font-weight: bold;
                margin-bottom: 16;
            }
            .detail-objectives {
                margin-bottom: 16;
            }
            .objectives-title {
                font-weight: bold;
                margin-bottom: 8;
                color: %s;
            }
            .objective-item {
                layout-mode: Left;
                padding: 8;
                margin-bottom: 4;
                background: #1a1a2e;
                border-radius: 4;
            }
            .objective-item.complete {
                opacity: 0.6;
            }
            .objective-check {
                width: 24;
                color: %s;
            }
            .objective-item.complete .objective-check {
                color: #4ade80;
            }
            .objective-desc {
                flex: 1;
                color: %s;
            }
            .objective-progress {
                color: %s;
            }
            .detail-actions {
                layout-mode: Left;
                margin-top: 16;
            }
            .action-btn {
                padding: 10 16;
                margin-right: 8;
                background: %s;
                border-radius: 6;
                color: %s;
                cursor: pointer;
            }
            .action-btn:hover {
                opacity: 0.9;
            }
            .action-btn.primary {
                background: %s;
            }
            .action-btn.danger {
                background: #dc2626;
            }
            """.formatted(
                bgColor,        // .quest-tab-content background
                panelColor,     // .quest-header background
                textColor,      // .quest-title-text color
                mutedColor,     // .quest-count color
                bgColor,        // dropdown backgrounds
                accentColor,    // dropdown borders
                textColor,      // dropdown text
                panelColor,     // .quest-detail-panel background
                panelColor,     // .quest-item background
                "#1e3a5f",      // .quest-item:hover background
                accentColor,    // .quest-item.selected border
                accentColor,    // .quest-item.pinned name color
                textColor,      // .quest-item-name color
                accentColor,    // .progress-fill background
                mutedColor,     // .progress-text color
                mutedColor,     // empty states color
                accentColor,    // .detail-type color
                textColor,      // .detail-title color
                mutedColor,     // .detail-description color
                textColor,      // .objectives-title color
                mutedColor,     // .objective-check color
                textColor,      // .objective-desc color
                mutedColor,     // .objective-progress color
                panelColor,     // .action-btn background
                textColor,      // .action-btn color
                accentColor     // .action-btn.primary background
            );
    }
    
    /**
     * Per-player UI state for the quest tab.
     */
    private static class PlayerUIState {
        QuestSortOption sortOption = QuestSortOption.PRIORITY;
        QuestFilterOption filterOption = QuestFilterOption.ALL;
        String selectedQuestId = null;
    }
}
