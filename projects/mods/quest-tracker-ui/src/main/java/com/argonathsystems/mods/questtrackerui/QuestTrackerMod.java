package com.argonathsystems.mods.questtrackerui;

import com.argonathsystems.framework.accessorapi.AccessorProvider;
import com.argonathsystems.framework.accessorapi.AccessorRegistry;
import com.argonathsystems.framework.accessorapi.HUDAccessor;
import com.argonathsystems.framework.accessorapi.ThemeAccessor;
import com.argonathsystems.framework.accessorapi.dto.LocationData;
import com.argonathsystems.mods.questtrackerui.api.QuestDataProvider;
import com.argonathsystems.mods.questtrackerui.api.QuestUpdateListener;
import com.argonathsystems.mods.questtrackerui.api.TrackedObjective;
import com.argonathsystems.mods.questtrackerui.api.TrackedQuest;
import com.argonathsystems.mods.questtrackerui.config.ConfigLoader;
import com.argonathsystems.mods.questtrackerui.config.TrackerConfig;
import com.argonathsystems.mods.questtrackerui.hud.QuestTrackerHUD;
// import com.argonathsystems.mods.questtrackerui.hud.RenderContext; // Removed
import com.argonathsystems.mods.questtrackerui.notification.NotificationConfig;
import com.argonathsystems.mods.questtrackerui.notification.QuestNotifications;
// import com.argonathsystems.mods.questtrackerui.notification.ToastRenderer; // Removed
import com.argonathsystems.mods.questtrackerui.theme.Theme;
import com.argonathsystems.mods.questtrackerui.theme.ThemeRegistry;
// import com.argonathsystems.mods.questtrackerui.waypoint.CompassRenderer; // Removed
import com.argonathsystems.mods.questtrackerui.waypoint.WaypointConfig;
import com.argonathsystems.mods.questtrackerui.waypoint.WaypointManager;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Main entry point for the Quest Tracker UI mod.
 * 
 * <p>Coordinates all components and handles lifecycle.
 */
public class QuestTrackerMod extends JavaPlugin implements QuestUpdateListener {
    
    public static final String MOD_ID = "quest-tracker-ui";
    public static final String MOD_NAME = "Quest Tracker UI";
    public static final String VERSION = "1.0.0";
    
    private AccessorProvider accessorProvider;
    private HUDAccessor hudAccessor;
    private ThemeAccessor themeAccessor;
    private final Path configDirectory;
    
    // Core systems
    private final ThemeRegistry themeRegistry;
    private final ProviderRegistry providerRegistry;
    private final ConfigLoader configLoader;
    
    // Configuration
    private TrackerConfig trackerConfig;
    private WaypointConfig waypointConfig;
    private NotificationConfig notificationConfig;
    
    // UI Components
    private QuestTrackerHUD hud;
    private QuestNotifications notifications;
    private WaypointManager waypointManager;
    
    // State
    private UUID currentPlayerId;
    private Supplier<LocationData> playerLocationSupplier;
    private Supplier<Float> playerYawSupplier;
    private boolean initialized;
    
    /**
     * Create the Quest Tracker mod.
     */
    public QuestTrackerMod(JavaPluginInit init) {
        super(init);
        // Assuming config dir logic is handled via accessor or hardcoded relative to data folder
        this.configDirectory = Path.of("data", "quest-tracker"); 
        this.themeRegistry = new ThemeRegistry();
        this.providerRegistry = ProviderRegistry.getInstance();
        this.configLoader = new ConfigLoader();
        this.initialized = false;
    }

    @Override
    public void setup() {
        if (initialized) {
            return;
        }
        this.accessorProvider = AccessorRegistry.getProvider();
        if (this.accessorProvider == null) {
            System.err.println("AccessorProvider is null! Aborting Quest Tracker UI setup.");
            return;
        }
        
        // Get specialized accessors
        this.hudAccessor = accessorProvider.getHUDAccessor();
        this.themeAccessor = accessorProvider.getThemeAccessor();
        
        // Bridge theme systems - connect mod's ThemeRegistry to platform's ThemeAccessor
        themeRegistry.setThemeAccessor(themeAccessor);
        
        // Load configuration
        loadConfiguration();
        
        // Load custom themes
        loadThemes();

        // Register UI Definition
        registerUI();
        
        // Initialize components
        hud = new QuestTrackerHUD(themeRegistry, trackerConfig);
        // toastRenderer = new ToastRenderer(notificationConfig, themeRegistry.getActiveTheme());
        // notifications = new QuestNotifications(toastRenderer, notificationConfig);
        waypointManager = new WaypointManager(waypointConfig);
        // compassRenderer = new CompassRenderer(waypointConfig, themeRegistry.getActiveTheme());
        
        // Set up HUD distance calculator
        hud.setDistanceCalculator(this::calculateDistanceToObjective);
        
        // Listen for theme changes
        themeRegistry.addChangeListener((oldTheme, newTheme) -> {
            // toastRenderer.setTheme(newTheme);
            // compassRenderer.setTheme(newTheme);
        });
        
        // Register as update listener for all providers
        providerRegistry.addChangeListener(new ProviderRegistry.ProviderChangeListener() {
            @Override
            public void onProviderAdded(QuestDataProvider provider) {
                provider.addUpdateListener(QuestTrackerMod.this);
            }
            
            @Override
            public void onProviderRemoved(QuestDataProvider provider) {
                provider.removeUpdateListener(QuestTrackerMod.this);
            }
        });
        
        // Register with existing providers
        for (QuestDataProvider provider : providerRegistry.getAllProviders()) {
            provider.addUpdateListener(this);
        }
        
        initialized = true;
    }

    private void registerUI() {
         // Register HUD using HUDAccessor (persistent overlay)
         try (InputStream is = getClass().getResourceAsStream("/ui/hud_quest_tracker.hyuiml")) {
            if (is != null) {
                String hudTemplate = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                hudAccessor.registerHUD("hud_quest_tracker", hudTemplate);
            } else {
                System.err.println("Could not find /ui/hud_quest_tracker.hyuiml");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Register Quest Book Panel using UIAccessor (modal panel)
        try (InputStream is = getClass().getResourceAsStream("/ui/quest_bookpanel.hyuiml")) {
            if (is != null) {
                String uiDef = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                accessorProvider.getUIAccessor().registerUI("quest_bookpanel", uiDef);
            } else {
                System.err.println("Could not find /ui/quest_bookpanel.hyuiml");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Set the current player.
     *
     * @param playerId Player's unique identifier
     */
    public void setCurrentPlayer(UUID playerId) {
        this.currentPlayerId = playerId;
        refreshQuests();
    }
    
    /**
     * Set the player location supplier for distance calculations.
     *
     * @param supplier Function that returns current player location
     */
    public void setPlayerLocationSupplier(Supplier<LocationData> supplier) {
        this.playerLocationSupplier = supplier;
        waypointManager.setPlayerLocationSupplier(supplier);
    }
    
    /**
     * Set the player yaw supplier for compass rendering.
     *
     * @param supplier Function that returns current player yaw
     */
    public void setPlayerYawSupplier(Supplier<Float> supplier) {
        this.playerYawSupplier = supplier;
    }
    
    /**
     * Refresh quest data from all providers.
     */
    public void refreshQuests() {
        if (currentPlayerId == null) {
            return;
        }
        
        List<TrackedQuest> pinnedQuests = providerRegistry.getAllPinnedQuests(currentPlayerId);
        hud.setPinnedQuests(pinnedQuests);
        
        // Update UI Layout
        if (accessorProvider != null && accessorProvider.getUIAccessor() != null) {
            String xamlContent = hud.generateXaml();
            accessorProvider.getUIAccessor().sendUIUpdate(currentPlayerId, "quest_list_container", xamlContent);
        }

        waypointManager.updateFromQuests(pinnedQuests);
    }
    
    /*
    public void render(RenderContext ctx) {
        // Legacy render method removed
    }
    */
    
    /**
     * Toggle HUD visibility.
     */
    public void toggleTracker() {
        hud.toggleVisibility();
    }
    
    /**
     * Toggle HUD collapsed state.
     */
    public void toggleCollapse() {
        hud.toggleCollapsed();
    }
    
    /**
     * Change the active theme.
     *
     * @param themeId Theme ID to activate
     * @return true if theme was changed
     */
    public boolean setTheme(String themeId) {
        return themeRegistry.setActiveTheme(themeId);
    }
    
    /**
     * Get the theme registry.
     *
     * @return Theme registry
     */
    public ThemeRegistry getThemeRegistry() {
        return themeRegistry;
    }
    
    /**
     * Get the provider registry.
     *
     * @return Provider registry
     */
    public ProviderRegistry getProviderRegistry() {
        return providerRegistry;
    }
    
    /**
     * Get the quest notifications handler.
     *
     * @return Quest notifications
     */
    public QuestNotifications getNotifications() {
        return notifications;
    }
    
    // Configuration loading
    
    private void loadConfiguration() {
        try {
            Path configFile = configDirectory.resolve("quest-tracker-ui.yml");
            trackerConfig = configLoader.loadFromFile(configFile);
        } catch (IOException e) {
            System.err.println("Failed to load config: " + e.getMessage());
            trackerConfig = TrackerConfig.DEFAULT;
        }
        
        // Use defaults for other configs (could also load from file)
        waypointConfig = WaypointConfig.DEFAULT;
        notificationConfig = NotificationConfig.DEFAULT;
    }
    
    private void loadThemes() {
        try {
            Path themesDir = configDirectory.resolve("themes");
            int loaded = themeRegistry.loadFromDirectory(themesDir);
            System.out.println("Loaded " + loaded + " custom themes");
        } catch (IOException e) {
            System.err.println("Failed to load themes: " + e.getMessage());
        }
    }
    
    private Double calculateDistanceToObjective(TrackedObjective objective) {
        return waypointManager.calculateDistance(objective);
    }
    
    // QuestUpdateListener implementation
    
    @Override
    public void onQuestAdded(UUID playerId, TrackedQuest quest) {
        if (playerId.equals(currentPlayerId)) {
            notifications.showQuestAccepted(quest);
            refreshQuests();
        }
    }
    
    @Override
    public void onQuestRemoved(UUID playerId, String questId) {
        if (playerId.equals(currentPlayerId)) {
            refreshQuests();
        }
    }
    
    @Override
    public void onQuestCompleted(UUID playerId, String questId) {
        if (playerId.equals(currentPlayerId)) {
            // Find the quest to get details for notification
            List<TrackedQuest> quests = providerRegistry.getAllTrackedQuests(playerId);
            quests.stream()
                .filter(q -> q.id().equals(questId))
                .findFirst()
                .ifPresent(quest -> notifications.showQuestComplete(quest, List.of()));
            
            refreshQuests();
        }
    }
    
    @Override
    public void onObjectiveUpdated(UUID playerId, String questId, TrackedObjective objective) {
        if (playerId.equals(currentPlayerId)) {
            refreshQuests();
        }
    }
    
    @Override
    public void onObjectiveCompleted(UUID playerId, String questId, String objectiveId) {
        if (playerId.equals(currentPlayerId)) {
            // Find the objective to show notification
            List<TrackedQuest> quests = providerRegistry.getAllTrackedQuests(playerId);
            quests.stream()
                .filter(q -> q.id().equals(questId))
                .flatMap(q -> q.objectives().stream())
                .filter(o -> o.id().equals(objectiveId))
                .findFirst()
                .ifPresent(notifications::showObjectiveComplete);
            
            refreshQuests();
        }
    }
    
    @Override
    public void onTimerUpdate(UUID playerId, TrackedQuest quest) {
        if (playerId.equals(currentPlayerId) && quest.isTimed()) {
            notifications.showTimerWarning(quest, quest.timeRemaining());
        }
    }
}
