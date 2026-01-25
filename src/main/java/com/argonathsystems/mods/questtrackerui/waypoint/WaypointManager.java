package com.argonathsystems.mods.questtrackerui.waypoint;

import com.argonathsystems.framework.accessorapi.dto.LocationData;
import com.argonathsystems.mods.questtrackerui.api.QuestWaypoint;
import com.argonathsystems.mods.questtrackerui.api.TrackedObjective;
import com.argonathsystems.mods.questtrackerui.api.TrackedQuest;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Manages waypoints for tracked quests and calculates distances.
 */
public class WaypointManager {
    
    private final WaypointConfig config;
    private Supplier<LocationData> playerLocationSupplier;
    private List<QuestWaypoint> activeWaypoints;
    
    /**
     * Create a waypoint manager.
     *
     * @param config Waypoint configuration
     */
    public WaypointManager(WaypointConfig config) {
        this.config = config;
        this.activeWaypoints = new ArrayList<>();
    }
    
    /**
     * Set the player location supplier.
     *
     * @param supplier Function that returns current player location
     */
    public void setPlayerLocationSupplier(Supplier<LocationData> supplier) {
        this.playerLocationSupplier = supplier;
    }
    
    /**
     * Update active waypoints from pinned quests.
     *
     * @param pinnedQuests Currently pinned quests
     */
    public void updateFromQuests(List<TrackedQuest> pinnedQuests) {
        activeWaypoints.clear();
        
        for (TrackedQuest quest : pinnedQuests) {
            for (TrackedObjective objective : quest.objectives()) {
                if (objective.hasWaypoint() && !objective.isComplete()) {
                    activeWaypoints.add(objective.waypoint());
                }
            }
        }
    }
    
    /**
     * Get all active waypoints.
     *
     * @return List of active waypoints
     */
    public List<QuestWaypoint> getActiveWaypoints() {
        return List.copyOf(activeWaypoints);
    }
    
    /**
     * Calculate distance to an objective's waypoint.
     *
     * @param objective Objective with waypoint
     * @return Distance in meters, or -1 if no waypoint or different world
     */
    public double calculateDistance(TrackedObjective objective) {
        if (!objective.hasWaypoint() || playerLocationSupplier == null) {
            return -1;
        }
        
        LocationData playerLocation = playerLocationSupplier.get();
        if (playerLocation == null) {
            return -1;
        }
        
        LocationData waypointLocation = objective.waypoint().position();
        return playerLocation.distance(waypointLocation);
    }
    
    /**
     * Calculate distance to a waypoint.
     *
     * @param waypoint Waypoint to measure distance to
     * @return Distance in meters, or -1 if in different world
     */
    public double calculateDistance(QuestWaypoint waypoint) {
        if (playerLocationSupplier == null) {
            return -1;
        }
        
        LocationData playerLocation = playerLocationSupplier.get();
        if (playerLocation == null) {
            return -1;
        }
        
        return playerLocation.distance(waypoint.position());
    }
    
    /**
     * Find the nearest waypoint.
     *
     * @return Nearest waypoint, or null if none
     */
    public QuestWaypoint findNearest() {
        if (activeWaypoints.isEmpty() || playerLocationSupplier == null) {
            return null;
        }
        
        LocationData playerLocation = playerLocationSupplier.get();
        if (playerLocation == null) {
            return null;
        }
        
        QuestWaypoint nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        
        for (QuestWaypoint waypoint : activeWaypoints) {
            double distance = playerLocation.distance(waypoint.position());
            if (distance >= 0 && distance < nearestDistance) {
                nearest = waypoint;
                nearestDistance = distance;
            }
        }
        
        return nearest;
    }
    
    /**
     * Get waypoints within render distance.
     *
     * @return List of waypoints within max render distance
     */
    public List<QuestWaypoint> getVisibleWaypoints() {
        if (!config.enabled() || playerLocationSupplier == null) {
            return List.of();
        }
        
        LocationData playerLocation = playerLocationSupplier.get();
        if (playerLocation == null) {
            return List.of();
        }
        
        return activeWaypoints.stream()
            .filter(wp -> {
                double distance = playerLocation.distance(wp.position());
                return distance >= 0 && distance <= config.maxDistance();
            })
            .toList();
    }
}
