package com.worldofcasus.professions.item.weapon;

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

import static org.bukkit.ChatColor.*;
import static org.bukkit.inventory.ItemFlag.*;

@SerializableAs("WeaponType")
public final class WeaponType implements CasusItemType, ConfigurationSerializable {

    private final String name;
    private final Rarity rarity;
    private final WeaponCategory category;
    private final CurrencyValue cost;
    private final WeaponDamage damage;
    private final Weight weight;
    private final List<WeaponProperty> properties;
    private final List<String> flavorText;
    private final Material minecraftType;
    private final ItemMeta meta;
    private final Interaction interaction;

    public WeaponType(
            String name,
            Rarity rarity,
            WeaponCategory category,
            CurrencyValue cost,
            WeaponDamage damage,
            Weight weight,
            List<WeaponProperty> properties,
            List<String> flavorText,
            Material minecraftType,
            ItemMeta meta,
            Interaction interaction
    ) {
        this.name = name;
        this.rarity = rarity;
        this.category = category;
        this.cost = cost;
        this.damage = damage;
        this.weight = weight;
        this.properties = properties;
        this.flavorText = flavorText;
        this.minecraftType = minecraftType;
        this.meta = meta;
        this.interaction = interaction;
    }

    public WeaponType(
            String name,
            Rarity rarity,
            WeaponCategory category,
            CurrencyValue cost,
            WeaponDamage damage,
            Weight weight,
            List<WeaponProperty> properties,
            List<String> flavorText,
            Material minecraftType,
            Interaction interaction
    ) {
        this(
                name,
                rarity,
                category,
                cost,
                damage,
                weight,
                properties,
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
    public WeaponCategory getCategory() {
        return category;
    }

    public CurrencyValue getCost() {
        return cost;
    }

    public WeaponDamage getDamage() {
        return damage;
    }

    public Weight getWeight() {
        return weight;
    }

    public List<WeaponProperty> getProperties() {
        return properties;
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
        ItemStack itemStack = new ItemStack(getMinecraftType());
        ItemMeta meta = getMeta().clone();
        meta.setDisplayName(getRarity().getColor() + getName());
        List<String> lore = new ArrayList<>();
        getProperties().stream()
                .map(WeaponProperty::toString)
                .reduce((a, b) -> a + ", " + b)
                .ifPresent(properties -> lore.add("" + DARK_PURPLE + BOLD + ITALIC + properties));
        WeaponProperty.Versatile versatile = (WeaponProperty.Versatile) getProperties().stream()
                .filter(property -> property instanceof WeaponProperty.Versatile)
                .findFirst()
                .orElse(null);
        if (versatile != null) {
            lore.add(DARK_RED + "Deals " + GOLD + getDamage().getRoll().toString() + " " + getDamage().getDamageType().getName()
                    + WHITE + " | " + DARK_PURPLE + BOLD + "One-handed");
            lore.add(DARK_RED + "Deals " + GOLD + versatile.getTwoHandedRoll().toString() + " " + getDamage().getDamageType().getName()
                    + WHITE + " | " + DARK_PURPLE + BOLD + "Two-handed");
        } else {
            lore.add(DARK_RED + "Deals " + GOLD + getDamage().getRoll().toString() + " " + getDamage().getDamageType().getName());
        }
        getFlavorText().forEach(flavorText -> lore.add(YELLOW + flavorText));
        meta.setLore(lore);
        meta.setUnbreakable(true);
        meta.addItemFlags(
                HIDE_ATTRIBUTES,
                HIDE_UNBREAKABLE,
                HIDE_PLACED_ON,
                HIDE_POTION_EFFECTS,
                HIDE_DESTROYS,
                HIDE_ENCHANTS
        );
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("name", name);
        serialized.put("rarity", rarity.name());
        serialized.put("category", category.name());
        serialized.put("cost", cost);
        serialized.put("damage", damage);
        serialized.put("weight", weight);
        serialized.put("properties", properties);
        serialized.put("flavor-text", flavorText);
        serialized.put("minecraft-type", minecraftType.name());
        serialized.put("meta", meta);
        serialized.put("interaction", interaction);
        return serialized;
    }

    public static WeaponType deserialize(Map<String, Object> serialized) {
        return new WeaponType(
                (String) serialized.get("name"),
                Rarity.valueOf((String) serialized.get("rarity")),
                WeaponCategory.valueOf((String) serialized.get("category")),
                (CurrencyValue) serialized.get("cost"),
                (WeaponDamage) serialized.get("damage"),
                (Weight) serialized.get("weight"),
                (List<WeaponProperty>) serialized.get("properties"),
                (List<String>) serialized.get("flavor-text"),
                Material.valueOf((String) serialized.get("minecraft-type")),
                (ItemMeta) serialized.get("meta"),
                (Interaction) serialized.get("interaction")
        );
    }
}
