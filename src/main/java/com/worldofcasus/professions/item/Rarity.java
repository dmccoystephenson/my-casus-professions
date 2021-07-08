package com.worldofcasus.professions.item;

import org.bukkit.ChatColor;

import static org.bukkit.ChatColor.*;

public enum Rarity {

    CRAP("Crap", GRAY),
    COMMON("Common", WHITE),
    UNCOMMON("Uncommon", GREEN),
    RARE("Rare", BLUE),
    LEGENDARY("Legendary", YELLOW),
    MYTHIC("Mythic", DARK_PURPLE);

    private final String name;
    private final ChatColor color;

    Rarity(String name, ChatColor color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

}
