package com.argonathsystems.mods.questtrackerui.notification;

import com.argonathsystems.mods.questtrackerui.api.Reward;
import com.argonathsystems.mods.questtrackerui.api.TrackedObjective;
import com.argonathsystems.mods.questtrackerui.api.TrackedQuest;

import java.time.Duration;
import java.util.List;

/**
 * Factory for creating quest-related notifications.
 */
public class QuestNotifications {
    
    private final ToastRenderer renderer;
    private final NotificationConfig config;
    
    /**
     * Create quest notifications handler.
     *
     * @param renderer Toast renderer
     * @param config Notification configuration
     */
    public QuestNotifications(ToastRenderer renderer, NotificationConfig config) {
        this.renderer = renderer;
        this.config = config;
    }
    
    /**
     * Show a "Quest Accepted" notification.
     *
     * @param quest The newly accepted quest
     */
    public void showQuestAccepted(TrackedQuest quest) {
        if (!config.questAccepted()) {
            return;
        }
        
        Toast toast = Toast.builder()
            .title("Quest Accepted")
            .message(quest.name())
            .icon(getQuestIcon(quest))
            .style(ToastStyle.SUCCESS)
            .duration(3000)
            .sound("quest_accept")
            .build();
        
        renderer.show(toast);
    }
    
    /**
     * Show an "Objective Complete" notification.
     *
     * @param objective The completed objective
     */
    public void showObjectiveComplete(TrackedObjective objective) {
        if (!config.objectiveCompleted()) {
            return;
        }
        
        Toast toast = Toast.builder()
            .title("Objective Complete")
            .message(objective.description())
            .icon("textures/icons/check.png")
            .style(ToastStyle.SUCCESS)
            .duration(2000)
            .sound("objective_complete")
            .build();
        
        renderer.show(toast);
    }
    
    /**
     * Show a "Quest Complete" notification with rewards.
     *
     * @param quest The completed quest
     * @param rewards Rewards earned
     */
    public void showQuestComplete(TrackedQuest quest, List<Reward> rewards) {
        if (!config.questCompleted()) {
            return;
        }
        
        Toast toast = Toast.builder()
            .title("Quest Complete!")
            .message(quest.name())
            .icon("textures/icons/trophy.png")
            .style(ToastStyle.EPIC)
            .duration(5000)
            .sound("quest_complete")
            .rewards(rewards)
            .playConfetti(true)
            .build();
        
        renderer.show(toast);
    }
    
    /**
     * Show a "Quest Failed" notification.
     *
     * @param quest The failed quest
     * @param reason Failure reason
     */
    public void showQuestFailed(TrackedQuest quest, String reason) {
        Toast toast = Toast.builder()
            .title("Quest Failed")
            .message(quest.name() + " - " + reason)
            .icon("textures/icons/x.png")
            .style(ToastStyle.ERROR)
            .duration(4000)
            .sound("quest_fail")
            .build();
        
        renderer.show(toast);
    }
    
    /**
     * Show a timer warning notification.
     *
     * @param quest The timed quest
     * @param remaining Time remaining
     */
    public void showTimerWarning(TrackedQuest quest, Duration remaining) {
        long seconds = remaining.toSeconds();
        
        // Check if we should show warning at this time
        if (!shouldShowTimerWarning(seconds)) {
            return;
        }
        
        ToastStyle style = seconds <= 60 ? ToastStyle.ERROR : ToastStyle.WARNING;
        
        Toast toast = Toast.builder()
            .title("Time Running Out!")
            .message(quest.name() + " - " + formatDuration(remaining))
            .icon("textures/icons/clock.png")
            .style(style)
            .duration(3000)
            .sound("timer_warning")
            .build();
        
        renderer.show(toast);
    }
    
    /**
     * Show a progress update notification.
     *
     * @param quest Quest being updated
     * @param objective Updated objective
     */
    public void showProgressUpdate(TrackedQuest quest, TrackedObjective objective) {
        // Only show for significant progress (configurable)
        if (objective.progressPercent() < 0.25 && objective.currentProgress() > 1) {
            return;
        }
        
        Toast toast = Toast.builder()
            .title("Progress: " + objective.progressText())
            .message(objective.description())
            .icon(getQuestIcon(quest))
            .style(ToastStyle.INFO)
            .duration(2000)
            .build();
        
        renderer.show(toast);
    }
    
    /**
     * Check if a timer warning should be shown for the given remaining seconds.
     */
    private boolean shouldShowTimerWarning(long seconds) {
        for (int warning : config.timerWarnings()) {
            // Allow a small tolerance (within 2 seconds)
            if (Math.abs(seconds - warning) <= 2) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get the icon path for a quest type.
     */
    private String getQuestIcon(TrackedQuest quest) {
        return switch (quest.type()) {
            case MAIN -> "textures/icons/star.png";
            case SIDE -> "textures/icons/diamond.png";
            case TIMED -> "textures/icons/clock.png";
            case GUILD -> "textures/icons/sword.png";
            case DAILY -> "textures/icons/sun.png";
            case WEEKLY -> "textures/icons/calendar.png";
            case EVENT -> "textures/icons/star.png";
        };
    }
    
    /**
     * Format a duration for display.
     */
    private String formatDuration(Duration duration) {
        long totalSeconds = duration.toSeconds();
        if (totalSeconds < 0) return "0:00";
        
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("%d:%02d", minutes, seconds);
    }
}
