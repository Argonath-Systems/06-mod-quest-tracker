package com.argonathsystems.mods.questtrackerui.config;

import com.argonathsystems.mods.questtrackerui.hud.AnchorPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ConfigLoader Tests")
class ConfigLoaderTest {
    
    private ConfigLoader loader;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        loader = new ConfigLoader();
    }
    
    @Nested
    @DisplayName("Default configuration")
    class DefaultConfiguration {
        
        @Test
        @DisplayName("returns defaults for missing file")
        void returnsDefaultsForMissingFile() throws IOException {
            Path nonExistent = tempDir.resolve("nonexistent.yml");
            
            TrackerConfig config = loader.loadFromFile(nonExistent);
            
            assertThat(config).isEqualTo(TrackerConfig.DEFAULT);
        }
    }
    
    @Nested
    @DisplayName("YAML loading")
    class YamlLoading {
        
        @Test
        @DisplayName("loads position configuration")
        void loadsPositionConfig() throws IOException {
            String yaml = """
                tracker:
                  position:
                    anchor: BOTTOM_LEFT
                    offset_x: 10
                    offset_y: 20
                """;
            
            Path configFile = tempDir.resolve("config.yml");
            Files.writeString(configFile, yaml);
            
            TrackerConfig config = loader.loadFromFile(configFile);
            
            assertThat(config.position().anchor()).isEqualTo(AnchorPosition.BOTTOM_LEFT);
            assertThat(config.position().offsetX()).isEqualTo(10);
            assertThat(config.position().offsetY()).isEqualTo(20);
        }
        
        @Test
        @DisplayName("loads size configuration")
        void loadsSizeConfig() throws IOException {
            String yaml = """
                tracker:
                  size:
                    width: 300
                    max_height: 500
                    scale: 1.5
                """;
            
            Path configFile = tempDir.resolve("config.yml");
            Files.writeString(configFile, yaml);
            
            TrackerConfig config = loader.loadFromFile(configFile);
            
            assertThat(config.size().width()).isEqualTo(300);
            assertThat(config.size().maxHeight()).isEqualTo(500);
            assertThat(config.size().scale()).isEqualTo(1.5);
        }
        
        @Test
        @DisplayName("loads display configuration")
        void loadsDisplayConfig() throws IOException {
            String yaml = """
                tracker:
                  display:
                    max_pinned_quests: 5
                    max_visible_objectives: 10
                    show_progress_bars: false
                """;
            
            Path configFile = tempDir.resolve("config.yml");
            Files.writeString(configFile, yaml);
            
            TrackerConfig config = loader.loadFromFile(configFile);
            
            assertThat(config.display().maxPinnedQuests()).isEqualTo(5);
            assertThat(config.display().maxVisibleObjectives()).isEqualTo(10);
            assertThat(config.display().showProgressBars()).isFalse();
        }
        
        @Test
        @DisplayName("uses defaults for missing sections")
        void usesDefaultsForMissingSections() throws IOException {
            String yaml = """
                tracker:
                  display:
                    max_pinned_quests: 5
                """;
            
            Path configFile = tempDir.resolve("config.yml");
            Files.writeString(configFile, yaml);
            
            TrackerConfig config = loader.loadFromFile(configFile);
            
            // Display is partially loaded
            assertThat(config.display().maxPinnedQuests()).isEqualTo(5);
            // Other sections use defaults
            assertThat(config.position()).isEqualTo(PositionConfig.DEFAULT);
            assertThat(config.collapse()).isEqualTo(CollapseConfig.DEFAULT);
        }
    }
    
    @Nested
    @DisplayName("YAML saving")
    class YamlSaving {
        
        @Test
        @DisplayName("saves and reloads configuration")
        void savesAndReloadsConfig() throws IOException {
            TrackerConfig original = TrackerConfig.builder()
                .position(new PositionConfig(AnchorPosition.BOTTOM_RIGHT, 100, 200))
                .size(new SizeConfig(350, 450, 2.0))
                .build();
            
            Path configFile = tempDir.resolve("saved.yml");
            loader.saveToFile(original, configFile);
            
            TrackerConfig reloaded = loader.loadFromFile(configFile);
            
            assertThat(reloaded.position().anchor()).isEqualTo(AnchorPosition.BOTTOM_RIGHT);
            assertThat(reloaded.position().offsetX()).isEqualTo(100);
            assertThat(reloaded.size().width()).isEqualTo(350);
            assertThat(reloaded.size().scale()).isEqualTo(2.0);
        }
        
        @Test
        @DisplayName("creates parent directories")
        void createsParentDirectories() throws IOException {
            Path configFile = tempDir.resolve("nested/deep/config.yml");
            
            loader.saveToFile(TrackerConfig.DEFAULT, configFile);
            
            assertThat(Files.exists(configFile)).isTrue();
        }
    }
}
