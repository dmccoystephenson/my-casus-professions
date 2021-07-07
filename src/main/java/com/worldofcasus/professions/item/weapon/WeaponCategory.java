package com.worldofcasus.professions.item.weapon;

import com.worldofcasus.professions.item.ItemCategory;

public enum WeaponCategory implements ItemCategory {

    SIMPLE_MELEE("Simple Melee Weapon", true, false, true, false),
    SIMPLE_RANGED("Simple Ranged Weapon", true, false, false, true),
    MARTIAL_MELEE("Martial Melee Weapon", false, true, true, false),
    MARTIAL_RANGED("Martial Ranged Weapon", false, true, false, true);

    private final String name;
    private final boolean isSimple;
    private final boolean isMartial;
    private final boolean isMelee;
    private final boolean isRanged;

    WeaponCategory(String name, boolean isSimple, boolean isMartial, boolean isMelee, boolean isRanged) {
        this.name = name;
        this.isSimple = isSimple;
        this.isMartial = isMartial;
        this.isMelee = isMelee;
        this.isRanged = isRanged;
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean isSimple() {
        return isSimple;
    }

    public boolean isMartial() {
        return isMartial;
    }

    public boolean isMelee() {
        return isMelee;
    }

    public boolean isRanged() {
        return isRanged;
    }

}
