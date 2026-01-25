package com.argonathsystems.mods.questtrackerui.theme;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ThemeRegistry Tests")
class ThemeRegistryTest {
    
    private ThemeRegistry registry;
    
    @BeforeEach
    void setUp() {
        registry = new ThemeRegistry();
    }
    
    @Nested
    @DisplayName("Default state")
    class DefaultState {
        
        @Test
        @DisplayName("has default themes registered")
        void hasDefaultThemes() {
            assertThat(registry.get("dark-fantasy")).isPresent();
            assertThat(registry.get("light-minimal")).isPresent();
        }
        
        @Test
        @DisplayName("dark-fantasy is default active theme")
        void darkFantasyIsDefault() {
            assertThat(registry.getActiveTheme()).isEqualTo(Theme.DARK_FANTASY);
        }
    }
    
    @Nested
    @DisplayName("Theme registration")
    class ThemeRegistration {
        
        @Test
        @DisplayName("register() adds theme to registry")
        void registerAddsTheme() {
            Theme custom = Theme.builder()
                .id("custom")
                .name("Custom Theme")
                .author("Test")
                .build();
            
            registry.register(custom);
            
            Optional<Theme> retrieved = registry.get("custom");
            assertThat(retrieved).isPresent();
            assertThat(retrieved.get().name()).isEqualTo("Custom Theme");
        }
        
        @Test
        @DisplayName("unregister() removes theme from registry")
        void unregisterRemovesTheme() {
            Theme custom = Theme.builder().id("custom").build();
            registry.register(custom);
            
            boolean removed = registry.unregister("custom");
            
            assertThat(removed).isTrue();
            assertThat(registry.get("custom")).isEmpty();
        }
        
        @Test
        @DisplayName("cannot unregister active theme")
        void cannotUnregisterActiveTheme() {
            boolean removed = registry.unregister("dark-fantasy");
            
            assertThat(removed).isFalse();
            assertThat(registry.get("dark-fantasy")).isPresent();
        }
    }
    
    @Nested
    @DisplayName("Active theme")
    class ActiveTheme {
        
        @Test
        @DisplayName("setActiveTheme() changes active theme")
        void setActiveThemeChanges() {
            boolean changed = registry.setActiveTheme("light-minimal");
            
            assertThat(changed).isTrue();
            assertThat(registry.getActiveTheme().id()).isEqualTo("light-minimal");
        }
        
        @Test
        @DisplayName("setActiveTheme() returns false for unknown theme")
        void setActiveThemeReturnsFalseForUnknown() {
            boolean changed = registry.setActiveTheme("nonexistent");
            
            assertThat(changed).isFalse();
            assertThat(registry.getActiveTheme()).isEqualTo(Theme.DARK_FANTASY);
        }
        
        @Test
        @DisplayName("theme change listener is notified")
        void changeListenerIsNotified() {
            Theme[] capturedOld = new Theme[1];
            Theme[] capturedNew = new Theme[1];
            
            registry.addChangeListener((oldTheme, newTheme) -> {
                capturedOld[0] = oldTheme;
                capturedNew[0] = newTheme;
            });
            
            registry.setActiveTheme("light-minimal");
            
            assertThat(capturedOld[0]).isEqualTo(Theme.DARK_FANTASY);
            assertThat(capturedNew[0]).isEqualTo(Theme.LIGHT_MINIMAL);
        }
    }
    
    @Nested
    @DisplayName("Theme filtering")
    class ThemeFiltering {
        
        @Test
        @DisplayName("getFreeThemes() returns only non-premium themes")
        void getFreeThemesReturnsNonPremium() {
            Theme premium = Theme.builder()
                .id("premium")
                .isPremium(true)
                .build();
            registry.register(premium);
            
            var freeThemes = registry.getFreeThemes();
            
            assertThat(freeThemes).allMatch(t -> !t.isPremium());
            assertThat(freeThemes).noneMatch(t -> t.id().equals("premium"));
        }
        
        @Test
        @DisplayName("getPremiumThemes() returns only premium themes")
        void getPremiumThemesReturnsPremium() {
            Theme premium = Theme.builder()
                .id("premium")
                .isPremium(true)
                .build();
            registry.register(premium);
            
            var premiumThemes = registry.getPremiumThemes();
            
            assertThat(premiumThemes).allMatch(Theme::isPremium);
            assertThat(premiumThemes).hasSize(1);
        }
    }
}
