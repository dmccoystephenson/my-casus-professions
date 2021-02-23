package com.worldofcasus.professions.node;

import org.bukkit.inventory.ItemStack;

public final class NodeItem {

    private final NodeItemId id;
    private final ItemStack item;
    private final int chance;

    public NodeItem(NodeItemId id, ItemStack item, int chance) {
        this.id = id;
        this.item = item;
        this.chance = chance;
    }

    public NodeItem(ItemStack item, int chance) {
        this(null, item, chance);
    }

    public NodeItemId getId() {
        return id;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getChance() {
        return chance;
    }

}
