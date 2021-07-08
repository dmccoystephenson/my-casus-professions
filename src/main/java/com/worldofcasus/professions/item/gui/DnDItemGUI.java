package com.worldofcasus.professions.item.gui;

import com.rpkit.core.exception.UnregisteredServiceException;
import com.worldofcasus.professions.CasusProfessions;
import com.worldofcasus.professions.gui.GUI;
import com.worldofcasus.professions.item.CasusItemStack;
import com.worldofcasus.professions.item.CasusItemType;
import com.worldofcasus.professions.item.ItemCategory;
import com.worldofcasus.professions.item.ItemService;
import com.worldofcasus.professions.item.armor.ArmorCategory;
import com.worldofcasus.professions.item.generic.GenericItemCategory;
import com.worldofcasus.professions.item.weapon.WeaponCategory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class DnDItemGUI extends GUI {

    private static final int PAGE_SIZE = 45;

    private final CasusProfessions plugin;
    private State state;

    private ItemCategory category = null;
    private int page = 0;

    public DnDItemGUI(CasusProfessions plugin) {
        super("DnD Items");
        this.plugin = plugin;
    }

    private enum State {
        GROUP_SELECTION,
        ARMOR_CATEGORY_SELECTION,
        WEAPON_CATEGORY_SELECTION,
        OTHER_CATEGORY_SELECTION,
        TYPE_SELECTION
    }

    @Override
    public void initializeItems(Player player) {
        this.state = State.GROUP_SELECTION;
        getInventory().clear();
        ItemStack armorOption = createOption(Material.IRON_CHESTPLATE, "Armor", "Click to view armor");
        ItemStack weaponsOption = createOption(Material.IRON_SWORD, "Weapons", "Click to view weapons");
        ItemStack otherOption = createOption(Material.NETHER_STAR, "Other", "Click to view other items");
        getInventory().setItem(0, armorOption);
        getInventory().setItem(1, weaponsOption);
        getInventory().setItem(2, otherOption);
    }

    private void showArmorCategories(Player player) {
        this.state = State.ARMOR_CATEGORY_SELECTION;
        getInventory().clear();
        getInventory().setItem(0, createOption(Material.LEATHER_CHESTPLATE, "Light armor", "Click to view light armor"));
        getInventory().setItem(1, createOption(Material.IRON_CHESTPLATE, "Medium armor", "Click to view medium armor"));
        getInventory().setItem(2, createOption(Material.DIAMOND_CHESTPLATE, "Heavy armor", "Click to view heavy armor"));
        getInventory().setItem(3, createOption(Material.SHIELD, "Shields", "Click to view shields"));
        getInventory().setItem(45, createOption(Material.RED_WOOL, "Back", "Click to go back"));
    }

    private void showWeaponCategories(Player player) {
        this.state = State.WEAPON_CATEGORY_SELECTION;
        getInventory().clear();
        getInventory().setItem(0, createOption(Material.WOODEN_SWORD, "Simple Melee", "Click to view Simple Melee weapons"));
        getInventory().setItem(1, createOption(Material.IRON_SWORD, "Martial Melee", "Click to view Martial Melee weapons"));
        getInventory().setItem(2, createOption(Material.BOW, "Simple Ranged", "Click to view Simple Ranged weapons"));
        getInventory().setItem(3, createOption(Material.CROSSBOW, "Martial Ranged", "Click to view Martial Ranged weapons"));
        getInventory().setItem(45, createOption(Material.RED_WOOL, "Back", "Click to go back"));
    }

    private void showOtherCategories(Player player) {
        this.state = State.OTHER_CATEGORY_SELECTION;
        getInventory().clear();
        for (int i = 0; i < GenericItemCategory.values().length; i++) {
            GenericItemCategory category = GenericItemCategory.values()[i];
            getInventory().setItem(i, createOption(Material.NETHER_STAR, category.getName(), "Click to view " + category.getName() + " items"));
        }
        getInventory().setItem(45, createOption(Material.RED_WOOL, "Back", "Click to go back"));
    }

    private void showItems(ItemCategory category, int page) {
        this.state = State.TYPE_SELECTION;
        this.category = category;
        this.page = page;
        getInventory().clear();
        getInventory().setItem(45, createOption(Material.RED_WOOL, "Back", "Click to go back"));
        ItemService itemService;
        try {
            itemService = plugin.core.getServiceManager().getServiceProvider(ItemService.class);
        } catch (UnregisteredServiceException exception) {
            return;
        }
        List<CasusItemType> itemTypes = itemService.getItemTypes(itemType -> itemType.getCategory() == category);
        itemTypes.stream()
                .skip((long) page * PAGE_SIZE)
                .limit(PAGE_SIZE)
                .forEach(itemType -> getInventory().addItem(new CasusItemStack(itemType, 1).toBukkitItemStack()));
        if (page > 0) {
            getInventory().setItem(46, createOption(Material.PAPER, "Previous page", "Click to return to the previous page"));
        }
        if (page < itemTypes.size() / PAGE_SIZE) {
            getInventory().setItem(53, createOption(Material.PAPER, "Next page", "Click to go to the next page"));
        }
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        int slot = event.getSlot();
        if (state == State.GROUP_SELECTION) {
            if (slot == 0) {
                showArmorCategories(player);
            } else if (slot == 1) {
                showWeaponCategories(player);
            } else if (slot == 2) {
                showOtherCategories(player);
            } else if (slot == 45) {
                initializeItems(player);
            }
        } else if (state == State.ARMOR_CATEGORY_SELECTION) {
            if (slot == 0) {
                showItems(ArmorCategory.LIGHT_ARMOR, 0);
            } else if (slot == 1) {
                showItems(ArmorCategory.MEDIUM_ARMOR, 0);
            } else if (slot == 2) {
                showItems(ArmorCategory.HEAVY_ARMOR, 0);
            } else if (slot == 3) {
                showItems(ArmorCategory.SHIELD, 0);
            } else if (slot == 45) {
                initializeItems(player);
            }
        } else if (state == State.WEAPON_CATEGORY_SELECTION) {
            if (slot == 0) {
                showItems(WeaponCategory.SIMPLE_MELEE, 0);
            } else if (slot == 1) {
                showItems(WeaponCategory.MARTIAL_MELEE, 0);
            } else if (slot == 2) {
                showItems(WeaponCategory.SIMPLE_RANGED, 0);
            } else if (slot == 3) {
                showItems(WeaponCategory.MARTIAL_RANGED, 0);
            } else if (slot == 45) {
                initializeItems(player);
            }
        } else if (state == State.OTHER_CATEGORY_SELECTION) {
            if (slot >= 0 && slot < GenericItemCategory.values().length) {
                showItems(GenericItemCategory.values()[slot], 0);
            } else if (slot == 45) {
                initializeItems(player);
            }
        } else if (state == State.TYPE_SELECTION) {
            ItemService itemService;
            try {
                itemService = plugin.core.getServiceManager().getServiceProvider(ItemService.class);
            } catch (UnregisteredServiceException exception) {
                return;
            }
            List<CasusItemType> itemTypes = itemService.getItemTypes(itemType -> itemType.getCategory() == category)
                    .stream()
                    .skip((long) page * PAGE_SIZE)
                    .limit(PAGE_SIZE)
                    .collect(Collectors.toList());
            if (slot == 46 && page > 0) {
                showItems(category, page - 1);
            } else if (slot == 53 && page < itemTypes.size() / PAGE_SIZE) {
                showItems(category, page + 1);
            } else if (slot == 45) {
                if (category instanceof ArmorCategory) {
                    showArmorCategories(player);
                } else if (category instanceof WeaponCategory) {
                    showWeaponCategories(player);
                } else if (category instanceof GenericItemCategory) {
                    showOtherCategories(player);
                }
            } else if (slot >= 0 && slot < Math.min(45, itemTypes.size())) {
                CasusItemType itemType = itemTypes.get(slot);
                if (event.isLeftClick()) {
                    player.setItemOnCursor(new CasusItemStack(itemType, itemType.toBukkitItemStack().getMaxStackSize()).toBukkitItemStack());
                } else if (event.isRightClick()) {
                    player.setItemOnCursor(new CasusItemStack(itemType, 1).toBukkitItemStack());
                }
            }
        }
    }

}
