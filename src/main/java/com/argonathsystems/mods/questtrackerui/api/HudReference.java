package com.argonathsystems.mods.questtrackerui.api;

import com.argonathsystems.framework.accessorapi.ui.UIContext;

import java.util.Objects;
import java.util.UUID;

/**
 * Type-safe wrapper for HUD references.
 * 
 * <p>Implements {@link UIContext} marker interface to provide compile-time type safety
 * when storing and retrieving HUD references, replacing the generic Object type.
 * 
 * <p>This class wraps the actual HyUI HUD reference object while providing type safety
 * and additional metadata about the HUD instance.
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // When registering a HUD
 * HudReference ref = HudReference.of(playerId, hudBuilder);
 * questTrackerMod.registerPlayerHud(playerId, ref);
 * 
 * // When retrieving
 * HudReference ref = questTrackerMod.getPlayerHud(playerId);
 * if (ref != null) {
 *     ref.getHud().refresh();
 * }
 * }</pre>
 * 
 * @author Argonath Systems
 * @version 1.1.0
 * @since 1.1.0
 */
public final class HudReference implements UIContext {
    
    private final UUID playerId;
    private final Object hudInstance;
    private final long createdAt;
    private boolean visible;
    
    /**
     * Create a new HUD reference.
     *
     * @param playerId The player this HUD belongs to
     * @param hudInstance The actual HyUI HUD instance (from HudBuilder)
     */
    public HudReference(UUID playerId, Object hudInstance) {
        this.playerId = Objects.requireNonNull(playerId, "playerId must not be null");
        this.hudInstance = Objects.requireNonNull(hudInstance, "hudInstance must not be null");
        this.createdAt = System.currentTimeMillis();
        this.visible = true;
    }
    
    /**
     * Factory method to create a HUD reference.
     *
     * @param playerId The player this HUD belongs to
     * @param hudInstance The actual HyUI HUD instance
     * @return New HUD reference
     */
    public static HudReference of(UUID playerId, Object hudInstance) {
        return new HudReference(playerId, hudInstance);
    }
    
    /**
     * Get the player ID this HUD belongs to.
     *
     * @return Player's unique identifier
     */
    public UUID getPlayerId() {
        return playerId;
    }
    
    /**
     * Get the underlying HUD instance.
     * 
     * <p>The returned object is the actual HyUI HUD instance that can be
     * used for operations like refresh, hide, show, etc.
     *
     * @return The HyUI HUD instance
     */
    public Object getHud() {
        return hudInstance;
    }
    
    /**
     * Get the underlying HUD instance cast to a specific type.
     * 
     * <p>Use this when you know the specific type of HUD being used.
     *
     * @param <T> The expected HUD type
     * @param type The class of the expected type
     * @return The HUD instance cast to the specified type
     * @throws ClassCastException if the HUD is not of the expected type
     */
    @SuppressWarnings("unchecked")
    public <T> T getHudAs(Class<T> type) {
        return type.cast(hudInstance);
    }
    
    /**
     * Get the timestamp when this HUD was created.
     *
     * @return Creation time in milliseconds since epoch
     */
    public long getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Check if this HUD is currently visible.
     *
     * @return true if visible
     */
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Set the visibility state.
     *
     * @param visible New visibility state
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    /**
     * Get the age of this HUD in milliseconds.
     *
     * @return Time since creation in milliseconds
     */
    public long getAge() {
        return System.currentTimeMillis() - createdAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HudReference that = (HudReference) o;
        return playerId.equals(that.playerId) && hudInstance.equals(that.hudInstance);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(playerId, hudInstance);
    }
    
    @Override
    public String toString() {
        return "HudReference{" +
                "playerId=" + playerId +
                ", visible=" + visible +
                ", age=" + getAge() + "ms" +
                '}';
    }
}
