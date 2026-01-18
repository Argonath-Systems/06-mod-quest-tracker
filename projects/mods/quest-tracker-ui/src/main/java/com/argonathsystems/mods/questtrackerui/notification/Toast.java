package com.argonathsystems.mods.questtrackerui.notification;

import com.argonathsystems.mods.questtrackerui.api.Reward;

import java.util.List;

/**
 * Represents a toast notification.
 *
 * @param title Toast title
 * @param message Toast message
 * @param icon Icon identifier
 * @param style Visual style
 * @param durationMs Display duration in milliseconds
 * @param sound Sound to play (null for no sound)
 * @param rewards Rewards to display (empty if none)
 * @param showConfetti Whether to show confetti effect
 */
public record Toast(
    String title,
    String message,
    String icon,
    ToastStyle style,
    int durationMs,
    String sound,
    List<Reward> rewards,
    boolean showConfetti
) {
    
    /**
     * Create a simple toast.
     *
     * @param title Title
     * @param message Message
     * @param style Style
     * @return Toast with default settings
     */
    public static Toast simple(String title, String message, ToastStyle style) {
        return new Toast(title, message, null, style, 3000, null, List.of(), false);
    }
    
    /**
     * Create a builder for a toast.
     *
     * @return Toast builder
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Check if this toast has rewards to display.
     *
     * @return true if rewards is not empty
     */
    public boolean hasRewards() {
        return rewards != null && !rewards.isEmpty();
    }
    
    /**
     * Fluent builder for Toast.
     */
    public static class Builder {
        private String title = "";
        private String message = "";
        private String icon = null;
        private ToastStyle style = ToastStyle.INFO;
        private int durationMs = 3000;
        private String sound = null;
        private List<Reward> rewards = List.of();
        private boolean showConfetti = false;
        
        public Builder title(String title) {
            this.title = title;
            return this;
        }
        
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        
        public Builder icon(String icon) {
            this.icon = icon;
            return this;
        }
        
        public Builder style(ToastStyle style) {
            this.style = style;
            return this;
        }
        
        public Builder duration(int durationMs) {
            this.durationMs = durationMs;
            return this;
        }
        
        public Builder sound(String sound) {
            this.sound = sound;
            return this;
        }
        
        public Builder rewards(List<Reward> rewards) {
            this.rewards = List.copyOf(rewards);
            return this;
        }
        
        public Builder confetti(boolean showConfetti) {
            this.showConfetti = showConfetti;
            return this;
        }
        
        public Toast build() {
            return new Toast(title, message, icon, style, durationMs, sound, rewards, showConfetti);
        }
    }
}
