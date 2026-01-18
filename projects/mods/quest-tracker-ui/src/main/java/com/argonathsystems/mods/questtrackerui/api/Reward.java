package com.argonathsystems.mods.questtrackerui.api;

/**
 * Represents a reward from completing a quest.
 *
 * @param type Type of reward (XP, GOLD, ITEM, REPUTATION, etc.)
 * @param name Display name of the reward
 * @param amount Quantity or amount
 * @param icon Optional icon identifier
 */
public record Reward(
    RewardType type,
    String name,
    int amount,
    String icon
) {
    
    /**
     * Create an XP reward.
     *
     * @param amount XP amount
     * @return XP reward
     */
    public static Reward xp(int amount) {
        return new Reward(RewardType.XP, "Experience", amount, "xp");
    }
    
    /**
     * Create a gold/currency reward.
     *
     * @param amount Gold amount
     * @return Gold reward
     */
    public static Reward gold(int amount) {
        return new Reward(RewardType.GOLD, "Gold", amount, "gold");
    }
    
    /**
     * Create an item reward.
     *
     * @param name Item name
     * @param amount Quantity
     * @param icon Item icon identifier
     * @return Item reward
     */
    public static Reward item(String name, int amount, String icon) {
        return new Reward(RewardType.ITEM, name, amount, icon);
    }
    
    /**
     * Create a reputation reward.
     *
     * @param factionName Faction name
     * @param amount Reputation amount (can be negative)
     * @return Reputation reward
     */
    public static Reward reputation(String factionName, int amount) {
        return new Reward(RewardType.REPUTATION, factionName + " Rep", amount, "reputation");
    }
    
    /**
     * Format the reward for display (e.g., "500 Gold", "+2,500 XP").
     *
     * @return Formatted reward string
     */
    public String formatted() {
        String prefix = type == RewardType.REPUTATION && amount > 0 ? "+" : "";
        return switch (type) {
            case XP -> "⭐ " + prefix + formatNumber(amount) + " XP";
            case GOLD -> "💰 " + formatNumber(amount) + " Gold";
            case ITEM -> name + (amount > 1 ? " x" + amount : "");
            case REPUTATION -> "❤️ " + prefix + formatNumber(amount) + " " + name;
            case SKILL_POINT -> "🔷 " + amount + " Skill Point" + (amount > 1 ? "s" : "");
            case OTHER -> name;
        };
    }
    
    private String formatNumber(int num) {
        if (num >= 1000) {
            return String.format("%,d", num);
        }
        return String.valueOf(num);
    }
    
    /**
     * Type of quest reward.
     */
    public enum RewardType {
        XP,
        GOLD,
        ITEM,
        REPUTATION,
        SKILL_POINT,
        OTHER
    }
}
