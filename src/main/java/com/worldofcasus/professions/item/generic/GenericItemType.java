package com.worldofcasus.professions.item.generic;

import com.worldofcasus.professions.item.CasusItemType;
import com.worldofcasus.professions.item.ItemCategory;
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

@SerializableAs("GenericItemType")
public final class GenericItemType implements CasusItemType, ConfigurationSerializable {

    private final String name;
    private final GenericItemCategory category;
    private final Rarity rarity;
    private final CurrencyValue cost;
    private final Weight weight;
    private final List<String> flavorText;
    private final Material minecraftType;
    private final ItemMeta meta;
    private final Interaction interaction;

    public GenericItemType(String name,
                           GenericItemCategory category,
                           Rarity rarity,
                           CurrencyValue cost,
                           Weight weight,
                           List<String> flavorText,
                           Material minecraftType,
                           ItemMeta meta,
                           Interaction interaction) {
        this.name = name;
        this.category = category;
        this.rarity = rarity;
        this.cost = cost;
        this.weight = weight;
        this.flavorText = flavorText;
        this.minecraftType = minecraftType;
        this.meta = meta;
        this.interaction = interaction;
    }

    public GenericItemType(String name,
                           GenericItemCategory category,
                           Rarity rarity,
                           CurrencyValue cost,
                           Weight weight,
                           List<String> flavorText,
                           Material minecraftType,
                           Interaction interaction) {
        this(name,
                category,
                rarity,
                cost,
                weight,
                flavorText,
                minecraftType,
                Bukkit.getItemFactory().getItemMeta(minecraftType),
                interaction);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ItemStack toBukkitItemStack() {
        ItemStack itemStack = new ItemStack(getMinecraftType());
        ItemMeta meta = getMeta().clone();
        meta.setDisplayName(getRarity().getColor() + getName());
        List<String> lore = new ArrayList<>();
        lore.add("" + DARK_PURPLE + BOLD + ITALIC + getCategory().getName());
        getFlavorText().forEach(flavorText -> lore.add(YELLOW + flavorText));
        meta.setUnbreakable(true);
        meta.addItemFlags(
                HIDE_ATTRIBUTES,
                HIDE_UNBREAKABLE,
                HIDE_PLACED_ON,
                HIDE_POTION_EFFECTS,
                HIDE_DESTROYS,
                HIDE_ENCHANTS
        );
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public ItemCategory getCategory() {
        return category;
    }

    @Override
    public Rarity getRarity() {
        return rarity;
    }

    @Override
    public CurrencyValue getCost() {
        return null;
    }

    public Weight getWeight() {
        return weight;
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
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("name", name);
        serialized.put("category", category.name());
        serialized.put("rarity", rarity.name());
        serialized.put("cost", cost);
        serialized.put("weight", weight);
        serialized.put("flavor-text", getFlavorText());
        serialized.put("minecraft-type", minecraftType.name());
        serialized.put("meta", meta);
        serialized.put("interaction", interaction);
        return serialized;
    }

    public static GenericItemType deserialize(Map<String, Object> serialized) {
        return new GenericItemType(
                (String) serialized.get("name"),
                GenericItemCategory.valueOf((String) serialized.get("category")),
                Rarity.valueOf((String) serialized.get("rarity")),
                (CurrencyValue) serialized.get("cost"),
                (Weight) serialized.get("weight"),
                (List<String>) serialized.get("flavor-text"),
                Material.valueOf((String) serialized.get("minecraft-type")),
                (ItemMeta) serialized.get("meta"),
                (Interaction) serialized.get("interaction")
        );
    }
}
