package com.argonathsystems.mods.questtrackerui.notification;

import com.argonathsystems.mods.questtrackerui.hud.RenderContext;
import com.argonathsystems.mods.questtrackerui.theme.ColorScheme;
import com.argonathsystems.mods.questtrackerui.theme.Theme;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * Renders and manages toast notifications.
 */
public class ToastRenderer {
    
    private static final int TOAST_WIDTH = 300;
    private static final int TOAST_HEIGHT = 60;
    private static final int TOAST_PADDING = 12;
    private static final int TOAST_SPACING = 8;
    private static final int MAX_VISIBLE_TOASTS = 5;
    private static final int ANIMATION_DURATION_MS = 200;
    
    private final NotificationConfig config;
    private final Deque<ActiveToast> activeToasts;
    private Theme theme;
    
    /**
     * Create a toast renderer.
     *
     * @param config Notification configuration
     * @param theme Theme for rendering
     */
    public ToastRenderer(NotificationConfig config, Theme theme) {
        this.config = config;
        this.theme = theme;
        this.activeToasts = new ArrayDeque<>();
    }
    
    /**
     * Set the theme.
     *
     * @param theme New theme
     */
    public void setTheme(Theme theme) {
        this.theme = theme;
    }
    
    /**
     * Show a toast notification.
     *
     * @param toast Toast to show
     */
    public void show(Toast toast) {
        if (!config.enabled()) {
            return;
        }
        
        // Remove oldest if at max
        while (activeToasts.size() >= MAX_VISIBLE_TOASTS) {
            activeToasts.removeLast();
        }
        
        activeToasts.addFirst(new ActiveToast(toast, System.currentTimeMillis()));
    }
    
    /**
     * Update toast states and remove expired toasts.
     */
    public void update() {
        long now = System.currentTimeMillis();
        
        Iterator<ActiveToast> iter = activeToasts.iterator();
        while (iter.hasNext()) {
            ActiveToast active = iter.next();
            long elapsed = now - active.startTime;
            
            if (elapsed > active.toast.duration() + ANIMATION_DURATION_MS) {
                iter.remove();
            }
        }
    }
    
    /**
     * Render all active toasts.
     *
     * @param ctx Render context
     */
    public void render(RenderContext ctx) {
        if (!config.enabled() || activeToasts.isEmpty()) {
            return;
        }
        
        update();
        
        long now = System.currentTimeMillis();
        int yOffset = 0;
        
        // Calculate starting position based on notification position
        int startX = calculateStartX(ctx);
        int startY = calculateStartY(ctx);
        int direction = isTopPosition() ? 1 : -1;
        
        for (ActiveToast active : activeToasts) {
            long elapsed = now - active.startTime;
            float animation = calculateAnimation(elapsed, active.toast.duration());
            
            int y = startY + (yOffset * direction);
            renderToast(ctx, active.toast, startX, y, animation);
            
            yOffset += TOAST_HEIGHT + TOAST_SPACING;
        }
    }
    
    /**
     * Calculate animation progress.
     *
     * @param elapsed Time elapsed since toast started
     * @param duration Toast duration
     * @return Animation value (0 = hidden, 1 = fully visible)
     */
    private float calculateAnimation(long elapsed, int duration) {
        if (elapsed < ANIMATION_DURATION_MS) {
            // Fade in
            return (float) elapsed / ANIMATION_DURATION_MS;
        } else if (elapsed > duration) {
            // Fade out
            float fadeProgress = (float) (elapsed - duration) / ANIMATION_DURATION_MS;
            return 1.0f - fadeProgress;
        }
        return 1.0f;
    }
    
    /**
     * Render a single toast.
     */
    private void renderToast(RenderContext ctx, Toast toast, int x, int y, float animation) {
        if (animation <= 0) {
            return;
        }
        
        int alpha = (int) (animation * 255);
        
        // Calculate slide offset
        int slideOffset = (int) ((1.0f - animation) * 50);
        if (!isTopPosition()) {
            slideOffset = -slideOffset;
        }
        y += slideOffset;
        
        // Background
        int bgColor = (alpha << 24) | (theme.colors().backgroundArgb() & 0x00FFFFFF);
        ctx.fillRoundedRect(x, y, TOAST_WIDTH, TOAST_HEIGHT, 8, bgColor);
        
        // Accent bar
        int accentColor = (alpha << 24) | (toast.style().primaryColor() & 0x00FFFFFF);
        ctx.fillRect(x, y, 4, TOAST_HEIGHT, accentColor);
        
        // Icon
        if (toast.icon() != null) {
            ctx.drawTexture(toast.icon(), x + TOAST_PADDING, y + TOAST_PADDING, 24, 24);
        }
        
        // Title
        int textX = x + TOAST_PADDING + (toast.icon() != null ? 32 : 0);
        int titleColor = (alpha << 24) | (theme.colors().textPrimaryArgb() & 0x00FFFFFF);
        ctx.drawTextWithShadow(toast.title(), textX, y + TOAST_PADDING, titleColor);
        
        // Message
        int messageColor = (alpha << 24) | (theme.colors().textSecondaryArgb() & 0x00FFFFFF);
        ctx.drawText(toast.message(), textX, y + TOAST_PADDING + ctx.textHeight() + 4, messageColor);
        
        // Rewards
        if (toast.hasRewards()) {
            int rewardY = y + TOAST_HEIGHT - TOAST_PADDING - ctx.textHeight();
            int rewardX = textX;
            for (var reward : toast.rewards()) {
                String text = reward.formatted();
                ctx.drawText(text, rewardX, rewardY, messageColor);
                rewardX += ctx.textWidth(text) + 12;
            }
        }
    }
    
    private int calculateStartX(RenderContext ctx) {
        return switch (config.position()) {
            case TOP_LEFT, BOTTOM_LEFT -> 20;
            case TOP_CENTER, BOTTOM_CENTER -> (ctx.screenWidth() - TOAST_WIDTH) / 2;
            case TOP_RIGHT, BOTTOM_RIGHT -> ctx.screenWidth() - TOAST_WIDTH - 20;
        };
    }
    
    private int calculateStartY(RenderContext ctx) {
        return switch (config.position()) {
            case TOP_LEFT, TOP_CENTER, TOP_RIGHT -> 60;
            case BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT -> ctx.screenHeight() - TOAST_HEIGHT - 60;
        };
    }
    
    private boolean isTopPosition() {
        return switch (config.position()) {
            case TOP_LEFT, TOP_CENTER, TOP_RIGHT -> true;
            case BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT -> false;
        };
    }
    
    /**
     * Active toast with timing information.
     */
    private record ActiveToast(Toast toast, long startTime) {}
}
