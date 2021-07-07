package com.worldofcasus.professions.item;

import org.bukkit.inventory.ItemStack;

public final class CasusItemStack {

    private final CasusItemType type;
    private final int amount;

    public CasusItemStack(CasusItemType type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    public CasusItemType getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public ItemStack toBukkitItemStack() {
        ItemStack bukkitItemStack = getType().toBukkitItemStack().clone();
        bukkitItemStack.setAmount(getAmount());
        return bukkitItemStack;
    }

}
