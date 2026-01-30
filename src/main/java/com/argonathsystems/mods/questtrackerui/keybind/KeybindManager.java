package com.argonathsystems.mods.questtrackerui.keybind;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Manages keybind actions and their handlers for the Quest Tracker.
 * 
 * <p>This class provides the business logic layer for keybind handling.
 * Actual keybind registration with the platform is handled by the adapter layer.
 * 
 * <h2>Usage</h2>
 * <pre>{@code
 * KeybindManager manager = new KeybindManager(KeybindConfig.DEFAULT);
 * 
 * // Register handlers
 * manager.registerHandler(KeybindAction.TOGGLE_TRACKER, () -> questTracker.toggleVisibility());
 * manager.registerHandler(KeybindAction.OPEN_QUEST_MENU, () -> screenController.openQuestMenu());
 * 
 * // Called by adapter when key is pressed
 * manager.triggerAction(KeybindAction.TOGGLE_TRACKER);
 * }</pre>
 * 
 * @author Argonath Systems
 * @version 1.1.0
 * @since 1.1.0
 */
public class KeybindManager {
    
    private KeybindConfig config;
    private final Map<KeybindAction, Runnable> handlers;
    private Consumer<KeybindAction> globalListener;
    private boolean enabled;
    private boolean inMenuContext;
    
    /**
     * Create a keybind manager with default configuration.
     */
    public KeybindManager() {
        this(KeybindConfig.DEFAULT);
    }
    
    /**
     * Create a keybind manager with custom configuration.
     *
     * @param config Keybind configuration
     */
    public KeybindManager(KeybindConfig config) {
        this.config = Objects.requireNonNull(config, "config must not be null");
        this.handlers = new EnumMap<>(KeybindAction.class);
        this.enabled = true;
        this.inMenuContext = false;
    }
    
    /**
     * Get the current keybind configuration.
     *
     * @return Current configuration
     */
    public KeybindConfig getConfig() {
        return config;
    }
    
    /**
     * Update the keybind configuration.
     *
     * @param config New configuration
     */
    public void setConfig(KeybindConfig config) {
        this.config = Objects.requireNonNull(config, "config must not be null");
    }
    
    /**
     * Register a handler for a keybind action.
     *
     * @param action The action to handle
     * @param handler The handler to invoke when action is triggered
     */
    public void registerHandler(KeybindAction action, Runnable handler) {
        Objects.requireNonNull(action, "action must not be null");
        Objects.requireNonNull(handler, "handler must not be null");
        handlers.put(action, handler);
    }
    
    /**
     * Unregister a handler for a keybind action.
     *
     * @param action The action to unregister
     */
    public void unregisterHandler(KeybindAction action) {
        handlers.remove(action);
    }
    
    /**
     * Set a global listener that is notified of all triggered actions.
     *
     * @param listener Listener to notify
     */
    public void setGlobalListener(Consumer<KeybindAction> listener) {
        this.globalListener = listener;
    }
    
    /**
     * Trigger a keybind action.
     * 
     * <p>If the manager is disabled, the action is ignored.
     * If the action is menu-context-only and we're not in menu context, it's ignored.
     *
     * @param action The action to trigger
     * @return true if the action was handled
     */
    public boolean triggerAction(KeybindAction action) {
        if (!enabled) {
            return false;
        }
        
        // Check menu context requirement
        if (action.isMenuContextOnly() && !inMenuContext) {
            return false;
        }
        
        Runnable handler = handlers.get(action);
        if (handler != null) {
            // Notify global listener first
            if (globalListener != null) {
                globalListener.accept(action);
            }
            
            // Execute handler
            handler.run();
            return true;
        }
        
        return false;
    }
    
    /**
     * Check if a handler is registered for an action.
     *
     * @param action The action to check
     * @return true if handler exists
     */
    public boolean hasHandler(KeybindAction action) {
        return handlers.containsKey(action);
    }
    
    /**
     * Enable or disable all keybind processing.
     *
     * @param enabled true to enable, false to disable
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Check if keybind processing is enabled.
     *
     * @return true if enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Set whether we're currently in a menu context.
     * 
     * <p>Menu-context-only keybinds (like PIN_QUEST) only work when in menu context.
     *
     * @param inMenuContext true if in menu context
     */
    public void setInMenuContext(boolean inMenuContext) {
        this.inMenuContext = inMenuContext;
    }
    
    /**
     * Check if we're in menu context.
     *
     * @return true if in menu context
     */
    public boolean isInMenuContext() {
        return inMenuContext;
    }
    
    /**
     * Get the keybind string for an action.
     *
     * @param action The action
     * @return Configured keybind string
     */
    public String getKeybindFor(KeybindAction action) {
        return action.getKeybind(config);
    }
    
    /**
     * Create a keybind descriptor for adapter registration.
     * 
     * <p>This provides all information needed by the adapter layer to register
     * the keybind with the platform's keybind system.
     *
     * @param action The action to create descriptor for
     * @return Keybind descriptor
     */
    public KeybindDescriptor getDescriptor(KeybindAction action) {
        String keybind = getKeybindFor(action);
        return new KeybindDescriptor(
            action,
            keybind,
            KeybindConfig.getBaseKey(keybind),
            KeybindConfig.requiresShift(keybind),
            KeybindConfig.requiresCtrl(keybind),
            KeybindConfig.requiresAlt(keybind),
            action.isMenuContextOnly()
        );
    }
    
    /**
     * Descriptor containing all information needed to register a keybind.
     */
    public record KeybindDescriptor(
        KeybindAction action,
        String keybindString,
        String baseKey,
        boolean shiftRequired,
        boolean ctrlRequired,
        boolean altRequired,
        boolean menuContextOnly
    ) {}
}
