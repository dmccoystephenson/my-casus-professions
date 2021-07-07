package com.worldofcasus.professions.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.bukkit.ChatColor.WHITE;
import static org.bukkit.ChatColor.YELLOW;
import static org.bukkit.inventory.ItemFlag.*;

public abstract class GUI implements InventoryHolder {
    private final Inventory inventory;
    public GUI(String title) {
        this.inventory = Bukkit.createInventory(this, 54, title);
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public abstract void initializeItems(Player player);
    public abstract void onClick(Player player, InventoryClickEvent event);

    protected ItemStack createOption(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(WHITE + name);
            meta.setLore(Arrays.stream(lore).map(line -> YELLOW + line).collect(Collectors.toList()));
            meta.addItemFlags(
                    HIDE_ATTRIBUTES,
                    HIDE_UNBREAKABLE,
                    HIDE_PLACED_ON,
                    HIDE_POTION_EFFECTS,
                    HIDE_DESTROYS,
                    HIDE_ENCHANTS
            );
            item.setItemMeta(meta);
        }
        return item;
    }

    public void openInventory(Player player) {
        player.openInventory(getInventory());
    }
}
