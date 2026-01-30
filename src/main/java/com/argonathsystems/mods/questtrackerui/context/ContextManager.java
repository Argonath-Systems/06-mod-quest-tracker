package com.argonathsystems.mods.questtrackerui.context;

import com.argonathsystems.mods.questtrackerui.config.ImmersiveConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Manages game context state for immersive tracker behavior per QT-L2-003.
 * 
 * <p>Tracks whether the player is in combat, dialogue, cutscene, etc. and
 * notifies listeners when visibility state should change.
 * 
 * <h2>Usage</h2>
 * <pre>{@code
 * ContextManager contextManager = new ContextManager(immersiveConfig);
 * contextManager.addVisibilityListener(visible -> {
 *     if (visible) questTracker.show();
 *     else questTracker.hide();
 * });
 * 
 * // Called by adapter when game state changes
 * contextManager.setCombatState(true);  // Hides tracker if configured
 * contextManager.setDialogueState(true); // Hides tracker if configured
 * }</pre>
 * 
 * @author Argonath Systems
 * @version 1.1.0
 * @since 1.1.0
 */
public class ContextManager {
    
    private ImmersiveConfig config;
    
    // Current context states
    private boolean inCombat;
    private boolean inDialogue;
    private boolean inCutscene;
    private boolean manuallyHidden;
    
    // Idle tracking
    private long lastUpdateTime;
    private boolean isIdle;
    
    // Listeners
    private final List<Consumer<Boolean>> visibilityListeners;
    private final List<Consumer<Double>> opacityListeners;
    private final List<Runnable> contextChangeListeners;
    
    /**
     * Create a context manager with default configuration.
     */
    public ContextManager() {
        this(ImmersiveConfig.DEFAULT);
    }
    
    /**
     * Create a context manager with custom configuration.
     *
     * @param config Immersive configuration
     */
    public ContextManager(ImmersiveConfig config) {
        this.config = config;
        this.visibilityListeners = new ArrayList<>();
        this.opacityListeners = new ArrayList<>();
        this.contextChangeListeners = new ArrayList<>();
        this.lastUpdateTime = System.currentTimeMillis();
        this.isIdle = false;
    }
    
    /**
     * Get the current configuration.
     *
     * @return Immersive configuration
     */
    public ImmersiveConfig getConfig() {
        return config;
    }
    
    /**
     * Update the configuration.
     *
     * @param config New configuration
     */
    public void setConfig(ImmersiveConfig config) {
        this.config = config;
        updateVisibility();
        updateOpacity();
    }
    
    // === Context State Setters ===
    
    /**
     * Set combat state.
     *
     * @param inCombat true if player is in combat
     */
    public void setCombatState(boolean inCombat) {
        if (this.inCombat != inCombat) {
            this.inCombat = inCombat;
            onContextChange();
        }
    }
    
    /**
     * Set dialogue state.
     *
     * @param inDialogue true if player is in dialogue
     */
    public void setDialogueState(boolean inDialogue) {
        if (this.inDialogue != inDialogue) {
            this.inDialogue = inDialogue;
            onContextChange();
        }
    }
    
    /**
     * Set cutscene state.
     *
     * @param inCutscene true if a cutscene is playing
     */
    public void setCutsceneState(boolean inCutscene) {
        if (this.inCutscene != inCutscene) {
            this.inCutscene = inCutscene;
            onContextChange();
        }
    }
    
    /**
     * Set manually hidden state (user toggled off).
     *
     * @param hidden true if manually hidden
     */
    public void setManuallyHidden(boolean hidden) {
        if (this.manuallyHidden != hidden) {
            this.manuallyHidden = hidden;
            updateVisibility();
        }
    }
    
    /**
     * Mark tracker as active (not idle).
     * 
     * <p>Call this when quest data updates or user interacts with tracker.
     */
    public void markActive() {
        lastUpdateTime = System.currentTimeMillis();
        if (isIdle) {
            isIdle = false;
            updateOpacity();
        }
    }
    
    /**
     * Check and update idle state based on timeout.
     * 
     * <p>Call this periodically (e.g., every second) to check idle timeout.
     */
    public void checkIdleState() {
        if (!config.enabled() || config.idleTimeoutMs() <= 0) {
            return;
        }
        
        long elapsed = System.currentTimeMillis() - lastUpdateTime;
        boolean shouldBeIdle = elapsed >= config.idleTimeoutMs();
        
        if (shouldBeIdle != isIdle) {
            isIdle = shouldBeIdle;
            updateOpacity();
        }
    }
    
    // === State Getters ===
    
    /**
     * Check if tracker should currently be visible.
     *
     * @return true if tracker should be visible
     */
    public boolean shouldBeVisible() {
        if (manuallyHidden) {
            return false;
        }
        
        return !config.shouldHide(inCombat, inDialogue, inCutscene);
    }
    
    /**
     * Get current opacity.
     *
     * @return Opacity value (0.0-1.0)
     */
    public double getCurrentOpacity() {
        return config.getOpacity(isIdle);
    }
    
    /**
     * Check if player is in combat.
     *
     * @return true if in combat
     */
    public boolean isInCombat() {
        return inCombat;
    }
    
    /**
     * Check if player is in dialogue.
     *
     * @return true if in dialogue
     */
    public boolean isInDialogue() {
        return inDialogue;
    }
    
    /**
     * Check if a cutscene is playing.
     *
     * @return true if in cutscene
     */
    public boolean isInCutscene() {
        return inCutscene;
    }
    
    /**
     * Check if tracker is manually hidden.
     *
     * @return true if manually hidden
     */
    public boolean isManuallyHidden() {
        return manuallyHidden;
    }
    
    /**
     * Check if tracker is idle.
     *
     * @return true if idle
     */
    public boolean isIdle() {
        return isIdle;
    }
    
    // === Listeners ===
    
    /**
     * Add a listener for visibility changes.
     *
     * @param listener Listener that receives true (show) or false (hide)
     */
    public void addVisibilityListener(Consumer<Boolean> listener) {
        visibilityListeners.add(listener);
    }
    
    /**
     * Remove a visibility listener.
     *
     * @param listener Listener to remove
     */
    public void removeVisibilityListener(Consumer<Boolean> listener) {
        visibilityListeners.remove(listener);
    }
    
    /**
     * Add a listener for opacity changes.
     *
     * @param listener Listener that receives opacity value (0.0-1.0)
     */
    public void addOpacityListener(Consumer<Double> listener) {
        opacityListeners.add(listener);
    }
    
    /**
     * Remove an opacity listener.
     *
     * @param listener Listener to remove
     */
    public void removeOpacityListener(Consumer<Double> listener) {
        opacityListeners.remove(listener);
    }
    
    /**
     * Add a listener for any context change.
     *
     * @param listener Listener to notify
     */
    public void addContextChangeListener(Runnable listener) {
        contextChangeListeners.add(listener);
    }
    
    /**
     * Remove a context change listener.
     *
     * @param listener Listener to remove
     */
    public void removeContextChangeListener(Runnable listener) {
        contextChangeListeners.remove(listener);
    }
    
    // === Private Helpers ===
    
    private void onContextChange() {
        // Notify general context change listeners
        for (Runnable listener : contextChangeListeners) {
            listener.run();
        }
        
        // Update visibility and opacity
        updateVisibility();
    }
    
    private void updateVisibility() {
        boolean visible = shouldBeVisible();
        for (Consumer<Boolean> listener : visibilityListeners) {
            listener.accept(visible);
        }
    }
    
    private void updateOpacity() {
        double opacity = getCurrentOpacity();
        for (Consumer<Double> listener : opacityListeners) {
            listener.accept(opacity);
        }
    }
    
    /**
     * Get a summary of current context state.
     *
     * @return Context state summary
     */
    public ContextState getState() {
        return new ContextState(
            inCombat,
            inDialogue,
            inCutscene,
            manuallyHidden,
            isIdle,
            shouldBeVisible(),
            getCurrentOpacity()
        );
    }
    
    /**
     * Immutable snapshot of context state.
     */
    public record ContextState(
        boolean inCombat,
        boolean inDialogue,
        boolean inCutscene,
        boolean manuallyHidden,
        boolean isIdle,
        boolean visible,
        double opacity
    ) {}
}
