package com.argonathsystems.plugin.questtrackerui;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.argonathsystems.adapter.hytaleadapter.HytaleAdapterProvider;
import com.argonathsystems.framework.accessorapi.AccessorProvider;
import com.argonathsystems.mods.questtrackerui.QuestTrackerMod;

public class QuestTrackerUiLoaderPlugin extends JavaPlugin {

    private QuestTrackerMod coreMod;

    public QuestTrackerUiLoaderPlugin(JavaPluginInit init) {
        super(init);
    }

    @Override
    public void onEnable() {
        getLogger().info("Loading quest-tracker-ui...");

        try {
            AccessorProvider accessors = HytaleAdapterProvider.getInstance();
            if (accessors == null) {
                getLogger().error("HytaleAdapterProvider not found. Aborting.");
                return;
            }

            // TODO: Instantiate the core mod with correct parameters
            // This is a generated stub. Please verify constructor arguments.
            // coreMod = new QuestTrackerMod(accessors); 
            getLogger().info("Warning: Core instantiation is commented out in generated loader.");

            getLogger().info("quest-tracker-ui initialized successfully (Stub).");

        } catch (Exception e) {
            getLogger().error("Failed to load quest-tracker-ui", e);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("quest-tracker-ui disabled.");
    }
}
