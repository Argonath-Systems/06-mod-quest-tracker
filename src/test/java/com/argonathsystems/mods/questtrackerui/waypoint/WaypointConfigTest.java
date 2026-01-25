package com.argonathsystems.mods.questtrackerui.waypoint;

import com.argonathsystems.framework.accessorapi.dto.LocationData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for WaypointConfig record.
 */
class WaypointConfigTest {
    
    @Test
    @DisplayName("Should calculate full opacity at minimum distance")
    void shouldCalculateFullOpacityAtMinimumDistance() {
        WaypointConfig config = WaypointConfig.DEFAULT;
        
        double opacity = config.calculateOpacity(5.0);
        
        assertThat(opacity).isEqualTo(1.0);
    }
    
    @Test
    @DisplayName("Should calculate zero opacity at maximum distance")
    void shouldCalculateZeroOpacityAtMaximumDistance() {
        WaypointConfig config = WaypointConfig.DEFAULT;
        
        double opacity = config.calculateOpacity(config.maxDistance());
        
        assertThat(opacity).isEqualTo(0.0);
    }
    
    @Test
    @DisplayName("Should calculate opacity at fade start")
    void shouldCalculateOpacityAtFadeStart() {
        WaypointConfig config = WaypointConfig.DEFAULT;
        double fadeStart = config.maxDistance() * config.fadeStartPercent();
        
        double opacity = config.calculateOpacity(fadeStart);
        
        // At fade start, opacity should still be 1.0
        assertThat(opacity).isCloseTo(1.0, org.assertj.core.data.Offset.offset(0.01));
    }
    
    @Test
    @DisplayName("Should calculate half opacity at midpoint of fade")
    void shouldCalculateHalfOpacityAtMidpointOfFade() {
        WaypointConfig config = WaypointConfig.DEFAULT;
        double fadeStart = config.maxDistance() * config.fadeStartPercent();
        double midpoint = fadeStart + (config.maxDistance() - fadeStart) / 2;
        
        double opacity = config.calculateOpacity(midpoint);
        
        assertThat(opacity).isCloseTo(0.5, org.assertj.core.data.Offset.offset(0.01));
    }
    
    @Test
    @DisplayName("Should use default values")
    void shouldUseDefaultValues() {
        WaypointConfig config = WaypointConfig.DEFAULT;
        
        assertThat(config.enabled()).isTrue();
        assertThat(config.showDistance()).isTrue();
        assertThat(config.showOnScreen()).isTrue();
        assertThat(config.maxWaypoints()).isEqualTo(5);
        assertThat(config.maxDistance()).isEqualTo(500.0);
        assertThat(config.fadeStartPercent()).isEqualTo(0.7);
    }
    
    @Test
    @DisplayName("Should create compact waypoints")
    void shouldCreateCompactWaypoints() {
        WaypointConfig compact = WaypointConfig.COMPACT;
        
        assertThat(compact.showDistance()).isFalse();
        assertThat(compact.maxWaypoints()).isEqualTo(3);
    }
}
