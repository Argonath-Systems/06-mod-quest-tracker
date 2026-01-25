package com.argonathsystems.mods.questtrackerui.notification;

import com.argonathsystems.mods.questtrackerui.api.Reward;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for Toast record and builder.
 */
class ToastTest {
    
    @Test
    @DisplayName("Should create simple toast with defaults")
    void shouldCreateSimpleToastWithDefaults() {
        Toast toast = Toast.builder()
            .title("Test Title")
            .message("Test Message")
            .build();
        
        assertThat(toast.title()).isEqualTo("Test Title");
        assertThat(toast.message()).isEqualTo("Test Message");
        assertThat(toast.style()).isEqualTo(ToastStyle.INFO);
        assertThat(toast.duration()).isEqualTo(3000);
        assertThat(toast.playConfetti()).isFalse();
    }
    
    @Test
    @DisplayName("Should create success toast with rewards")
    void shouldCreateSuccessToastWithRewards() {
        List<Reward> rewards = List.of(
            new Reward(Reward.RewardType.XP, "Experience", 100, null),
            new Reward(Reward.RewardType.GOLD, "Gold", 50, null)
        );
        
        Toast toast = Toast.builder()
            .title("Quest Complete!")
            .message("You saved the village")
            .style(ToastStyle.SUCCESS)
            .duration(5000)
            .rewards(rewards)
            .playConfetti(true)
            .build();
        
        assertThat(toast.style()).isEqualTo(ToastStyle.SUCCESS);
        assertThat(toast.duration()).isEqualTo(5000);
        assertThat(toast.rewards()).hasSize(2);
        assertThat(toast.playConfetti()).isTrue();
    }
    
    @Test
    @DisplayName("Should create epic toast")
    void shouldCreateEpicToast() {
        Toast toast = Toast.builder()
            .title("Legendary Achievement!")
            .style(ToastStyle.EPIC)
            .build();
        
        assertThat(toast.style()).isEqualTo(ToastStyle.EPIC);
        assertThat(toast.style().primaryColor()).isEqualTo(0xFFFFD700); // Gold
    }
    
    @Test
    @DisplayName("ToastStyle should provide correct colors")
    void toastStyleShouldProvideCorrectColors() {
        assertThat(ToastStyle.INFO.primaryColor()).isEqualTo(0xFF87CEEB);
        assertThat(ToastStyle.SUCCESS.primaryColor()).isEqualTo(0xFF90EE90);
        assertThat(ToastStyle.WARNING.primaryColor()).isEqualTo(0xFFFFA500);
        assertThat(ToastStyle.ERROR.primaryColor()).isEqualTo(0xFFFF6B6B);
    }
    
    @Test
    @DisplayName("ToastStyle should provide sound ids")
    void toastStyleShouldProvideSoundIds() {
        assertThat(ToastStyle.SUCCESS.soundId()).isEqualTo("quest.complete");
        assertThat(ToastStyle.EPIC.soundId()).isEqualTo("quest.legendary");
    }
}
