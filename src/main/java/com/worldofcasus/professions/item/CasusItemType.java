package com.worldofcasus.professions.item;

import com.worldofcasus.professions.item.interaction.Interaction;
import com.worldofcasus.professions.money.CurrencyValue;
import org.bukkit.inventory.ItemStack;

public interface CasusItemType {

    String getName();
    ItemStack toBukkitItemStack();
    Rarity getRarity();
    ItemCategory getCategory();
    CurrencyValue getCost();
    Interaction getInteraction();

}
