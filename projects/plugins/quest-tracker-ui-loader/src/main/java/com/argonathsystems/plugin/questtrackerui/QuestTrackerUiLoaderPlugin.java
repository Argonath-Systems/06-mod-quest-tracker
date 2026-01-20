package com.argonathsystems.plugin.questtrackerui;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.argonathsystems.framework.accessorapi.AccessorRegistry;
import com.argonathsystems.framework.accessorapi.AccessorProvider;
import com.argonathsystems.mods.questtrackerui.QuestTrackerMod;

public class QuestTrackerUiLoaderPlugin extends JavaPlugin {

    private QuestTrackerMod coreMod;

    public QuestTrackerUiLoaderPlugin(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        getLogger().atInfo().log("Loading quest-tracker-ui...");

        try {
            AccessorProvider accessors = AccessorRegistry.getProvider();
            if (accessors == null) {
                getLogger().atSevere().log("HytaleAdapterProvider not found. Aborting.");
                return;
            }

            // TODO: Instantiate the core mod with correct parameters
            // This is a generated stub. Please verify constructor arguments.
            coreMod = new QuestTrackerMod();
            coreMod.initialize();
            
            getLogger().atInfo().log("quest-tracker-ui initialized successfully.");

        } catch (Exception e) {
            getLogger().atSevere().withCause(e).log("Failed to load quest-tracker-ui");
        }
    }

    // @Override
    // public void shutdown() {
    //     getLogger().atInfo().log("quest-tracker-ui disabled.");
    // }
}
