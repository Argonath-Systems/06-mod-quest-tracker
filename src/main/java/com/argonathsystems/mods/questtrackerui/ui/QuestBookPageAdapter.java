package com.argonathsystems.mods.questtrackerui.ui;

import java.util.UUID;

/**
 * Quest Book UI adapter for the Quest Tracker mod.
 * Manages the quest journal interface for players.
 * 
 * <p><b>MIGRATION-001 Status:</b> BLOCKED - Requires Official Hytale SDK & HyUI</p>
 * 
 * <p><b>Architectural Note:</b> Moved from adapter layer to mod layer as per
 * architectural remediation (2026-01-29). Quest book UI is mod-specific functionality,
 * not a generic platform concern.</p>
 * 
 * @since 1.0.0
 */
public class QuestBookPageAdapter {
    public void openQuestBook(UUID playerId) {
        throw new UnsupportedOperationException(
            "QuestBookPageAdapter.openQuestBook() requires official Hytale SDK Player and HyUI"
        );
    }
    
    public void closeQuestBook(UUID playerId) {
        throw new UnsupportedOperationException(
            "QuestBookPageAdapter.closeQuestBook() requires official Hytale SDK Player and HyUI"
        );
    }
    
    public void updateQuestBook(UUID playerId, String questId) {
        throw new UnsupportedOperationException(
            "QuestBookPageAdapter.updateQuestBook() requires official Hytale SDK Player and HyUI"
        );
    }
}
