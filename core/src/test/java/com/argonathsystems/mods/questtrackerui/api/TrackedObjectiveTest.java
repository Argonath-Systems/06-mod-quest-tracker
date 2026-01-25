package com.argonathsystems.mods.questtrackerui.api;

import com.argonathsystems.framework.accessorapi.dto.LocationData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TrackedObjective Tests")
class TrackedObjectiveTest {
    
    @Nested
    @DisplayName("Factory methods")
    class FactoryMethods {
        
        @Test
        @DisplayName("simple() creates 0/1 objective when incomplete")
        void simpleCreatesZeroOneWhenIncomplete() {
            TrackedObjective obj = TrackedObjective.simple("obj1", "Test", false);
            
            assertThat(obj.currentProgress()).isZero();
            assertThat(obj.requiredProgress()).isEqualTo(1);
            assertThat(obj.isComplete()).isFalse();
        }
        
        @Test
        @DisplayName("simple() creates 1/1 objective when complete")
        void simpleCreatesOneOneWhenComplete() {
            TrackedObjective obj = TrackedObjective.simple("obj1", "Test", true);
            
            assertThat(obj.currentProgress()).isEqualTo(1);
            assertThat(obj.requiredProgress()).isEqualTo(1);
            assertThat(obj.isComplete()).isTrue();
        }
        
        @Test
        @DisplayName("progress() creates objective with auto-calculated completion")
        void progressAutoCalculatesCompletion() {
            TrackedObjective incomplete = TrackedObjective.progress("obj1", "Kill Orcs", 5, 10);
            TrackedObjective complete = TrackedObjective.progress("obj2", "Kill Orcs", 10, 10);
            
            assertThat(incomplete.isComplete()).isFalse();
            assertThat(complete.isComplete()).isTrue();
        }
    }
    
    @Nested
    @DisplayName("Progress calculation")
    class ProgressCalculation {
        
        @Test
        @DisplayName("progressPercent() returns correct value")
        void progressPercentReturnsCorrectValue() {
            TrackedObjective obj = TrackedObjective.progress("obj1", "Test", 7, 10);
            
            assertThat(obj.progressPercent()).isEqualTo(0.7);
        }
        
        @Test
        @DisplayName("progressPercent() caps at 1.0 when over-complete")
        void progressPercentCapsAtOne() {
            TrackedObjective obj = TrackedObjective.progress("obj1", "Test", 15, 10);
            
            assertThat(obj.progressPercent()).isEqualTo(1.0);
        }
        
        @Test
        @DisplayName("progressPercent() returns 0 when required is 0")
        void progressPercentReturnsZeroWhenRequiredZero() {
            TrackedObjective obj = new TrackedObjective("obj1", "Test", 0, 0, false, null);
            
            assertThat(obj.progressPercent()).isZero();
        }
        
        @Test
        @DisplayName("progressText() formats as current/required")
        void progressTextFormatsCorrectly() {
            TrackedObjective obj = TrackedObjective.progress("obj1", "Test", 7, 10);
            
            assertThat(obj.progressText()).isEqualTo("7/10");
        }
    }
    
    @Nested
    @DisplayName("Waypoints")
    class Waypoints {
        
        @Test
        @DisplayName("hasWaypoint() returns false when no waypoint")
        void hasWaypointReturnsFalseWhenNull() {
            TrackedObjective obj = TrackedObjective.simple("obj1", "Test", false);
            
            assertThat(obj.hasWaypoint()).isFalse();
        }
        
        @Test
        @DisplayName("hasWaypoint() returns true when waypoint set")
        void hasWaypointReturnsTrueWhenSet() {
            LocationData location = new LocationData("world", 100, 64, 200);
            QuestWaypoint waypoint = QuestWaypoint.of("wp1", location, "Target");
            
            TrackedObjective obj = TrackedObjective.simple("obj1", "Test", false)
                .withWaypoint(waypoint);
            
            assertThat(obj.hasWaypoint()).isTrue();
            assertThat(obj.waypoint()).isEqualTo(waypoint);
        }
    }
    
    @Nested
    @DisplayName("Immutable updates")
    class ImmutableUpdates {
        
        @Test
        @DisplayName("withProgress() creates new instance with updated progress")
        void withProgressCreatesNewInstance() {
            TrackedObjective original = TrackedObjective.progress("obj1", "Test", 5, 10);
            TrackedObjective updated = original.withProgress(8);
            
            assertThat(original.currentProgress()).isEqualTo(5);
            assertThat(updated.currentProgress()).isEqualTo(8);
            assertThat(updated.id()).isEqualTo(original.id());
        }
        
        @Test
        @DisplayName("withProgress() auto-updates completion status")
        void withProgressUpdatesCompletion() {
            TrackedObjective incomplete = TrackedObjective.progress("obj1", "Test", 5, 10);
            TrackedObjective complete = incomplete.withProgress(10);
            
            assertThat(incomplete.isComplete()).isFalse();
            assertThat(complete.isComplete()).isTrue();
        }
    }
}
