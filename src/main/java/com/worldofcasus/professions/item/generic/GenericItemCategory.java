package com.worldofcasus.professions.item.generic;

import com.worldofcasus.professions.item.ItemCategory;

public enum GenericItemCategory implements ItemCategory {
    MATERIAL("Material"),
    INGREDIENT("Ingredient"),
    FOOD("Food"),
    GEODE("Geode"),
    GARBAGE("Garbage"),
    HERB("Herb"),
    POTION("Potion"),
    ADVENTURING_GEAR("Adventuring gear"),
    TOOL("Tool"),
    TRADE_GOOD("Trade good"),
    MISC("Misc");

    private final String name;

    GenericItemCategory(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}
