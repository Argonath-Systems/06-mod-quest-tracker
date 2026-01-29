package com.argonathsystems.mods.questtrackerui;

import com.argonathsystems.framework.accessorapi.AccessorProvider;
import com.argonathsystems.framework.accessorapi.AccessorRegistry;
import com.argonathsystems.framework.accessorapi.dto.LocationData;
import com.argonathsystems.framework.ui.UnifiedUIManager;
import com.argonathsystems.framework.ui.dev.DevModeConfig;
import com.argonathsystems.mods.questtrackerui.api.QuestDataProvider;
import com.argonathsystems.mods.questtrackerui.api.QuestUpdateListener;
import com.argonathsystems.mods.questtrackerui.api.TrackedObjective;
import com.argonathsystems.mods.questtrackerui.api.TrackedQuest;
import com.argonathsystems.mods.questtrackerui.config.ConfigLoader;
import com.argonathsystems.mods.questtrackerui.config.TrackerConfig;
import com.argonathsystems.mods.questtrackerui.hud.HyuimlQuestTrackerHUD;
import com.argonathsystems.mods.questtrackerui.notification.NotificationConfig;
import com.argonathsystems.mods.questtrackerui.notification.QuestNotifications;
import com.argonathsystems.mods.questtrackerui.notification.ToastRenderer;
import com.argonathsystems.mods.questtrackerui.theme.Theme;
import com.argonathsystems.mods.questtrackerui.theme.ThemeRegistry;
import com.argonathsystems.mods.questtrackerui.waypoint.CompassRenderer;
import com.argonathsystems.mods.questtrackerui.waypoint.WaypointConfig;
import com.argonathsystems.mods.questtrackerui.waypoint.WaypointManager;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import com.argonathsystems.adapter.api.ArgonathPlugin;


/**
 * Main entry point for the Quest Tracker UI mod.
 * 
 * <p>Coordinates all components and handles lifecycle.
 * 
 * @since MIGRATION-001 - Now extends ArgonathPlugin (platform-agnostic)
 */
public class QuestTrackerMod extends ArgonathPlugin implements QuestUpdateListener {
    
    public static final String MOD_ID = "quest-tracker-ui";
    public static final String MOD_NAME = "Quest Tracker UI";
    public static final String VERSION = "1.0.0";
    
    private final AccessorProvider accessorProvider;
    private final Path configDirectory;
    
    // Core systems
    private final ThemeRegistry themeRegistry;
    private final ProviderRegistry providerRegistry;
    private final ConfigLoader configLoader;
    
    // Configuration
    private TrackerConfig trackerConfig;
    private WaypointConfig waypointConfig;
    private NotificationConfig notificationConfig;
    
    // UI Components (HYUIML-based)
    private HyuimlQuestTrackerHUD hud;
    private ToastRenderer toastRenderer;
    private QuestNotifications notifications;
    private WaypointManager waypointManager;
    private CompassRenderer compassRenderer;
    
    // Per-player HUD tracking for refresh
    private final Map<UUID, Object> playerHudRefs = new ConcurrentHashMap<>();
    
    // Hot reload support
    private boolean devModeEnabled;
    
    // State
    private UUID currentPlayerId;
    private Supplier<LocationData> playerLocationSupplier;
    private Supplier<Float> playerYawSupplier;
    private boolean initialized;
    
    /**
     * Create the Quest Tracker mod.
     */
    public QuestTrackerMod() {
        this.accessorProvider = AccessorRegistry.getProvider();
        // Assuming config dir logic is handled via accessor or hardcoded relative to data folder
        this.configDirectory = Path.of("data", "quest-tracker"); 
        this.themeRegistry = new ThemeRegistry();
        this.providerRegistry = ProviderRegistry.getInstance();
        this.configLoader = new ConfigLoader();
        this.initialized = false;
    }
    
    /**
     * Initialize the mod.
     */
    public void initialize() {
        if (initialized) {
            return;
        }
        
        // Load configuration
        loadConfiguration();
        
        // Load custom themes
        loadThemes();
        
        // Initialize development mode for hot reload (check environment)
        initDevMode();
        
        // Initialize HYUIML-based HUD components
        hud = new HyuimlQuestTrackerHUD(themeRegistry, trackerConfig);
        
        // Set up template supplier for hot reload support
        if (devModeEnabled) {
            Supplier<String> templateSupplier = UnifiedUIManager.getInstance()
                .createUISupplier("quest-tracker-hud", "config/ui/huds/quest-tracker-hud.hyuiml");
            hud.setTemplateSupplier(templateSupplier);
        }
        
        toastRenderer = new ToastRenderer(notificationConfig, themeRegistry.getActiveTheme());
        notifications = new QuestNotifications(toastRenderer, notificationConfig);
        waypointManager = new WaypointManager(waypointConfig);
        compassRenderer = new CompassRenderer(waypointConfig, themeRegistry.getActiveTheme());
        
        // Set up HUD distance calculator
        hud.setDistanceCalculator(this::calculateDistanceToObjective);
        
        // Listen for theme changes
        themeRegistry.addChangeListener((oldTheme, newTheme) -> {
            toastRenderer.setTheme(newTheme);
            compassRenderer.setTheme(newTheme);
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
        waypointManager.updateFromQuests(pinnedQuests);
        
        // Notify that HUD needs refresh (for HYUIML-based rendering)
        onHudNeedsRefresh();
    }
    
    /**
     * Generate the current HUD HTML for HYUIML-based rendering.
     * 
     * <p>This replaces the old programmatic render method. Call this to get
     * the HYUIML string, then pass it to HudBuilder.
     *
     * @return HYUIML string for the quest tracker HUD
     */
    public String generateHudHtml() {
        if (!initialized) {
            return "<!-- Quest Tracker not initialized -->";
        }
        
        // Use template if available (hot reload), otherwise generate programmatically
        return hud.generateFromTemplate();
    }
    
    /**
     * Called when the HUD needs to be refreshed.
     * 
     * <p>Override or set a callback to handle HUD refresh in the adapter layer.
     */
    protected void onHudNeedsRefresh() {
        // This will be called when quest data changes
        // The adapter layer should override or listen to refresh the actual HUD
    }
    
    /**
     * Register a player's HUD reference for refresh tracking.
     *
     * @param playerId Player's unique identifier
     * @param hudRef The HUD reference from HudBuilder
     */
    public void registerPlayerHud(UUID playerId, Object hudRef) {
        playerHudRefs.put(playerId, hudRef);
    }
    
    /**
     * Unregister a player's HUD reference.
     *
     * @param playerId Player's unique identifier
     */
    public void unregisterPlayerHud(UUID playerId) {
        playerHudRefs.remove(playerId);
    }
    
    /**
     * Get the HUD reference for a player.
     *
     * @param playerId Player's unique identifier
     * @return The HUD reference, or null if not registered
     */
    public Object getPlayerHud(UUID playerId) {
        return playerHudRefs.get(playerId);
    }
    
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
    
    /**
     * Initialize development mode for UI hot reload.
     * 
     * <p>Checks the ARGONATH_ENV environment variable. If not set to "production",
     * enables hot reload for HYUIML files.
     */
    private void initDevMode() {
        String env = System.getenv("ARGONATH_ENV");
        devModeEnabled = !"production".equalsIgnoreCase(env);
        
        if (devModeEnabled) {
            try {
                // Initialize UI hot reload with config directory
                Path uiDirectory = configDirectory.resolve("ui");
                DevModeConfig devConfig = DevModeConfig.builder()
                    .enabled(true)
                    .hotReloadEnabled(true)
                    .uiDirectory(uiDirectory)
                    .pollIntervalMs(500)
                    .autoRefreshPlayers(true)
                    .logChanges(true)
                    .build();
                
                UnifiedUIManager.getInstance().initDevMode(devConfig);
                
                // Register for reload events to refresh HUDs
                UnifiedUIManager.getInstance().getHotReloadService().ifPresent(service -> {
                    service.onReload(pageId -> {
                        if ("quest-tracker-hud".equals(pageId)) {
                            System.out.println("[Quest Tracker] Hot reload detected, refreshing HUDs...");
                            onHudNeedsRefresh();
                        }
                    });
                });
                
                System.out.println("[Quest Tracker] Development mode enabled - UI hot reload active");
            } catch (Exception e) {
                System.err.println("[Quest Tracker] Failed to initialize dev mode: " + e.getMessage());
                devModeEnabled = false;
            }
        } else {
            System.out.println("[Quest Tracker] Production mode - UI hot reload disabled");
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
