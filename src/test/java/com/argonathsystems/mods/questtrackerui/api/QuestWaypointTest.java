package com.argonathsystems.mods.questtrackerui.api;

import com.argonathsystems.framework.accessorapi.dto.LocationData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for QuestWaypoint record.
 */
class QuestWaypointTest {
    
    @Test
    @DisplayName("Should create simple waypoint with defaults")
    void shouldCreateSimpleWaypointWithDefaults() {
        LocationData location = new LocationData("world", 100.0, 64.0, 200.0);
        
        QuestWaypoint waypoint = QuestWaypoint.of("wp1", location, "Test Location");
        
        assertThat(waypoint.id()).isEqualTo("wp1");
        assertThat(waypoint.position()).isEqualTo(location);
        assertThat(waypoint.label()).isEqualTo("Test Location");
        assertThat(waypoint.style()).isEqualTo(WaypointStyle.DEFAULT);
        assertThat(waypoint.showCompass()).isTrue();
        assertThat(waypoint.showWorldMarker()).isTrue();
        assertThat(waypoint.showDistance()).isTrue();
    }
    
    @Test
    @DisplayName("Should create styled waypoint")
    void shouldCreateStyledWaypoint() {
        LocationData location = new LocationData("world", 50.0, 100.0, 75.0);
        
        QuestWaypoint waypoint = QuestWaypoint.styled("wp2", location, "NPC Location", WaypointStyle.NPC);
        
        assertThat(waypoint.style()).isEqualTo(WaypointStyle.NPC);
    }
    
    @Test
    @DisplayName("Should calculate distance from player")
    void shouldCalculateDistanceFromPlayer() {
        LocationData waypointLoc = new LocationData("world", 100.0, 64.0, 100.0);
        LocationData playerLoc = new LocationData("world", 100.0, 64.0, 200.0);
        
        QuestWaypoint waypoint = QuestWaypoint.of("wp1", waypointLoc, "Test");
        
        double distance = waypoint.distanceFrom(playerLoc);
        
        assertThat(distance).isCloseTo(100.0, org.assertj.core.data.Offset.offset(0.1));
    }
    
    @Test
    @DisplayName("Should format distance text")
    void shouldFormatDistanceText() {
        assertThat(QuestWaypoint.formatDistance(25.5)).isEqualTo("26m");
        assertThat(QuestWaypoint.formatDistance(150.0)).isEqualTo("150m");
        assertThat(QuestWaypoint.formatDistance(999.0)).isEqualTo("999m");
        assertThat(QuestWaypoint.formatDistance(1000.0)).isEqualTo("1.0km");
        assertThat(QuestWaypoint.formatDistance(1500.0)).isEqualTo("1.5km");
    }
    
    @Test
    @DisplayName("WaypointStyle should provide correct icon names")
    void waypointStyleShouldProvideCorrectIconNames() {
        assertThat(WaypointStyle.DEFAULT.iconName()).isEqualTo("waypoint_default");
        assertThat(WaypointStyle.OBJECTIVE.iconName()).isEqualTo("waypoint_objective");
        assertThat(WaypointStyle.NPC.iconName()).isEqualTo("waypoint_npc");
        assertThat(WaypointStyle.DANGER.iconName()).isEqualTo("waypoint_danger");
        assertThat(WaypointStyle.QUEST_GIVER.iconName()).isEqualTo("waypoint_quest_giver");
        assertThat(WaypointStyle.QUEST_TURNIN.iconName()).isEqualTo("waypoint_quest_turnin");
    }
    
    @Test
    @DisplayName("WaypointStyle should provide correct colors")
    void waypointStyleShouldProvideCorrectColors() {
        assertThat(WaypointStyle.DEFAULT.color()).isEqualTo(0xFFFFFFFF);
        assertThat(WaypointStyle.DANGER.color()).isEqualTo(0xFFFF4444);
        assertThat(WaypointStyle.QUEST_GIVER.color()).isEqualTo(0xFFFFD700);
        assertThat(WaypointStyle.QUEST_TURNIN.color()).isEqualTo(0xFF90EE90);
    }
}
