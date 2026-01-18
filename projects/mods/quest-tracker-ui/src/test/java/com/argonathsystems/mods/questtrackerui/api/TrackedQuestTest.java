package com.argonathsystems.mods.questtrackerui.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TrackedQuest Tests")
class TrackedQuestTest {
    
    @Nested
    @DisplayName("Factory methods")
    class FactoryMethods {
        
        @Test
        @DisplayName("of() creates quest with default settings")
        void ofCreatesQuestWithDefaults() {
            List<TrackedObjective> objectives = List.of(
                TrackedObjective.simple("obj1", "Test Objective", false)
            );
            
            TrackedQuest quest = TrackedQuest.of("quest1", "Test Quest", QuestType.MAIN, objectives);
            
            assertThat(quest.id()).isEqualTo("quest1");
            assertThat(quest.name()).isEqualTo("Test Quest");
            assertThat(quest.type()).isEqualTo(QuestType.MAIN);
            assertThat(quest.objectives()).hasSize(1);
            assertThat(quest.timeRemaining()).isNull();
            assertThat(quest.isPinned()).isFalse();
            assertThat(quest.displayPriority()).isZero();
        }
    }
    
    @Nested
    @DisplayName("Progress calculation")
    class ProgressCalculation {
        
        @Test
        @DisplayName("progressPercent() returns correct value")
        void progressPercentReturnsCorrectValue() {
            List<TrackedObjective> objectives = List.of(
                TrackedObjective.simple("obj1", "Objective 1", true),
                TrackedObjective.simple("obj2", "Objective 2", true),
                TrackedObjective.simple("obj3", "Objective 3", false),
                TrackedObjective.simple("obj4", "Objective 4", false)
            );
            
            TrackedQuest quest = TrackedQuest.of("quest1", "Test", QuestType.SIDE, objectives);
            
            assertThat(quest.progressPercent()).isEqualTo(0.5);
        }
        
        @Test
        @DisplayName("progressPercent() returns 0 for empty objectives")
        void progressPercentReturnsZeroForEmpty() {
            TrackedQuest quest = TrackedQuest.of("quest1", "Test", QuestType.SIDE, List.of());
            
            assertThat(quest.progressPercent()).isZero();
        }
        
        @Test
        @DisplayName("isComplete() returns true when all objectives complete")
        void isCompleteReturnsTrueWhenAllComplete() {
            List<TrackedObjective> objectives = List.of(
                TrackedObjective.simple("obj1", "Objective 1", true),
                TrackedObjective.simple("obj2", "Objective 2", true)
            );
            
            TrackedQuest quest = TrackedQuest.of("quest1", "Test", QuestType.MAIN, objectives);
            
            assertThat(quest.isComplete()).isTrue();
        }
        
        @Test
        @DisplayName("isComplete() returns false when any objective incomplete")
        void isCompleteReturnsFalseWhenIncomplete() {
            List<TrackedObjective> objectives = List.of(
                TrackedObjective.simple("obj1", "Objective 1", true),
                TrackedObjective.simple("obj2", "Objective 2", false)
            );
            
            TrackedQuest quest = TrackedQuest.of("quest1", "Test", QuestType.MAIN, objectives);
            
            assertThat(quest.isComplete()).isFalse();
        }
    }
    
    @Nested
    @DisplayName("Timed quests")
    class TimedQuests {
        
        @Test
        @DisplayName("isTimed() returns true when time remaining is set")
        void isTimedReturnsTrueWhenTimeSet() {
            TrackedQuest quest = TrackedQuest.of("quest1", "Test", QuestType.TIMED, List.of())
                .withTimeRemaining(Duration.ofMinutes(10));
            
            assertThat(quest.isTimed()).isTrue();
        }
        
        @Test
        @DisplayName("isTimed() returns false when time remaining is null")
        void isTimedReturnsFalseWhenNoTime() {
            TrackedQuest quest = TrackedQuest.of("quest1", "Test", QuestType.TIMED, List.of());
            
            assertThat(quest.isTimed()).isFalse();
        }
        
        @Test
        @DisplayName("formattedTimeRemaining() formats minutes and seconds")
        void formattedTimeRemainingFormatsMinutesSeconds() {
            TrackedQuest quest = TrackedQuest.of("quest1", "Test", QuestType.TIMED, List.of())
                .withTimeRemaining(Duration.ofMinutes(12).plusSeconds(34));
            
            assertThat(quest.formattedTimeRemaining()).isEqualTo("12:34");
        }
        
        @Test
        @DisplayName("formattedTimeRemaining() formats hours, minutes, and seconds")
        void formattedTimeRemainingFormatsHours() {
            TrackedQuest quest = TrackedQuest.of("quest1", "Test", QuestType.TIMED, List.of())
                .withTimeRemaining(Duration.ofHours(1).plusMinutes(23).plusSeconds(45));
            
            assertThat(quest.formattedTimeRemaining()).isEqualTo("1:23:45");
        }
    }
    
    @Nested
    @DisplayName("nextObjective()")
    class NextObjective {
        
        @Test
        @DisplayName("returns first incomplete objective")
        void returnsFirstIncomplete() {
            List<TrackedObjective> objectives = List.of(
                TrackedObjective.simple("obj1", "Complete", true),
                TrackedObjective.simple("obj2", "First Incomplete", false),
                TrackedObjective.simple("obj3", "Second Incomplete", false)
            );
            
            TrackedQuest quest = TrackedQuest.of("quest1", "Test", QuestType.MAIN, objectives);
            
            TrackedObjective next = quest.nextObjective();
            assertThat(next).isNotNull();
            assertThat(next.id()).isEqualTo("obj2");
        }
        
        @Test
        @DisplayName("returns null when all complete")
        void returnsNullWhenAllComplete() {
            List<TrackedObjective> objectives = List.of(
                TrackedObjective.simple("obj1", "Complete 1", true),
                TrackedObjective.simple("obj2", "Complete 2", true)
            );
            
            TrackedQuest quest = TrackedQuest.of("quest1", "Test", QuestType.MAIN, objectives);
            
            assertThat(quest.nextObjective()).isNull();
        }
    }
}
