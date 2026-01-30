package com.argonathsystems.mods.questtrackerui;

import com.argonathsystems.adapter.hytalemodapi.HytalePluginWrapper;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

/**
 * Hytale plugin wrapper for the Quest Tracker UI mod.
 * 
 * <p>This thin wrapper bridges Hytale's JavaPlugin system to the 
 * platform-agnostic {@link QuestTrackerMod}.</p>
 * 
 * <p>The actual mod logic is in {@link QuestTrackerMod}, which extends
 * {@code ArgonathPlugin} for platform independence.</p>
 * 
 * @since 1.0.0
 */
public class QuestTrackerWrapper extends HytalePluginWrapper<QuestTrackerMod> {
    
    /**
     * Create the Quest Tracker wrapper.
     * Called by Hytale during plugin loading.
     * 
     * @param init Hytale plugin initialization context
     */
    public QuestTrackerWrapper(JavaPluginInit init) {
        super(init, QuestTrackerMod::new);
    }
    
    @Override
    protected String getPluginName() {
        return "QuestTrackerUI";
    }
}
