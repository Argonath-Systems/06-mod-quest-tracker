package com.argonathsystems.mods.questtrackerui.api;

import com.argonathsystems.framework.accessorapi.ui.UIContext;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link HudReference}.
 */
@DisplayName("HudReference Tests")
class HudReferenceTest {

    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final Object MOCK_HUD = new Object();

    @Nested
    @DisplayName("Construction")
    class Construction {
        
        @Test
        @DisplayName("Constructor creates valid reference")
        void constructorCreatesValid() {
            HudReference ref = new HudReference(PLAYER_ID, MOCK_HUD);
            
            assertThat(ref.getPlayerId()).isEqualTo(PLAYER_ID);
            assertThat(ref.getHud()).isSameAs(MOCK_HUD);
        }
        
        @Test
        @DisplayName("Factory method creates valid reference")
        void factoryCreatesValid() {
            HudReference ref = HudReference.of(PLAYER_ID, MOCK_HUD);
            
            assertThat(ref.getPlayerId()).isEqualTo(PLAYER_ID);
            assertThat(ref.getHud()).isSameAs(MOCK_HUD);
        }
        
        @Test
        @DisplayName("Null playerId throws NullPointerException")
        void nullPlayerIdThrows() {
            assertThatThrownBy(() -> new HudReference(null, MOCK_HUD))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("playerId");
        }
        
        @Test
        @DisplayName("Null hudInstance throws NullPointerException")
        void nullHudInstanceThrows() {
            assertThatThrownBy(() -> new HudReference(PLAYER_ID, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("hudInstance");
        }
    }

    @Nested
    @DisplayName("UIContext implementation")
    class UIContextImplementation {
        
        @Test
        @DisplayName("HudReference implements UIContext")
        void implementsUIContext() {
            HudReference ref = HudReference.of(PLAYER_ID, MOCK_HUD);
            
            assertThat(ref).isInstanceOf(UIContext.class);
        }
        
        @Test
        @DisplayName("Can be stored as UIContext")
        void canBeStoredAsUIContext() {
            UIContext context = HudReference.of(PLAYER_ID, MOCK_HUD);
            
            assertThat(context).isNotNull();
        }
    }

    @Nested
    @DisplayName("Type-safe HUD access")
    class TypeSafeAccess {
        
        @Test
        @DisplayName("getHudAs() returns correctly typed HUD")
        void getHudAsReturnsTyped() {
            MockHud mockHud = new MockHud();
            HudReference ref = HudReference.of(PLAYER_ID, mockHud);
            
            MockHud retrieved = ref.getHudAs(MockHud.class);
            
            assertThat(retrieved).isSameAs(mockHud);
        }
        
        @Test
        @DisplayName("getHudAs() throws ClassCastException for wrong type")
        void getHudAsThrowsForWrongType() {
            HudReference ref = HudReference.of(PLAYER_ID, new Object());
            
            assertThatThrownBy(() -> ref.getHudAs(String.class))
                .isInstanceOf(ClassCastException.class);
        }
    }

    @Nested
    @DisplayName("Visibility")
    class Visibility {
        
        @Test
        @DisplayName("Default visibility is true")
        void defaultVisibilityIsTrue() {
            HudReference ref = HudReference.of(PLAYER_ID, MOCK_HUD);
            
            assertThat(ref.isVisible()).isTrue();
        }
        
        @Test
        @DisplayName("setVisible() changes visibility")
        void setVisibleChangesState() {
            HudReference ref = HudReference.of(PLAYER_ID, MOCK_HUD);
            
            ref.setVisible(false);
            assertThat(ref.isVisible()).isFalse();
            
            ref.setVisible(true);
            assertThat(ref.isVisible()).isTrue();
        }
    }

    @Nested
    @DisplayName("Timestamps")
    class Timestamps {
        
        @Test
        @DisplayName("createdAt is set on construction")
        void createdAtIsSet() {
            long before = System.currentTimeMillis();
            HudReference ref = HudReference.of(PLAYER_ID, MOCK_HUD);
            long after = System.currentTimeMillis();
            
            assertThat(ref.getCreatedAt())
                .isGreaterThanOrEqualTo(before)
                .isLessThanOrEqualTo(after);
        }
        
        @Test
        @DisplayName("getAge() returns time since creation")
        void getAgeReturnsElapsed() throws InterruptedException {
            HudReference ref = HudReference.of(PLAYER_ID, MOCK_HUD);
            
            Thread.sleep(10);
            
            assertThat(ref.getAge()).isGreaterThanOrEqualTo(10);
        }
    }

    @Nested
    @DisplayName("Equality")
    class Equality {
        
        @Test
        @DisplayName("Same player and HUD are equal")
        void samePlayerAndHudEqual() {
            HudReference ref1 = new HudReference(PLAYER_ID, MOCK_HUD);
            HudReference ref2 = new HudReference(PLAYER_ID, MOCK_HUD);
            
            assertThat(ref1).isEqualTo(ref2);
        }
        
        @Test
        @DisplayName("Different players are not equal")
        void differentPlayersNotEqual() {
            HudReference ref1 = HudReference.of(PLAYER_ID, MOCK_HUD);
            HudReference ref2 = HudReference.of(UUID.randomUUID(), MOCK_HUD);
            
            assertThat(ref1).isNotEqualTo(ref2);
        }
        
        @Test
        @DisplayName("Different HUDs are not equal")
        void differentHudsNotEqual() {
            HudReference ref1 = HudReference.of(PLAYER_ID, MOCK_HUD);
            HudReference ref2 = HudReference.of(PLAYER_ID, new Object());
            
            assertThat(ref1).isNotEqualTo(ref2);
        }
        
        @Test
        @DisplayName("hashCode is consistent with equals")
        void hashCodeConsistent() {
            HudReference ref1 = new HudReference(PLAYER_ID, MOCK_HUD);
            HudReference ref2 = new HudReference(PLAYER_ID, MOCK_HUD);
            
            assertThat(ref1.hashCode()).isEqualTo(ref2.hashCode());
        }
    }

    // Mock HUD class for type-safe testing
    private static class MockHud {
        public void refresh() {
            // Mock refresh
        }
    }
}
