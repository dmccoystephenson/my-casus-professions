package com.worldofcasus.professions.item.armor;

import com.worldofcasus.professions.ability.StrengthRequirement;
import com.worldofcasus.professions.armorclass.ArmorClassCalculation;
import com.worldofcasus.professions.item.CasusItemType;
import com.worldofcasus.professions.item.Rarity;
import com.worldofcasus.professions.item.interaction.Interaction;
import com.worldofcasus.professions.money.CurrencyValue;
import com.worldofcasus.professions.weight.Weight;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.bukkit.ChatColor.*;
import static org.bukkit.inventory.ItemFlag.*;

@SerializableAs("ArmorType")
public final class ArmorType implements CasusItemType, ConfigurationSerializable {

    private final String name;
    private final Rarity rarity;
    private final ArmorCategory category;
    private final CurrencyValue cost;
    private final ArmorClassCalculation armorClass;
    private final StrengthRequirement strengthRequirement;
    private final boolean disadvantageToStealthCheck;
    private final Weight weight;
    private final boolean isMetal;
    private final List<String> flavorText;
    private final Material minecraftType;
    private final ItemMeta meta;
    private final Interaction interaction;

    public ArmorType(String name,
                     Rarity rarity,
                     ArmorCategory category,
                     CurrencyValue cost,
                     ArmorClassCalculation armorClass,
                     StrengthRequirement strengthRequirement,
                     boolean disadvantageToStealthCheck,
                     Weight weight,
                     boolean isMetal,
                     List<String> flavorText,
                     Material minecraftType,
                     ItemMeta meta,
                     Interaction interaction) {
        this.name = name;
        this.rarity = rarity;
        this.category = category;
        this.cost = cost;
        this.armorClass = armorClass;
        this.strengthRequirement = strengthRequirement;
        this.disadvantageToStealthCheck = disadvantageToStealthCheck;
        this.weight = weight;
        this.isMetal = isMetal;
        this.flavorText = flavorText;
        this.minecraftType = minecraftType;
        this.meta = meta;
        this.interaction = interaction;
    }

    public ArmorType(
            String name,
            Rarity rarity,
            ArmorCategory category,
            CurrencyValue cost,
            ArmorClassCalculation armorClass,
            StrengthRequirement strengthRequirement,
            boolean disadvantageToStealthCheck,
            Weight weight,
            boolean isMetal,
            List<String> flavorText,
            Material minecraftType,
            Interaction interaction
    ) {
        this(
                name,
                rarity,
                category,
                cost,
                armorClass,
                strengthRequirement,
                disadvantageToStealthCheck,
                weight,
                isMetal,
                flavorText,
                minecraftType,
                Bukkit.getItemFactory().getItemMeta(minecraftType),
                interaction
        );
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Rarity getRarity() {
        return rarity;
    }

    @Override
    public ArmorCategory getCategory() {
        return category;
    }

    @Override
    public CurrencyValue getCost() {
        return cost;
    }

    public ArmorClassCalculation getArmorClass() {
        return armorClass;
    }

    public StrengthRequirement getStrengthRequirement() {
        return strengthRequirement;
    }

    public boolean isDisadvantageToStealthCheck() {
        return disadvantageToStealthCheck;
    }

    public Weight getWeight() {
        return weight;
    }

    public boolean isMetal() {
        return isMetal;
    }

    public List<String> getFlavorText() {
        return flavorText;
    }

    public Material getMinecraftType() {
        return minecraftType;
    }

    public ItemMeta getMeta() {
        return meta;
    }

    @Override
    public Interaction getInteraction() {
        return interaction;
    }

    @Override
    public ItemStack toBukkitItemStack() {
        ItemStack bukkitItemStack = new ItemStack(getMinecraftType());
        ItemMeta meta = getMeta().clone();
        meta.setDisplayName(getRarity().getColor() + getName());
        List<String> lore = new ArrayList<>();
        lore.add(RED + "Requirement to wield: " + WHITE +
                (getStrengthRequirement() == null ? "None" :
                        getStrengthRequirement().getAbility().getName() + " " + getStrengthRequirement().getMinimum()));
        lore.add("" + DARK_PURPLE + BOLD + ITALIC + getCategory().getName());
        lore.add(DARK_RED + "Grants " + GOLD + getArmorClass().toString() + "AC");
        lore.add(WHITE + "----");
        lore.addAll(getFlavorText().stream().map((line) -> GRAY + line).collect(Collectors.toList()));
        if (isDisadvantageToStealthCheck()) {
            lore.add(YELLOW + "Disadvantage on Stealth");
        }
        meta.setLore(lore);
        meta.addItemFlags(
                HIDE_ATTRIBUTES,
                HIDE_UNBREAKABLE,
                HIDE_PLACED_ON,
                HIDE_POTION_EFFECTS,
                HIDE_DESTROYS,
                HIDE_ENCHANTS
        );
        bukkitItemStack.setItemMeta(meta);
        return bukkitItemStack;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("name", name);
        serialized.put("rarity", rarity.name());
        serialized.put("category", category.name());
        serialized.put("cost", cost);
        serialized.put("armor-class", armorClass);
        serialized.put("strength-requirement", strengthRequirement);
        serialized.put("disadvantage-to-stealth-check", disadvantageToStealthCheck);
        serialized.put("weight", weight);
        serialized.put("metal", isMetal);
        serialized.put("flavor-text", flavorText);
        serialized.put("minecraft-type", minecraftType.name());
        serialized.put("meta", meta);
        serialized.put("interaction", interaction);
        return serialized;
    }

    public static ArmorType deserialize(Map<String, Object> serialized) {
        Material minecraftType = Material.valueOf((String) serialized.get("minecraft-type"));
        ItemMeta meta = (ItemMeta) serialized.get("meta");
        return new ArmorType(
                (String) serialized.get("name"),
                Rarity.valueOf((String) serialized.get("rarity")),
                ArmorCategory.valueOf((String) serialized.get("category")),
                (CurrencyValue) serialized.get("cost"),
                (ArmorClassCalculation) serialized.get("armor-class"),
                (StrengthRequirement) serialized.get("strength-requirement"),
                (boolean) serialized.get("disadvantage-to-stealth-check"),
                (Weight) serialized.get("weight"),
                (boolean) serialized.get("metal"),
                (List<String>) serialized.get("flavor-text"),
                minecraftType,
                meta == null ? Bukkit.getItemFactory().getItemMeta(minecraftType) : meta,
                (Interaction) serialized.get("interaction")
        );
    }
}
