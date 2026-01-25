package com.argonathsystems.mods.questtrackerui.theme;

import com.argonathsystems.framework.text.Style;
import com.argonathsystems.framework.text.TextColor;

/**
 * Defines styling profiles for different UI text elements.
 * Replaces old FontConfig with Text Styling Library integration.
 *
 * @param title Style for titles/headers
 * @param body Style for regular content text
 * @param small Style for subtitles/footnotes
 * @param active Style for active/selected elements
 * @param completed Style for completed objectives
 * @param failed Style for failed/error states
 */
public record TextStylingProfile(
    Style title,
    Style body,
    Style small,
    Style active,
    Style completed,
    Style failed,
    int titleSize,
    int bodySize,
    int smallSize
) {
    
    public static final TextStylingProfile DEFAULT = new TextStylingProfile(
        Style.builder().bold(true).color(TextColor.GOLD).build(), // Title
        Style.builder().color(TextColor.WHITE).build(), // Body
        Style.builder().color(TextColor.GRAY).build(), // Small
        Style.builder().color(TextColor.GREEN).build(), // Active
        Style.builder().color(TextColor.GRAY).strikethrough(true).build(), // Completed
        Style.builder().color(TextColor.RED).build(), // Failed
        14, // titleSize
        12, // bodySize
        10  // smallSize
    );

    // Helpers to quickly override sizes or colors if needed in future
    // For now, Style encapsulates font and color.
}
