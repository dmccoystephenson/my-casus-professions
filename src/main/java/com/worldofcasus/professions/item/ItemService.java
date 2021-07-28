package com.worldofcasus.professions.item;

import com.rpkit.core.service.Service;
import com.worldofcasus.professions.CasusProfessions;
import com.worldofcasus.professions.ability.StrengthRequirement;
import com.worldofcasus.professions.armorclass.ArmorClassCalculation;
import com.worldofcasus.professions.distance.Distance;
import com.worldofcasus.professions.item.armor.ArmorType;
import com.worldofcasus.professions.item.generic.GenericItemCategory;
import com.worldofcasus.professions.item.generic.GenericItemType;
import com.worldofcasus.professions.item.interaction.BlockedInteraction;
import com.worldofcasus.professions.item.weapon.WeaponDamage;
import com.worldofcasus.professions.item.weapon.WeaponProperty;
import com.worldofcasus.professions.item.weapon.WeaponType;
import com.worldofcasus.professions.money.CurrencyValue;
import com.worldofcasus.professions.roll.Roll;
import com.worldofcasus.professions.weight.Weight;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.worldofcasus.professions.ability.Ability.DEXTERITY;
import static com.worldofcasus.professions.damage.DamageType.*;
import static com.worldofcasus.professions.distance.DistanceUnit.FEET;
import static com.worldofcasus.professions.item.Rarity.*;
import static com.worldofcasus.professions.item.armor.ArmorCategory.*;
import static com.worldofcasus.professions.item.generic.GenericItemCategory.*;
import static com.worldofcasus.professions.item.weapon.WeaponCategory.*;
import static com.worldofcasus.professions.money.Currency.*;
import static com.worldofcasus.professions.weight.WeightUnit.LB;
import static com.worldofcasus.professions.weight.WeightUnit.OZ;
import static org.bukkit.Material.*;

public final class ItemService implements Service {

    private final CasusProfessions plugin;
    private final Map<String, CasusItemType> itemTypes = new HashMap<>();

    public ItemService(CasusProfessions plugin) {
        this.plugin = plugin;
        loadItemTypes();
    }

    @Override
    public CasusProfessions getPlugin() {
        return plugin;
    }

    public CasusItemType getItemType(String name) {
        return itemTypes.get(name);
    }

    public List<CasusItemType> getItemTypes() {
        return new ArrayList<>(itemTypes.values());
    }

    public List<CasusItemType> getItemTypes(Predicate<CasusItemType> predicate) {
        return getItemTypes().stream().filter(predicate).collect(Collectors.toList());
    }

    public void loadItemTypes() {
        File itemFolder = new File(plugin.getDataFolder(), "items");
        if (itemFolder.exists() && !itemFolder.isDirectory()) {
            File backupItemsFile = new File(plugin.getDataFolder(), "items.bak");
            if (!itemFolder.renameTo(backupItemsFile)) {
                plugin.getLogger().severe("A file exists at " + itemFolder.getPath() + " and could not be moved, please move it manually.");
            } else {
                plugin.getLogger().warning("A file existed at " + itemFolder.getPath() + ", it has been moved to " + backupItemsFile.getPath());
            }
        }
        if (!itemFolder.exists()) {
            if (!itemFolder.mkdirs()) {
                plugin.getLogger().severe("Could not create items folder.");
            } else {
                saveDefaultItems(itemFolder);
            }
        }
        loadItemTypes(itemFolder);
    }

    private void loadItemTypes(File folder) {
        plugin.getLogger().info("Loading item types from folder \"" + folder.getPath() + "\"...");
        for (File itemFile : folder.listFiles()) {
            if (itemFile.isDirectory()) {
                loadItemTypes(itemFile);
            } else if (itemFile.getName().endsWith(".yml")) {
                YamlConfiguration itemConfiguration = YamlConfiguration.loadConfiguration(itemFile);
                CasusItemType itemType = (CasusItemType) itemConfiguration.get("item");
                if (itemType != null) {
                    itemTypes.put(itemType.getName(), itemType);
                    plugin.getLogger().info("Loaded item " + itemType.getName() + " from " + itemFile.getPath());
                } else {
                    plugin.getLogger().severe("Failed to load item from " + itemFile.getPath());
                }
            }
        }
    }

    private void saveDefaultItems(File itemFolder) {
        // Armor
        File armorFolder = new File(itemFolder, "armor");
        saveItem(armorFolder, new ArmorType(
            "Leather Armor",
                COMMON,
                LIGHT_ARMOR,
                new CurrencyValue(
                        10,
                        GP
                ),
                new ArmorClassCalculation(
                        new ArmorClassCalculation.BaseArmorClassComponent(11),
                        new ArmorClassCalculation.AbilityModifierArmorClassComponent(DEXTERITY)
                ),
                null,
                false,
                new Weight(10, LB),
                false,
                Arrays.asList(
                        "Usually what Hunters wear."
                ),
                LEATHER_CHESTPLATE,
                new BlockedInteraction()
        ));
        saveItem(armorFolder, new ArmorType(
                "Leather Lamellar Armor",
                COMMON,
                LIGHT_ARMOR,
                new CurrencyValue(45, GP),
                new ArmorClassCalculation(
                        new ArmorClassCalculation.BaseArmorClassComponent(12),
                        new ArmorClassCalculation.AbilityModifierArmorClassComponent(DEXTERITY)
                ),
                null,
                false,
                new Weight(13, LB),
                false,
                Arrays.asList(
                        "Usually worn by light infantry or archers."
                ),
                LEATHER_CHESTPLATE,
                new BlockedInteraction()
        ));
        saveItem(armorFolder, new ArmorType(
                "Studded Leather Armor",
                COMMON,
                LIGHT_ARMOR,
                new CurrencyValue(45, GP),
                new ArmorClassCalculation(
                        new ArmorClassCalculation.BaseArmorClassComponent(12),
                        new ArmorClassCalculation.CappedAbilityModifierArmorClassComponent(DEXTERITY, 2)
                ),
                null,
                false,
                new Weight(13, LB),
                true,
                Arrays.asList(
                        "Tough and flexible leather ",
                        "chest piece and gauntlets reinforced",
                        "with close-set rivets."
                ),
                LEATHER_CHESTPLATE,
                new BlockedInteraction()
        ));
        saveItem(armorFolder, new ArmorType(
                "Chain Shirt",
                COMMON,
                MEDIUM_ARMOR,
                new CurrencyValue(50, GP),
                new ArmorClassCalculation(
                        new ArmorClassCalculation.BaseArmorClassComponent(13),
                        new ArmorClassCalculation.CappedAbilityModifierArmorClassComponent(DEXTERITY, 2)
                ),
                null,
                false,
                new Weight(20, LB),
                true,
                Arrays.asList(
                        "Usually worn by city guards",
                        "and common infantrymen"
                ),
                CHAINMAIL_CHESTPLATE,
                new BlockedInteraction()
        ));
        saveItem(armorFolder, new ArmorType(
                "Scale Mail",
                COMMON,
                MEDIUM_ARMOR,
                new CurrencyValue(50, GP),
                new ArmorClassCalculation(
                        new ArmorClassCalculation.BaseArmorClassComponent(14),
                        new ArmorClassCalculation.CappedAbilityModifierArmorClassComponent(DEXTERITY, 2)
                ),
                null,
                true,
                new Weight(45, LB),
                true,
                Arrays.asList(
                        "A chestpiece consisting",
                        "of many small individual",
                        "steel 'scale' plates with",
                        "full chainmail sleeves."
                ),
                CHAINMAIL_CHESTPLATE,
                new BlockedInteraction()
        ));
        saveItem(armorFolder, new ArmorType(
                "Breastplate",
                COMMON,
                MEDIUM_ARMOR,
                new CurrencyValue(400, GP),
                new ArmorClassCalculation(
                        new ArmorClassCalculation.BaseArmorClassComponent(14),
                        new ArmorClassCalculation.CappedAbilityModifierArmorClassComponent(DEXTERITY, 2)
                ),
                null,
                false,
                new Weight(20, LB),
                true,
                Arrays.asList(
                        "A fitted metal chest piece",
                        "with flexible leather parts.",
                        "Relatively light and protects",
                        "vital organs, but leaves extremities",
                        "exposed."
                ),
                CHAINMAIL_CHESTPLATE,
                new BlockedInteraction()
        ));
        saveItem(armorFolder, new ArmorType(
                "Half-Plate Armor",
                COMMON,
                MEDIUM_ARMOR,
                new CurrencyValue(750, GP),
                new ArmorClassCalculation(
                        new ArmorClassCalculation.BaseArmorClassComponent(15),
                        new ArmorClassCalculation.CappedAbilityModifierArmorClassComponent(DEXTERITY, 2)
                ),
                null,
                true,
                new Weight(40, LB),
                true,
                Arrays.asList(
                        "A chestpiece consisting",
                        "of shaped metal plates",
                        "that cover a majority of",
                        "the body."
                ),
                IRON_CHESTPLATE,
                new BlockedInteraction()
        ));
        saveItem(armorFolder, new ArmorType(
                "Chain Hauberk",
                COMMON,
                HEAVY_ARMOR,
                new CurrencyValue(75, GP),
                new ArmorClassCalculation(
                        new ArmorClassCalculation.BaseArmorClassComponent(16)
                ),
                new StrengthRequirement(13),
                true,
                new Weight(55, LB),
                true,
                Arrays.asList(
                        "A full coat of steel",
                        "linked chains covering",
                        "the torso. Also has full",
                        "sleeves of mail and a ",
                        "head piece."
                ),
                CHAINMAIL_CHESTPLATE,
                new BlockedInteraction()
        ));
        saveItem(armorFolder, new ArmorType(
                "Splint",
                COMMON,
                HEAVY_ARMOR,
                new CurrencyValue(200, GP),
                new ArmorClassCalculation(
                        new ArmorClassCalculation.BaseArmorClassComponent(17)
                ),
                new StrengthRequirement(15),
                true,
                new Weight(60, LB),
                true,
                Arrays.asList(
                        "Armor made up of over-",
                        "lapping strips of metal,",
                        "fastened to a sturdy",
                        "backing of leather and",
                        "chainmail."
                ),
                IRON_CHESTPLATE,
                new BlockedInteraction()
        ));
        saveItem(armorFolder, new ArmorType(
                "Full-Plate Armor",
                COMMON,
                HEAVY_ARMOR,
                new CurrencyValue(1500, GP),
                new ArmorClassCalculation(
                        new ArmorClassCalculation.BaseArmorClassComponent(18)
                ),
                new StrengthRequirement(15),
                true,
                new Weight(65, LB),
                true,
                Arrays.asList(
                        "A full set of personal",
                        "body armor made up of",
                        "steel plates fastened",
                        "onto an underlying",
                        "Gambeson and chainmail."
                ),
                IRON_CHESTPLATE,
                new BlockedInteraction()
        ));
        saveItem(armorFolder, new ArmorType(
                "Adamantine Armor",
                COMMON,
                HEAVY_ARMOR,
                null,
                new ArmorClassCalculation(
                        new ArmorClassCalculation.BaseArmorClassComponent(18)
                ),
                new StrengthRequirement(18),
                true,
                new Weight(65, LB),
                true,
                Arrays.asList(
                        "The toughest armor there is, it is made with adamantium, the strongest metal discovered.",
                        "To find such metal, miners must reach the deepest, and most dangerous, caves.",
                        "With this armor, any critical hit becomes normal."
                ),
                DIAMOND_CHESTPLATE,
                new BlockedInteraction()
        ));
        // Weapons
        File weaponFolder = new File(itemFolder, "weapons");
        ItemMeta knuckleDusterMeta = Bukkit.getItemFactory().getItemMeta(WOODEN_SWORD);
        if (knuckleDusterMeta != null) {
            knuckleDusterMeta.setCustomModelData(100);
        }
        saveItem(weaponFolder, new WeaponType(
                "Knuckle Duster",
                COMMON,
                SIMPLE_MELEE,
                new CurrencyValue(1, SP),
                new WeaponDamage(new Roll(new Roll.Die(1, 4)), BLUDGEONING),
                new Weight(2, LB),
                Arrays.asList(
                        new WeaponProperty.Light()
                ),
                Arrays.asList(
                        "Perfect for tavern brawls!"
                ),
                WOODEN_SWORD,
                knuckleDusterMeta,
                new BlockedInteraction()
        ));
        ItemMeta spikedClubMeta = Bukkit.getItemFactory().getItemMeta(WOODEN_SWORD);
        if (spikedClubMeta != null) {
            spikedClubMeta.setCustomModelData(111);
        }
        saveItem(weaponFolder, new WeaponType(
                "Spiked Club",
                COMMON,
                SIMPLE_MELEE,
                new CurrencyValue(1, SP),
                new WeaponDamage(new Roll(new Roll.Die(1, 4)), BLUDGEONING),
                new Weight(2, LB),
                Arrays.asList(
                        new WeaponProperty.Light()
                ),
                Arrays.asList(
                        "During arguments, peasants have the last",
                        "word with these..."
                ),
                WOODEN_SWORD,
                spikedClubMeta,
                new BlockedInteraction()
        ));
        ItemMeta lightCrossbowMeta = Bukkit.getItemFactory().getItemMeta(CROSSBOW);
        if (lightCrossbowMeta != null) {
            lightCrossbowMeta.setCustomModelData(901);
        }
        saveItem(weaponFolder, new WeaponType(
                "Light Crossbow",
                COMMON,
                SIMPLE_RANGED,
                new CurrencyValue(25, GP),
                new WeaponDamage(new Roll(new Roll.Die(1, 8)), PIERCING),
                new Weight(5, LB),
                Arrays.asList(
                        new WeaponProperty.Ammunition(),
                        new WeaponProperty.Range(new Distance(80, FEET), new Distance(320, FEET)),
                        new WeaponProperty.Loading(),
                        new WeaponProperty.TwoHanded()
                ),
                Arrays.asList(
                        "Usually used by city guards or common",
                        "mercenaries."
                ),
                CROSSBOW,
                lightCrossbowMeta,
                new BlockedInteraction()
        ));
        ItemMeta heavyCrossbowMeta = Bukkit.getItemFactory().getItemMeta(CROSSBOW);
        if (heavyCrossbowMeta != null) {
            heavyCrossbowMeta.setCustomModelData(900);
        }
        saveItem(weaponFolder, new WeaponType(
                "Heavy Crossbow",
                COMMON,
                MARTIAL_RANGED,
                new CurrencyValue(50, GP),
                new WeaponDamage(new Roll(new Roll.Die(1, 10)), PIERCING),
                new Weight(18, LB),
                Arrays.asList(
                        new WeaponProperty.Ammunition(),
                        new WeaponProperty.Range(new Distance(100, FEET), new Distance(400, FEET)),
                        new WeaponProperty.Heavy(),
                        new WeaponProperty.Loading(),
                        new WeaponProperty.TwoHanded()
                ),
                Arrays.asList(
                        "This bad boy can pierce even the toughest of armors."
                ),
                CROSSBOW,
                heavyCrossbowMeta,
                new BlockedInteraction()
        ));
        ItemMeta daggerMeta = Bukkit.getItemFactory().getItemMeta(WOODEN_SWORD);
        if (daggerMeta != null) {
            daggerMeta.setCustomModelData(104);
        }
        saveItem(weaponFolder, new WeaponType(
                "Dagger",
                COMMON,
                SIMPLE_MELEE,
                new CurrencyValue(2, GP),
                new WeaponDamage(new Roll(new Roll.Die(1, 4)), SLASHING),
                new Weight(1, LB),
                Arrays.asList(
                        new WeaponProperty.Finesse(),
                        new WeaponProperty.Light(),
                        new WeaponProperty.Thrown(),
                        new WeaponProperty.Range(new Distance(20, FEET), new Distance(60, FEET))
                ),
                Arrays.asList(
                        "The favorite weapon of the shady folk."
                ),
                WOODEN_SWORD,
                daggerMeta,
                new BlockedInteraction()
        ));
        ItemMeta handaxeMeta = Bukkit.getItemFactory().getItemMeta(WOODEN_AXE);
        if (handaxeMeta != null) {
            handaxeMeta.setCustomModelData(201);
        }
        saveItem(weaponFolder, new WeaponType(
                "Handaxe",
                COMMON,
                SIMPLE_MELEE,
                new CurrencyValue(5, GP),
                new WeaponDamage(new Roll(new Roll.Die(1, 6)), SLASHING),
                new Weight(2, LB),
                Arrays.asList(
                        new WeaponProperty.Light(),
                        new WeaponProperty.Thrown(),
                        new WeaponProperty.Range(new Distance(20, FEET), new Distance(60, FEET))
                ),
                Arrays.asList(
                        "The favorite weapon for raiders."
                ),
                WOODEN_AXE,
                handaxeMeta,
                new BlockedInteraction()
        ));
        ItemMeta javelinMeta = Bukkit.getItemFactory().getItemMeta(STONE_SWORD);
        if (javelinMeta != null) {
            javelinMeta.setCustomModelData(307);
        }
        saveItem(weaponFolder, new WeaponType(
                "Javelin",
                COMMON,
                SIMPLE_MELEE,
                new CurrencyValue(5, SP),
                new WeaponDamage(new Roll(new Roll.Die(1, 6)), PIERCING),
                new Weight(2, LB),
                Arrays.asList(
                        new WeaponProperty.Thrown(),
                        new WeaponProperty.Range(new Distance(30, FEET), new Distance(120, FEET))
                ),
                Arrays.asList(
                        "Like a spear, but smaller, and you can throw",
                        "it!"
                ),
                STONE_SWORD,
                javelinMeta,
                new BlockedInteraction()
        ));
        ItemMeta lightHammerMeta = Bukkit.getItemFactory().getItemMeta(STONE_AXE);
        if (lightHammerMeta != null) {
            lightHammerMeta.setCustomModelData(402);
        }
        saveItem(weaponFolder, new WeaponType(
                "Light Hammer",
                COMMON,
                SIMPLE_MELEE,
                new CurrencyValue(2, GP),
                new WeaponDamage(new Roll(new Roll.Die(1, 4)), BLUDGEONING),
                new Weight(2, LB),
                Arrays.asList(
                        new WeaponProperty.Light(),
                        new WeaponProperty.Thrown(),
                        new WeaponProperty.Range(new Distance(20, FEET), new Distance(60, FEET))
                ),
                Arrays.asList(
                        "Wait, you can throw these?"
                ),
                STONE_AXE,
                lightHammerMeta,
                new BlockedInteraction()
        ));
        ItemMeta maceMeta = Bukkit.getItemFactory().getItemMeta(STONE_AXE);
        if (maceMeta != null) {
            maceMeta.setCustomModelData(404);
        }
        saveItem(weaponFolder, new WeaponType(
                "Mace",
                COMMON,
                SIMPLE_MELEE,
                new CurrencyValue(5, GP),
                new WeaponDamage(new Roll(new Roll.Die(1, 6)), BLUDGEONING),
                new Weight(4, LB),
                Collections.emptyList(),
                Arrays.asList(
                        "Mace in yer face!"
                ),
                STONE_AXE,
                maceMeta,
                new BlockedInteraction()
        ));
        ItemMeta shortBowMeta = Bukkit.getItemFactory().getItemMeta(BOW);
        if (shortBowMeta != null) {
            shortBowMeta.setCustomModelData(802);
        }
        saveItem(weaponFolder, new WeaponType(
                "Shortbow",
                COMMON,
                SIMPLE_RANGED,
                new CurrencyValue(25, GP),
                new WeaponDamage(new Roll(new Roll.Die(1, 6)), PIERCING),
                new Weight(2, LB),
                Arrays.asList(
                        new WeaponProperty.Ammunition(),
                        new WeaponProperty.Range(new Distance(80, FEET), new Distance(320, FEET)),
                        new WeaponProperty.TwoHanded()
                ),
                Arrays.asList(
                        "Usually used by poachers."
                ),
                BOW,
                shortBowMeta,
                new BlockedInteraction()
        ));
        ItemMeta longBowMeta = Bukkit.getItemFactory().getItemMeta(BOW);
        if (longBowMeta != null) {
            longBowMeta.setCustomModelData(800);
        }
        saveItem(weaponFolder, new WeaponType(
                "Longbow",
                COMMON,
                MARTIAL_RANGED,
                new CurrencyValue(15, GP),
                new WeaponDamage(new Roll(new Roll.Die(1, 8)), PIERCING),
                new Weight(2, LB),
                Arrays.asList(
                        new WeaponProperty.Ammunition(),
                        new WeaponProperty.Range(new Distance(150, FEET), new Distance(600, FEET)),
                        new WeaponProperty.Heavy(),
                        new WeaponProperty.TwoHanded()
                ),
                Arrays.asList(
                        "A skilled archer can do some real damage",
                        "with this bad boy."
                ),
                BOW,
                longBowMeta,
                new BlockedInteraction()
        ));
        ItemMeta shortswordMeta = Bukkit.getItemFactory().getItemMeta(STONE_SWORD);
        if (shortswordMeta != null) {
            shortswordMeta.setCustomModelData(300);
        }
        saveItem(weaponFolder, new WeaponType(
                "Shortsword",
                COMMON,
                MARTIAL_MELEE,
                new CurrencyValue(10, GP),
                new WeaponDamage(new Roll(new Roll.Die(1, 6)), SLASHING),
                new Weight(2, LB),
                Arrays.asList(
                        new WeaponProperty.Finesse(),
                        new WeaponProperty.Light()
                ),
                Arrays.asList(
                        "These are personal favorites for thieves."
                ),
                STONE_SWORD,
                shortswordMeta,
                new BlockedInteraction()
        ));
        ItemMeta scimitarMeta = Bukkit.getItemFactory().getItemMeta(STONE_SWORD);
        if (scimitarMeta != null) {
            scimitarMeta.setCustomModelData(304);
        }
        saveItem(weaponFolder, new WeaponType(
                "Scimitar",
                COMMON,
                MARTIAL_MELEE,
                new CurrencyValue(25, GP),
                new WeaponDamage(new Roll(new Roll.Die(1, 6)), SLASHING),
                new Weight(3, LB),
                Arrays.asList(
                        new WeaponProperty.Finesse(),
                        new WeaponProperty.Light()
                ),
                Arrays.asList(
                        "These are personal favorites for thieves."
                ),
                STONE_SWORD,
                scimitarMeta,
                new BlockedInteraction()
        ));
        ItemMeta longswordMeta = Bukkit.getItemFactory().getItemMeta(STONE_SWORD);
        if (longswordMeta != null) {
            longswordMeta.setCustomModelData(302);
        }
        saveItem(weaponFolder, new WeaponType(
                "Longsword",
                COMMON,
                MARTIAL_MELEE,
                new CurrencyValue(15, GP),
                new WeaponDamage(new Roll(new Roll.Die(1, 8)), SLASHING),
                new Weight(3, LB),
                Arrays.asList(
                        new WeaponProperty.Versatile(new Roll(new Roll.Die(1, 10)))
                ),
                Arrays.asList(
                        "The favorite weapon of a knight."
                ),
                STONE_SWORD,
                longswordMeta,
                new BlockedInteraction()
        ));
        saveItem(weaponFolder, new WeaponType(
                "War pick",
                COMMON,
                MARTIAL_MELEE,
                new CurrencyValue(5, GP),
                new WeaponDamage(new Roll(new Roll.Die(1, 8)), PIERCING),
                new Weight(2, LB),
                Collections.emptyList(),
                Arrays.asList(
                        "You'll regret insulting the miner now!"
                ),
                DIAMOND_HOE,
                new BlockedInteraction()
        ));
        ItemMeta rapierMeta = Bukkit.getItemFactory().getItemMeta(WOODEN_SWORD);
        if (rapierMeta != null) {
            rapierMeta.setCustomModelData(105);
        }
        saveItem(weaponFolder, new WeaponType(
                "Rapier",
                COMMON,
                MARTIAL_MELEE,
                new CurrencyValue(25, GP),
                new WeaponDamage(new Roll(new Roll.Die(1, 8)), PIERCING),
                new Weight(2, LB),
                Arrays.asList(
                        new WeaponProperty.Finesse()
                ),
                Arrays.asList(
                        "Touché!"
                ),
                WOODEN_SWORD,
                rapierMeta,
                new BlockedInteraction()
        ));
        ItemMeta handCrossbowMeta = Bukkit.getItemFactory().getItemMeta(CROSSBOW);
        if (handCrossbowMeta != null) {
            handCrossbowMeta.setCustomModelData(902);
        }
        saveItem(weaponFolder, new WeaponType(
                "Hand Crossbow",
                COMMON,
                MARTIAL_RANGED,
                new CurrencyValue(75, GP),
                new WeaponDamage(new Roll(new Roll.Die(1, 6)), PIERCING),
                new Weight(3, LB),
                Arrays.asList(
                        new WeaponProperty.Ammunition(),
                        new WeaponProperty.Range(new Distance(30, FEET), new Distance(120, FEET)),
                        new WeaponProperty.Light(),
                        new WeaponProperty.Loading()
                ),
                Arrays.asList(
                        "Will surely come in handy..."
                ),
                CROSSBOW,
                handCrossbowMeta,
                new BlockedInteraction()
        ));
        ItemMeta quarterstaffMeta = Bukkit.getItemFactory().getItemMeta(STONE_SWORD);
        if (quarterstaffMeta != null) {
            quarterstaffMeta.setCustomModelData(308);
        }
        saveItem(weaponFolder, new WeaponType(
                "Quarterstaff",
                COMMON,
                SIMPLE_MELEE,
                new CurrencyValue(2, SP),
                new WeaponDamage(new Roll(new Roll.Die(1, 6)), BLUDGEONING),
                new Weight(4, LB),
                Arrays.asList(
                        new WeaponProperty.Versatile(new Roll(new Roll.Die(1, 8)))
                ),
                Arrays.asList(
                        "It's not just a \"walking stick\", Gandalf..."
                ),
                STONE_SWORD,
                quarterstaffMeta,
                new BlockedInteraction()
        ));
        ItemMeta boStaffMeta = Bukkit.getItemFactory().getItemMeta(STONE_SWORD);
        if (boStaffMeta != null) {
            boStaffMeta.setCustomModelData(308);
        }
        saveItem(weaponFolder, new WeaponType(
                "Bo Staff",
                COMMON,
                SIMPLE_MELEE,
                new CurrencyValue(2, SP),
                new WeaponDamage(new Roll(new Roll.Die(1, 6)), BLUDGEONING),
                new Weight(4, LB),
                Arrays.asList(
                        new WeaponProperty.Versatile(new Roll(new Roll.Die(1, 8)))
                ),
                Arrays.asList(
                        ChatColor.STRIKETHROUGH + "YOU SHALL NOT PASS" + ChatColor.RESET + "Shoot, wrong franchise."
                ),
                STONE_SWORD,
                boStaffMeta,
                new BlockedInteraction()
        ));
        ItemMeta whipMeta = Bukkit.getItemFactory().getItemMeta(WOODEN_SWORD);
        if (whipMeta != null) {
            whipMeta.setCustomModelData(106);
        }
        saveItem(weaponFolder, new WeaponType(
                "Whip",
                COMMON,
                MARTIAL_MELEE,
                new CurrencyValue(2, GP),
                new WeaponDamage(new Roll(new Roll.Die(1, 4)), SLASHING),
                new Weight(3, LB),
                Arrays.asList(
                        new WeaponProperty.Finesse(),
                        new WeaponProperty.Reach()
                ),
                Arrays.asList(
                        "Now watch me whip..."
                ),
                WOODEN_SWORD,
                whipMeta,
                new BlockedInteraction()
        ));
        ItemMeta warhammerMeta = Bukkit.getItemFactory().getItemMeta(STONE_AXE);
        if (warhammerMeta != null) {
            warhammerMeta.setCustomModelData(403);
        }
        saveItem(weaponFolder, new WeaponType(
                "Warhammer",
                COMMON,
                MARTIAL_MELEE,
                new CurrencyValue(15, GP),
                new WeaponDamage(new Roll(new Roll.Die(1, 8)), BLUDGEONING),
                new Weight(2, LB),
                Arrays.asList(
                        new WeaponProperty.Versatile(new Roll(new Roll.Die(1, 10)))
                ),
                Arrays.asList(
                        "Perfect for flattening heads!"
                ),
                STONE_AXE,
                warhammerMeta,
                new BlockedInteraction()
        ));
        ItemMeta sickleMeta = Bukkit.getItemFactory().getItemMeta(STONE_SWORD);
        if (sickleMeta != null) {
            sickleMeta.setCustomModelData(301);
        }
        saveItem(weaponFolder, new WeaponType(
                "Sickle",
                COMMON,
                SIMPLE_MELEE,
                new CurrencyValue(1, GP),
                new WeaponDamage(new Roll(new Roll.Die(1, 4)), SLASHING),
                new Weight(2, LB),
                Arrays.asList(
                        new WeaponProperty.Light()
                ),
                Arrays.asList(
                        "Usually used to harvest wheat... usually."
                ),
                STONE_SWORD,
                sickleMeta,
                new BlockedInteraction()
        ));
        ItemMeta morningstarMeta = Bukkit.getItemFactory().getItemMeta(BOW);
        if (morningstarMeta != null) {
            morningstarMeta.setCustomModelData(405);
        }
        saveItem(weaponFolder, new WeaponType(
                "Morningstar",
                COMMON,
                MARTIAL_MELEE,
                new CurrencyValue(15, GP),
                new WeaponDamage(new Roll(new Roll.Die(1, 8)), BLUDGEONING),
                new Weight(4, LB),
                Collections.emptyList(),
                Arrays.asList(
                        "It's always a good morning with one of these babies in your hand!"
                ),
                STONE_AXE,
                morningstarMeta,
                new BlockedInteraction()
        ));
        ItemMeta slingshotMeta = Bukkit.getItemFactory().getItemMeta(WOODEN_SWORD);
        if (slingshotMeta != null) {
            slingshotMeta.setCustomModelData(112);
        }
        saveItem(weaponFolder, new WeaponType(
                "Slingshot",
                COMMON,
                SIMPLE_RANGED,
                new CurrencyValue(1, SP),
                new WeaponDamage(new Roll(new Roll.Die(1, 4)), BLUDGEONING),
                new Weight(0, LB),
                Arrays.asList(
                        new WeaponProperty.Ammunition(),
                        new WeaponProperty.Range(new Distance(30, FEET), new Distance(120, FEET))
                ),
                Arrays.asList(
                        "Used by town kids to break glass windows."
                ),
                WOODEN_SWORD,
                slingshotMeta,
                new BlockedInteraction()
        ));
        ItemMeta yklwaMeta = Bukkit.getItemFactory().getItemMeta(STONE_SWORD);
        if (yklwaMeta != null) {
            yklwaMeta.setCustomModelData(312);
        }
        saveItem(weaponFolder, new WeaponType(
                "Yklwa",
                COMMON,
                SIMPLE_MELEE,
                new CurrencyValue(1, GP),
                new WeaponDamage(new Roll(new Roll.Die(1, 8)), SLASHING),
                new Weight(3, LB),
                Arrays.asList(
                        new WeaponProperty.Thrown(),
                        new WeaponProperty.Range(new Distance(10, FEET), new Distance(30, FEET))
                ),
                Arrays.asList(
                        "This odd design for a weapon can be thrown, but is not fit for it"
                ),
                STONE_SWORD,
                yklwaMeta,
                new BlockedInteraction()
        ));
        ItemMeta battleaxeMeta = Bukkit.getItemFactory().getItemMeta(STONE_SWORD);
        if (battleaxeMeta != null) {
            battleaxeMeta.setCustomModelData(400);
        }
        saveItem(weaponFolder, new WeaponType(
                "Battleaxe",
                COMMON,
                MARTIAL_MELEE,
                new CurrencyValue(10, GP),
                new WeaponDamage(new Roll(new Roll.Die(1, 8)), SLASHING),
                new Weight(4, LB),
                Arrays.asList(
                        new WeaponProperty.Versatile(new Roll(new Roll.Die(1, 10)))
                ),
                Arrays.asList(
                        "Orks love this weapon!"
                ),
                STONE_SWORD,
                battleaxeMeta,
                new BlockedInteraction()
        ));
        ItemMeta halberdMeta = Bukkit.getItemFactory().getItemMeta(IRON_SWORD);
        if (halberdMeta != null) {
            halberdMeta.setCustomModelData(504);
        }
        saveItem(weaponFolder, new WeaponType(
                "Halberd",
                COMMON,
                MARTIAL_MELEE,
                new CurrencyValue(20, GP),
                new WeaponDamage(new Roll(new Roll.Die(1, 10)), SLASHING),
                new Weight(6, LB),
                Arrays.asList(
                        new WeaponProperty.Heavy(),
                        new WeaponProperty.Reach(),
                        new WeaponProperty.TwoHanded()
                ),
                Arrays.asList(
                        "Poke or swing with it, it's great for both!"
                ),
                IRON_SWORD,
                halberdMeta,
                new BlockedInteraction()
        ));
        ItemMeta pikeMeta = Bukkit.getItemFactory().getItemMeta(IRON_SWORD);
        if (pikeMeta != null) {
            pikeMeta.setCustomModelData(508);
        }
        saveItem(weaponFolder, new WeaponType(
                "Pike",
                COMMON,
                MARTIAL_MELEE,
                new CurrencyValue(5, GP),
                new WeaponDamage(new Roll(new Roll.Die(1, 10)), PIERCING),
                new Weight(18, LB),
                Arrays.asList(
                        new WeaponProperty.Heavy(),
                        new WeaponProperty.Reach(),
                        new WeaponProperty.TwoHanded()
                ),
                Arrays.asList(
                        "Pikes are perfect to keep the enemy at bay"
                ),
                IRON_SWORD,
                pikeMeta,
                new BlockedInteraction()
        ));
        ItemMeta lanceMeta = Bukkit.getItemFactory().getItemMeta(IRON_SWORD);
        if (lanceMeta != null) {
            lanceMeta.setCustomModelData(514);
        }
        saveItem(weaponFolder, new WeaponType(
                "Lance",
                COMMON,
                MARTIAL_MELEE,
                new CurrencyValue(10, GP),
                new WeaponDamage(new Roll(new Roll.Die(1, 12)), PIERCING),
                new Weight(6, LB),
                Arrays.asList(
                        new WeaponProperty.Reach(),
                        new WeaponProperty.Special("You have a disadvantage when you use a lance to attack a target within 5 feet of you. Also, a lance requires two hands to wield when you aren't mounted.")
                ),
                Arrays.asList(
                        "Best used while mounted",
                        "If mounted you can use it one-handed instead.",
                        "If you hit an enemy while mounted and have",
                        "traveled more than 3 blocks, you add a 1d10 to",
                        "damage roll."
                ),
                IRON_SWORD,
                lanceMeta,
                new BlockedInteraction()
        ));
        ItemMeta spearMeta = Bukkit.getItemFactory().getItemMeta(IRON_SWORD);
        if (spearMeta != null) {
            spearMeta.setCustomModelData(502);
        }
        saveItem(weaponFolder, new WeaponType(
                "Spear",
                COMMON,
                SIMPLE_MELEE,
                new CurrencyValue(1, GP),
                new WeaponDamage(new Roll(new Roll.Die(1, 6)), PIERCING),
                new Weight(3, LB),
                Arrays.asList(
                        new WeaponProperty.Thrown(),
                        new WeaponProperty.Range(new Distance(20, FEET), new Distance(60, FEET)),
                        new WeaponProperty.Versatile(new Roll(new Roll.Die(1, 8)))
                ),
                Arrays.asList(
                        "A good weapon to have against most foes."
                ),
                IRON_SWORD,
                spearMeta,
                new BlockedInteraction()
        ));
        ItemMeta maulMeta = Bukkit.getItemFactory().getItemMeta(IRON_AXE);
        if (maulMeta != null) {
            maulMeta.setCustomModelData(603);
        }
        saveItem(weaponFolder, new WeaponType(
                "Maul",
                COMMON,
                MARTIAL_MELEE,
                new CurrencyValue(10, GP),
                new WeaponDamage(new Roll(new Roll.Die(2, 6)), BLUDGEONING),
                new Weight(10, LB),
                Arrays.asList(
                        new WeaponProperty.Heavy(),
                        new WeaponProperty.TwoHanded()
                ),
                Arrays.asList(
                    "Dwarves love this weapon, great for busting kneecaps."
                ),
                IRON_AXE,
                maulMeta,
                new BlockedInteraction()
        ));
        ItemMeta greatswordMeta = Bukkit.getItemFactory().getItemMeta(IRON_SWORD);
        if (greatswordMeta != null) {
            greatswordMeta.setCustomModelData(500);
        }
        saveItem(weaponFolder, new WeaponType(
                "Greatsword",
                COMMON,
                MARTIAL_MELEE,
                new CurrencyValue(50, GP),
                new WeaponDamage(new Roll(new Roll.Die(2, 6)), SLASHING),
                new Weight(6, LB),
                Arrays.asList(
                        new WeaponProperty.Heavy(),
                        new WeaponProperty.TwoHanded()
                ),
                Arrays.asList(
                        "So sharp you could probably cleave someone in half..."
                ),
                IRON_SWORD,
                greatswordMeta,
                new BlockedInteraction()
        ));
        ItemMeta greataxeMeta = Bukkit.getItemFactory().getItemMeta(IRON_AXE);
        if (greataxeMeta != null) {
            greataxeMeta.setCustomModelData(601);
        }
        saveItem(weaponFolder, new WeaponType(
                "Greataxe",
                COMMON,
                MARTIAL_MELEE,
                new CurrencyValue(30, GP),
                new WeaponDamage(new Roll(new Roll.Die(1, 12)), SLASHING),
                new Weight(7, LB),
                Arrays.asList(
                        new WeaponProperty.Heavy(),
                        new WeaponProperty.TwoHanded()
                ),
                Arrays.asList(
                        "This massive axe is great for chopping body parts."
                ),
                IRON_AXE,
                greataxeMeta,
                new BlockedInteraction()
        ));
        ItemMeta glaiveMeta = Bukkit.getItemFactory().getItemMeta(IRON_AXE);
        if (glaiveMeta != null) {
            glaiveMeta.setCustomModelData(605);
        }
        saveItem(weaponFolder, new WeaponType(
                "Glaive",
                COMMON,
                MARTIAL_MELEE,
                new CurrencyValue(20, GP),
                new WeaponDamage(new Roll(new Roll.Die(1, 10)), SLASHING),
                new Weight(6, LB),
                Arrays.asList(
                        new WeaponProperty.Heavy(),
                        new WeaponProperty.Reach(),
                        new WeaponProperty.TwoHanded()
                ),
                Arrays.asList(
                        "Swingity swungity... you're dead!"
                ),
                IRON_AXE,
                glaiveMeta,
                new BlockedInteraction()
        ));
        ItemMeta greatBattlehammerMeta = Bukkit.getItemFactory().getItemMeta(IRON_AXE);
        if (greatBattlehammerMeta != null) {
            greatBattlehammerMeta.setCustomModelData(602);
        }
        saveItem(weaponFolder, new WeaponType(
                "Great Battlehammer",
                UNCOMMON,
                MARTIAL_MELEE,
                new CurrencyValue(15, GP),
                new WeaponDamage(new Roll(new Roll.Die(2, 6)), BLUDGEONING),
                new Weight(2, LB),
                Arrays.asList(
                        new WeaponProperty.TwoHanded()
                ),
                Arrays.asList(
                        "A MASSIVE hammer... what more do ya need?"
                ),
                IRON_AXE,
                greatBattlehammerMeta,
                new BlockedInteraction()
        ));
        ItemMeta dwarvenGreatswordMeta = Bukkit.getItemFactory().getItemMeta(IRON_SWORD);
        if (dwarvenGreatswordMeta != null) {
            dwarvenGreatswordMeta.setCustomModelData(513);
        }
        saveItem(weaponFolder, new WeaponType(
                "Dwarven Greatsword",
                UNCOMMON,
                MARTIAL_MELEE,
                new CurrencyValue(75, GP),
                new WeaponDamage(new Roll(new Roll.Die(2, 6)), SLASHING),
                new Weight(6, LB),
                Arrays.asList(
                        new WeaponProperty.TwoHanded()
                ),
                Arrays.asList(
                        "A greatsword of Dwarvish design.",
                        "It's about the length of a longsword for the",
                        "average human."
                ),
                IRON_SWORD,
                dwarvenGreatswordMeta,
                new BlockedInteraction()
        ));
        ItemMeta jadeDaggerMeta = Bukkit.getItemFactory().getItemMeta(WOODEN_SWORD);
        if (jadeDaggerMeta != null) {
            jadeDaggerMeta.setCustomModelData(104);
        }
        saveItem(weaponFolder, new WeaponType(
                "Jade Dagger",
                RARE,
                SIMPLE_MELEE,
                null,
                new WeaponDamage(new Roll(new Roll.Die(1, 4)), SLASHING),
                new Weight(1, LB),
                Arrays.asList(
                        new WeaponProperty.Finesse(),
                        new WeaponProperty.Light(),
                        new WeaponProperty.Thrown(),
                        new WeaponProperty.Range(new Distance(20, FEET), new Distance(60, FEET))
                ),
                Arrays.asList(
                        "The favorite weapon of the shady folk",
                        "Misses every hit."
                ),
                WOODEN_SWORD,
                jadeDaggerMeta,
                new BlockedInteraction()
        ));
        // Adventuring gear
        File adventuringGearFolder = new File(itemFolder, "adventuring_gear");
        saveItem(adventuringGearFolder, new GenericItemType(
                "Abacus",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(2, GP),
                new Weight(2, LB),
                Collections.emptyList(),
                OAK_WOOD,
                new BlockedInteraction()
        ));
        PotionMeta acidVialMeta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(SPLASH_POTION);
        if (acidVialMeta != null) {
            acidVialMeta.setBasePotionData(new PotionData(PotionType.POISON));
        }
        saveItem(adventuringGearFolder, new GenericItemType(
                "Acid (vial)",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(25, GP),
                new Weight(2, LB),
                Arrays.asList(
                        "As an action, you can splash the contents of",
                        "this vial onto a creature within 5 feet of you or throw",
                        "the vial up to 20 feet, shattering it on impact. In either",
                        "case, make a ranged attack against a creature or object,",
                        "treating the acid as an improvised weapon. On a hit, the", "" +
                        "target takes 2d6 acid damage."
                ),
                SPLASH_POTION,
                acidVialMeta,
                new BlockedInteraction()
        ));
        PotionMeta alchemistsFireMeta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(SPLASH_POTION);
        if (alchemistsFireMeta != null) {
            alchemistsFireMeta.setBasePotionData(new PotionData(PotionType.FIRE_RESISTANCE));
        }
        saveItem(adventuringGearFolder, new GenericItemType(
                "Alchemist's fire (flask)",
                ADVENTURING_GEAR,
                UNCOMMON,
                new CurrencyValue(50, GP),
                new Weight(1, LB),
                Arrays.asList(
                        "This sticky, adhesive fluid ignites",
                        "when exposed to air. As an action, you can throw this",
                        "flask up to 20 feet, shattering it on impact. Make a",
                        "ranged attack against a creature or object, treating",
                        "the alchemist's fire as an improvised weapon. On a",
                        "hit, the target takes 1d4 fire damage at the start of",
                        "each of its turns. A creature can end this damage by",
                        "using its action to make a DC 10 Dexterity check to",
                        "extinguish the flames"
                ),
                SPLASH_POTION,
                alchemistsFireMeta,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Arrow",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(5, CP),
                new Weight(0.05, LB),
                Collections.emptyList(),
                ARROW,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Blowgun Needle",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(2, CP),
                new Weight(0.02, LB),
                Collections.emptyList(),
                ARROW,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Crossbow Bolt",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(5, CP),
                new Weight(0.075, LB),
                Collections.emptyList(),
                ARROW,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Sling bullet",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(1, CP),
                new Weight(0.075, LB),
                Collections.emptyList(),
                ENDER_PEARL,
                new BlockedInteraction()
        ));
        PotionMeta antitoxinMeta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.POTION);
        if (antitoxinMeta != null) {
            antitoxinMeta.setBasePotionData(new PotionData(PotionType.MUNDANE));
        }
        saveItem(adventuringGearFolder, new GenericItemType(
                "Antitoxin (vial)",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(50, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "A creature that drinks this vial of liquid",
                        "gains advantage on saving throws against poison for 1",
                        "hour. It confers no benefit to undead or constructs."
                ),
                Material.POTION,
                antitoxinMeta,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Crystal",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(10, GP),
                new Weight(1, LB),
                Arrays.asList(
                        "An arcane focus is a special item -",
                        "an orb, a crystal, a rod, a specially constructed staff,",
                        "a wand-like length of wood, or some similar item -",
                        "designed to channel the power of arcane spells. A",
                        "sorcerer, warlock, or wizard can use such an item as a",
                        "spellcasting focus, as described in chapter 10."
                ),
                DIAMOND,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Orb",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(20, GP),
                new Weight(3, LB),
                Arrays.asList(
                        "An arcane focus is a special item -",
                        "an orb, a crystal, a rod, a specially constructed staff,",
                        "a wand-like length of wood, or some similar item -",
                        "designed to channel the power of arcane spells. A",
                        "sorcerer, warlock, or wizard can use such an item as a",
                        "spellcasting focus, as described in chapter 10."
                ),
                ENDER_PEARL,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Rod",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(10, GP),
                new Weight(2, LB),
                Arrays.asList(
                        "An arcane focus is a special item -",
                        "an orb, a crystal, a rod, a specially constructed staff,",
                        "a wand-like length of wood, or some similar item -",
                        "designed to channel the power of arcane spells. A",
                        "sorcerer, warlock, or wizard can use such an item as a",
                        "spellcasting focus, as described in chapter 10."
                ),
                BLAZE_ROD,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Staff",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(5, GP),
                new Weight(4, LB),
                Arrays.asList(
                        "An arcane focus is a special item -",
                        "an orb, a crystal, a rod, a specially constructed staff,",
                        "a wand-like length of wood, or some similar item -",
                        "designed to channel the power of arcane spells. A",
                        "sorcerer, warlock, or wizard can use such an item as a",
                        "spellcasting focus, as described in chapter 10."
                ),
                STICK,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Wand",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(10, GP),
                new Weight(1, LB),
                Arrays.asList(
                        "An arcane focus is a special item -",
                        "an orb, a crystal, a rod, a specially constructed staff,",
                        "a wand-like length of wood, or some similar item -",
                        "designed to channel the power of arcane spells. A",
                        "sorcerer, warlock, or wizard can use such an item as a",
                        "spellcasting focus, as described in chapter 10."
                ),
                STICK,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Backpack",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(2, GP),
                new Weight(5, LB),
                Arrays.asList(
                        "Stores 1 cubic foot/30 pounds of gear.",
                        "You can also strap items, such as a bedroll or coil of rope,",
                        "to the outside of a backpack."
                ),
                SHULKER_BOX,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Ball bearings (bag of 1000)",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(1, GP),
                new Weight(2, LB),
                Arrays.asList(
                        "As an action, you can spill these tiny",
                        "metal balls from their pouch to cover a level, square",
                        "area that is 10 feet on a side. A creature moving across",
                        "the covered area must succeed on a DC 10 Dexterity",
                        "saving throw or fall prone. A creature moving through",
                        "the area at half speed doesn't need to make the save."
                ),
                MELON_SEEDS,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Barrel",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(2, GP),
                new Weight(70, LB),
                Arrays.asList(
                        "Can contain up to 40 gallons of liquid, or 4 cubic feet of solid."
                ),
                BARREL,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Basket",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(4, SP),
                new Weight(2, LB),
                Arrays.asList(
                        "Has a capacity of 2 cubic feet/40 pounds of gear."
                ),
                CHEST_MINECART,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Bedroll",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(1, GP),
                new Weight(7, LB),
                Collections.emptyList(),
                RED_BED,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Bell",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(1, GP),
                new Weight(0, LB),
                Collections.emptyList(),
                BELL,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Blanket",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(5, SP),
                new Weight(3, LB),
                Collections.emptyList(),
                RED_CARPET,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Block and tackle",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(1, GP),
                new Weight(5, LB),
                Arrays.asList(
                        "A set of pulleys with a cable",
                        "threaded through them and a hook to attach to objects, a",
                        "block and tackle allows you to hoist up to four times the",
                        "weight you can normally lift."
                ),
                TRIPWIRE_HOOK,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Book",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(25, GP),
                new Weight(5, LB),
                Arrays.asList(
                        "A book might contain poetry, historical",
                        "accounts, information pertaining to a particular field",
                        "of lore, diagrams and notes on gnomish contraptions,",
                        "or just about anything else that can be represented",
                        "using text or pictures."
                ),
                BOOK,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Bottle, glass",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(2, GP),
                new Weight(2, LB),
                Arrays.asList(
                        "Can contain up to 1.5 pints of liquid."
                ),
                GLASS_BOTTLE,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Bucket",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(5, CP),
                new Weight(2, LB),
                Arrays.asList(
                        "Can contain up to 3 gallons of liquid, or half a cubic ",
                        "foot of solid."
                ),
                BUCKET,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Caltrops (bag of 20)",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(1, GP),
                new Weight(2, LB),
                Arrays.asList(
                        "As an action, you can spread a bag of caltrops",
                        "to cover a square area that is 5 feet on a side. Any",
                        "creature that enters the area must succeed on a DC 15",
                        "Dexterity saving throw or stop moving this turn and",
                        "take 1 piercing damage. Taking this damage reduces the",
                        "creature's walking speed by 10 feet until the creature",
                        "regains at least 1 hit point. A creature moving through",
                        "the area at half speed doesn't need to make the save."
                ),
                STONE_PRESSURE_PLATE,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Candle",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(1, CP),
                new Weight(0, LB),
                Arrays.asList(
                        "For 1 hour, a candle sheds bright light in a",
                        "5-foot radius and dim light for an additional 5 feet."
                ),
                END_ROD,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Case, crossbow bolt",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(1, GP),
                new Weight(1, LB),
                Arrays.asList(
                        "This wooden case can hold up",
                        "to twenty crossbow bolts."
                ),
                SHULKER_BOX,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Case, map or scroll",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(1, GP),
                new Weight(1, LB),
                Arrays.asList(
                        "This cylindrical leather case can",
                        "hold up to ten rolled-up sheets of paper or five rolled-up",
                        "sheets of parchment."
                ),
                SHULKER_BOX,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Chain (10 feet)",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(5, GP),
                new Weight(10, LB),
                Arrays.asList(
                        "A chain has 10 hit points. It can be burst with a",
                        "successful DC 20 Strength check."
                ),
                STRING,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Chalk (1 piece)",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(1, CP),
                new Weight(0, LB),
                Collections.emptyList(),
                QUARTZ,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Chest",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(5, GP),
                new Weight(25, LB),
                Arrays.asList(
                        "Has a capacity of 12 cubic feet/300 pounds of gear."
                ),
                CHEST,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Climber's kit",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(25, GP),
                new Weight(12, LB),
                Arrays.asList(
                        "A climber's kit includes special pitons",
                        "boot tips, gloves and a harness. You can use the",
                        "climber's kit as an action to anchor yourself; when you",
                        "do, you can't fall more than 25 feet from the point where",
                        "you anchored yourself, and you can't climb more than 25",
                        "feet away from that point without undoing the anchor."
                ),
                STRING,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Clothes, common",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(5, SP),
                new Weight(3, LB),
                Collections.emptyList(),
                LEATHER_CHESTPLATE,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Clothes, fine",
                ADVENTURING_GEAR,
                UNCOMMON,
                new CurrencyValue(15, GP),
                new Weight(6, LB),
                Collections.emptyList(),
                LEATHER_CHESTPLATE,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Clothes, traveler's",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(2, GP),
                new Weight(4, LB),
                Collections.emptyList(),
                LEATHER_CHESTPLATE,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Component pouch",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(25, GP),
                new Weight(2, LB),
                Arrays.asList(
                        "A component pouch is a small,",
                        "watertight leather belt pouch that has compartments",
                        "to hold all the material components and other special",
                        "items you need to cast your spells, except for those",
                        "components that have a specific cost (as indicated in a",
                        "spell's description).",
                        "Can contain up to 1/5 cubic foot or 6 pounds of items."
                ),
                SHULKER_BOX,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Crowbar",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(2, GP),
                new Weight(5, LB),
                Arrays.asList(
                        "Using a crowbar grants advantage",
                        "to Strength checks where the crowbar's leverage",
                        "can be applied."
                ),
                IRON_HOE,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Sprig of Mistletoe",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(1, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "A druidic focus might be a sprig of",
                        "mistletoe or holly, a wand or scepter made of yew or",
                        "another special wood, a staff drawn whole out of a living",
                        "tree, or a totem object incorporating feathers, fur, bones",
                        "and teeth from sacred animals. A druid can use such an",
                        "object as a spellcasting focus."
                ),
                OAK_LEAVES,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Totem",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(1, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "A druidic focus might be a sprig of",
                        "mistletoe or holly, a wand or scepter made of yew or",
                        "another special wood, a staff drawn whole out of a living",
                        "tree, or a totem object incorporating feathers, fur, bones",
                        "and teeth from sacred animals. A druid can use such an",
                        "object as a spellcasting focus."
                ),
                FEATHER,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Wooden staff",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(5, GP),
                new Weight(4, LB),
                Arrays.asList(
                        "A druidic focus might be a sprig of",
                        "mistletoe or holly, a wand or scepter made of yew or",
                        "another special wood, a staff drawn whole out of a living",
                        "tree, or a totem object incorporating feathers, fur, bones",
                        "and teeth from sacred animals. A druid can use such an",
                        "object as a spellcasting focus."
                ),
                STICK,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Yew wand",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(10, GP),
                new Weight(1, LB),
                Arrays.asList(
                        "A druidic focus might be a sprig of",
                        "mistletoe or holly, a wand or scepter made of yew or",
                        "another special wood, a staff drawn whole out of a living",
                        "tree, or a totem object incorporating feathers, fur, bones",
                        "and teeth from sacred animals. A druid can use such an",
                        "object as a spellcasting focus."
                ),
                STICK,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Fishing tackle",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(1, GP),
                new Weight(4, LB),
                Arrays.asList(
                        "This kit includes a wooden rod, silken",
                        "line, corkwood bobbers, steel hooks, lead sinkers, velvet",
                        "lures, and narrow netting."
                ),
                FISHING_ROD,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Flask or tankard",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(2, CP),
                new Weight(1, LB),
                Arrays.asList(
                        "Can contain up to 1 pint of liquid."
                ),
                GLASS_BOTTLE,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Grappling hook",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(2, GP),
                new Weight(4, LB),
                Collections.emptyList(),
                TRIPWIRE_HOOK,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Hammer",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(1, GP),
                new Weight(3, LB),
                Collections.emptyList(),
                STONE_AXE,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Hammer, sledge",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(2, GP),
                new Weight(10, LB),
                Collections.emptyList(),
                WOODEN_AXE,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Healer's kit",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(5, GP),
                new Weight(3, LB),
                Arrays.asList(
                        "This kit is a leather pouch containing",
                        "bandages, salves, and splints. The kit has ten uses. As",
                        "an action, you can expend one use of the kit to stabilize",
                        "a creature that has 0 hit points, without needing to make",
                        "a Wisdom (Medicine) check."
                ),
                PAPER,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Amulet",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(5, GP),
                new Weight(1, LB),
                Arrays.asList(
                        "A holy symbol is a representation of",
                        "a god or pantheon. It might be an amulet depicting a",
                        "symbol representing a deity, the same symbol carefully",
                        "engraved or inlaid as an emblem on a shield, or a tiny",
                        "box holding a fragment of a sacred relic.",
                        "A cleric or paladin can use a holy symbol as a spellcasting",
                        "focus. To use the symbol in this way, the caster must hold",
                        "it in hand, wear it visibly or bear it on a shield."
                ),
                EMERALD,
                new BlockedInteraction()
        ));
        BannerMeta emblemMeta = (BannerMeta) Bukkit.getItemFactory().getItemMeta(BLACK_BANNER);
        if (emblemMeta != null) {
            emblemMeta.setPatterns(Arrays.asList(
                    new Pattern(DyeColor.WHITE, PatternType.STRAIGHT_CROSS),
                    new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP),
                    new Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM),
                    new Pattern(DyeColor.BLACK, PatternType.RHOMBUS_MIDDLE),
                    new Pattern(DyeColor.RED, PatternType.TRIANGLES_BOTTOM)
            ));
        }
        saveItem(adventuringGearFolder, new GenericItemType(
                "Emblem",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(5, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "A holy symbol is a representation of",
                        "a god or pantheon. It might be an amulet depicting a",
                        "symbol representing a deity, the same symbol carefully",
                        "engraved or inlaid as an emblem on a shield, or a tiny",
                        "box holding a fragment of a sacred relic.",
                        "A cleric or paladin can use a holy symbol as a spellcasting",
                        "focus. To use the symbol in this way, the caster must hold",
                        "it in hand, wear it visibly or bear it on a shield."
                ),
                BLACK_BANNER,
                emblemMeta,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Reliquary",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(5, GP),
                new Weight(2, LB),
                Arrays.asList(
                        "A holy symbol is a representation of",
                        "a god or pantheon. It might be an amulet depicting a",
                        "symbol representing a deity, the same symbol carefully",
                        "engraved or inlaid as an emblem on a shield, or a tiny",
                        "box holding a fragment of a sacred relic.",
                        "A cleric or paladin can use a holy symbol as a spellcasting",
                        "focus. To use the symbol in this way, the caster must hold",
                        "it in hand, wear it visibly or bear it on a shield."
                ),
                GOLD_INGOT,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Holy water (flask)",
                ADVENTURING_GEAR,
                UNCOMMON,
                new CurrencyValue(25, GP),
                new Weight(1, LB),
                Arrays.asList(
                        "As an action, you can splash the contents",
                        "of this flask onto a creature within 5 feet of you or throw",
                        "it up to 20 feet, shattering it on impact. In either case,",
                        "make a ranged attack against a target creature, treating",
                        "the holy water as an improvised weapon. If the target is",
                        "a fiend or undead, it takes 2d6, radiant damage.",
                        "A cleric or paladin may create holy water by",
                        "performing a special ritual. The ritual takes 1 hour",
                        "to perform, uses 25 gp worth of powdered silver, and",
                        "requires the caster to expend a 1st level spell slot."
                ),
                SPLASH_POTION,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Hourglass",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(25, GP),
                new Weight(1, LB),
                Collections.emptyList(),
                CLOCK,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Hunting trap",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(5, GP),
                new Weight(25, LB),
                Arrays.asList(
                        "When you use your action to set it,",
                        "this trap forms a saw-toothed steel ring that snaps shut",
                        "when a creature steps on a pressure plate in the center.",
                        "The trap is affixed by a heavy chain to an immobile",
                        "object, such as a tree or a spike driven into the ground.",
                        "A creature that steps on the plate must succeed on a DC",
                        "13 Dexterity saving throw or take 1d4 piercing damage",
                        "and stop moving. Thereafter, until the creature breaks",
                        "free of the trap, its movement is limited by the length",
                        "of the chain (typically 3 feet long). A creature can use",
                        "its action to make a DC 13 Strength check, freeing",
                        "itself or another creature within its reach on a success.",
                        "Each failed check deals 1 piercing damage to the",
                        "trapped creature."
                ),
                HEAVY_WEIGHTED_PRESSURE_PLATE,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Ink (1 ounce bottle)",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(10, GP),
                new Weight(1, OZ),
                Collections.emptyList(),
                INK_SAC,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Ink pen",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(2, CP),
                new Weight(0, LB),
                Collections.emptyList(),
                FEATHER,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Jug or pitcher",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(2, CP),
                new Weight(4, LB),
                Arrays.asList(
                        "Can contain up to 1 gallon of liquid."
                ),
                GLASS_BOTTLE,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Ladder (10-foot)",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(1, SP),
                new Weight(25, LB),
                Collections.emptyList(),
                LADDER,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Lamp",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(5, SP),
                new Weight(1, LB),
                Arrays.asList(
                        "A lamp casts bright light in a 15-foot radius",
                        "and dim light for an additional 30 feet. Once lit, it burns",
                        "for 6 hours on a flask (1 pint) of oil."
                ),
                REDSTONE_LAMP,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Lantern, bullseye",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(10, GP),
                new Weight(2, LB),
                Arrays.asList(
                        "A bullseye lantern casts bright",
                        "light in a 60-foot cone and dim light for an additional 60",
                        "feet. Once lit, it burns 6 hours on a flask (1 pint) of oil."
                ),
                REDSTONE_LAMP,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Lantern, hooded",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(5, GP),
                new Weight(2, LB),
                Arrays.asList(
                        "A hooded lantern casts bright light",
                        "in a 30-foot radius and dim light for an additional 30",
                        "feet. Once lit, it burns for 6 hours on a flask (1 pint) of",
                        "oil. As an action, you can lower the hood, reducing the",
                        "light to a dim light in a 5-foot radius."
                ),
                REDSTONE_TORCH,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Lock",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(10, GP),
                new Weight(1, LB),
                Arrays.asList(
                        "A key is provided with the lock. Without the",
                        "key, a creature proficient with thieves' tools can pick",
                        "this lock with a successful DC 15 Dexterity check."
                ),
                IRON_INGOT,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Magnifying glass",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(100, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "This lens allows a closer look at",
                        "small objects. It is also useful as a substitute for flint",
                        "and steel when starting fires. Lighting a fire with a",
                        "magnifying glass requires light as bright as sunlight to",
                        "focus, tinder to ignite, and about 5 minutes for the fire",
                        "to ignite. A magnifying glass grants advantage on any",
                        "ability check made to appraise or inspect an item that is",
                        "small or highly detailed."
                ),
                IRON_INGOT,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Manacles",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(2, GP),
                new Weight(6, LB),
                Arrays.asList(
                        "These metal restraints can bind a Small",
                        "or Medium creature. Escaping the manacles requires",
                        "a successful DC 20 Dexterity check. Breaking them",
                        "requires a successful DC 20 Strength check. Each",
                        "set of manacles comes with one key. Without the key,",
                        "a creature proficient with thieves' tools can pick the",
                        "manacles' lock with a successful DC 15 Dexterity",
                        "check. Manacles have 15 hit points."
                ),
                IRON_INGOT,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Mess Kit",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(2, SP),
                new Weight(1, LB),
                Arrays.asList(
                        "This tin box contains a cup and simple",
                        "cutlery. The box clamps together, and one side can",
                        "be used as a cooking pan and the other as a plate or",
                        "shallow bowl."
                ),
                IRON_INGOT,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Mirror, steel",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(5, GP),
                new Weight(0.5, LB),
                Collections.emptyList(),
                IRON_BLOCK,
                new BlockedInteraction()
        ));
        PotionMeta oilMeta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.POTION);
        if (oilMeta != null) {
            oilMeta.setBasePotionData(new PotionData(PotionType.FIRE_RESISTANCE));
        }
        saveItem(adventuringGearFolder, new GenericItemType(
                "Oil (flask)",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(1, SP),
                new Weight(1, LB),
                Arrays.asList(
                        "Oil usually comes in a clay flask that holds 1",
                        "pint. As an action, you can splash the oil in this flask",
                        "onto a creature within 5 feet of you or throw it up to",
                        "20 feet, shattering it on impact. Make a ranged attack",
                        "against a target creature or object, treating the oil as",
                        "an improvised weapon. On a hit, the target is covered",
                        "in oil. If the target takes any fire damage before the oil",
                        "dries (after 1 minute), the target takes an additional 5",
                        "fire damage from the burning oil. You can also pour a",
                        "flask of oil on the ground to cover a 5-foot square area,",
                        "provided the surface is level. If lit, the oil burns for",
                        "2 rounds and deals 5 fire damage to any creature that",
                        "enters the area or ends its turn in the area. A creature",
                        "can take this damage only once per turn."
                ),
                Material.POTION,
                oilMeta,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Paper (one sheet)",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(2, SP),
                new Weight(0, LB),
                Collections.emptyList(),
                PAPER,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Parchment (one sheet)",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(1, SP),
                new Weight(0, LB),
                Collections.emptyList(),
                PAPER,
                new BlockedInteraction()
        ));
        PotionMeta perfumeMeta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.POTION);
        if (perfumeMeta != null) {
            perfumeMeta.setBasePotionData(new PotionData(PotionType.STRENGTH));
        }
        saveItem(adventuringGearFolder, new GenericItemType(
                "Perfume (vial)",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(5, GP),
                new Weight(0, LB),
                Collections.emptyList(),
                Material.POTION,
                perfumeMeta,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Pick, miner's",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(2, GP),
                new Weight(10, LB),
                Collections.emptyList(),
                IRON_PICKAXE,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Piton",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(5, CP),
                new Weight(0.25, LB),
                Collections.emptyList(),
                STICK,
                new BlockedInteraction()
        ));
        PotionMeta poisonMeta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.POTION);
        if (poisonMeta != null) {
            poisonMeta.setBasePotionData(new PotionData(PotionType.POISON));
        }
        saveItem(adventuringGearFolder, new GenericItemType(
                "Poison, basic (vial)",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(100, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "You can use the poison in this vial",
                        "to coat one slashing or piercing weapon or up to three",
                        "pieces of ammunition. Applying the poison takes",
                        "an action. A creature hit by the poisoned weapon or",
                        "ammunition must make a DC 10 Constitution saving",
                        "throw or take 1d4 poison damage. Once applied, the",
                        "poison retains potency for 1 minute before drying."
                ),
                Material.POTION,
                poisonMeta,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Pole (10-foot)",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(5, CP),
                new Weight(7, LB),
                Collections.emptyList(),
                STICK,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Pot, iron",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(2, GP),
                new Weight(10, LB),
                Arrays.asList(
                        "Can contain up to 1 gallon of liquid."
                ),
                CAULDRON,
                new BlockedInteraction()
        ));
        PotionMeta healingPotionMeta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.POTION);
        if (healingPotionMeta != null) {
            healingPotionMeta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL));
        }
        saveItem(adventuringGearFolder, new GenericItemType(
                "Potion of healing",
                ADVENTURING_GEAR,
                UNCOMMON,
                new CurrencyValue(50, GP),
                new Weight(0.5, LB),
                Arrays.asList(
                        "A character who drinks the magical",
                        "red fluid in this vial regains 2d4+2 hit points. Drinking",
                        "or administering a potion takes an action."
                ),
                Material.POTION,
                healingPotionMeta,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Pouch",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(5, SP),
                new Weight(1, LB),
                Arrays.asList(
                        "A cloth or leather pouch can hold up to 20",
                        "sling bullets or 50 blowgun needles, among other",
                        "things. A compartmentalized pouch for holding spell",
                        "components is called a component pouch, which is a",
                        "different item.",
                        "Can store up to 1/5 cubic foot or 6 pounds of gear."
                ),
                SHULKER_BOX,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Quiver",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(1, GP),
                new Weight(1, LB),
                Arrays.asList(
                        "A quiver can hold up to 20 arrows."
                ),
                SHULKER_BOX,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Ram, portable",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(4, GP),
                new Weight(35, LB),
                Arrays.asList(
                        "You can use a portable ram to break",
                        "down doors. When doing so, you gain a +4 bonus on the",
                        "Strength check. One other character can help you use",
                        "the ram, giving you advantage on this check."
                ),
                IRON_BLOCK,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Rations (1 day)",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(5, SP),
                new Weight(2, LB),
                Arrays.asList(
                        "Rations consist of dry foods suitable",
                        "for extended travel, including jerky, dried fruit,",
                        "hardtack, and nuts."
                ),
                BREAD,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Robes",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(1, GP),
                new Weight(4, LB),
                Collections.emptyList(),
                LEATHER_CHESTPLATE,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Rope, hempen (50 feet)",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(1, GP),
                new Weight(10, LB),
                Arrays.asList(
                        "Rope, whether made of hemp, or silk, has 2 hit",
                        "points and can be burst with a DC 17 Strength check."
                ),
                STRING,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Rope, silk (50 feet)",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(10, GP),
                new Weight(5, LB),
                Arrays.asList(
                        "Rope, whether made of hemp, or silk, has 2 hit",
                        "points and can be burst with a DC 17 Strength check."
                ),
                STRING,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Sack",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(1, CP),
                new Weight(0.5, LB),
                Arrays.asList(
                        "Can contain up to 1 cubic foot or 30 pounds of gear."
                ),
                SHULKER_BOX,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Scale, merchant's",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(5, GP),
                new Weight(3, LB),
                Arrays.asList(
                        "A scale includes a small balance,",
                        "pans, and a suitable assortment of weights up to 2",
                        "pounds. With it, you can measure the exact weight of",
                        "goods, to help determine their worth."
                ),
                IRON_BLOCK,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Sealing wax",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(5, SP),
                new Weight(0, LB),
                Collections.emptyList(),
                SLIME_BALL,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Shovel",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(2, GP),
                new Weight(5, LB),
                Collections.emptyList(),
                IRON_SHOVEL,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Signal whistle",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(5, CP),
                new Weight(0, LB),
                Collections.emptyList(),
                IRON_INGOT,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Signet ring",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(5, GP),
                new Weight(0, LB),
                Collections.emptyList(),
                GOLD_INGOT,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Soap",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(2, CP),
                new Weight(0, LB),
                Collections.emptyList(),
                BRICK,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Spellbook",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(50, GP),
                new Weight(3, LB),
                Arrays.asList(
                        "Essential for wizards, a spellbook is",
                        "a leather-bound tome with 100 blank vellum pages",
                        "suitable for recording spells."
                ),
                BOOK,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Spikes, iron (10)",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(1, GP),
                new Weight(5, LB),
                Collections.emptyList(),
                IRON_INGOT,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Spyglass",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(1000, GP),
                new Weight(1, LB),
                Arrays.asList(
                        "Objects viewed through a spyglass are",
                        "magnified to twice their size."
                ),
                IRON_INGOT,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Tent, two-person",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(2, GP),
                new Weight(20, LB),
                Arrays.asList(
                        "A simple and portable canvas shelter, a",
                        "tent sleeps two."
                ),
                GREEN_WOOL,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Tinderbox",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(5, SP),
                new Weight(1, LB),
                Arrays.asList(
                        "This small container holds flint, fire steel,",
                        "and tinder (usually dry cloth soaked in light oil) used to",
                        "kindle a fire. Using it to light a torch - or anything else",
                        "with abundant, exposed fuel - takes an action. Lighting",
                        "any other fire takes 1 minute."
                ),
                FLINT_AND_STEEL,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Torch",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(1, CP),
                new Weight(1, LB),
                Arrays.asList(
                        "A torch burns for 1 hour, providing bright light",
                        "in a 20-foot radius and dim light for an additional 20",
                        "feet. If you make a melee attack with a burning torch",
                        "and hit, it deals 1 fire damage."
                ),
                TORCH,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Vial",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(1, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "Can contain up to 4 ounces of liquid."
                ),
                Material.POTION,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Waterskin",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(2, SP),
                new Weight(5, LB),
                Arrays.asList(
                        "Can contain up to 4 pints of liquid."
                ),
                LEATHER,
                new BlockedInteraction()
        ));
        saveItem(adventuringGearFolder, new GenericItemType(
                "Whetstone",
                ADVENTURING_GEAR,
                COMMON,
                new CurrencyValue(1, CP),
                new Weight(1, LB),
                Collections.emptyList(),
                STONE_BUTTON,
                new BlockedInteraction()
        ));
        // Tools
        File toolsFolder = new File(itemFolder, "tools");
        saveItem(toolsFolder, new GenericItemType(
                "Alchemist's supplies",
                TOOL,
                COMMON,
                new CurrencyValue(50, GP),
                new Weight(8, LB),
                Arrays.asList(
                        "These special tools include the items",
                        "needed to pursue a craft or trade. Proficiency with a",
                        "set of artisan's tools lets you add your proficiency",
                        "bonus to any ability checks you can make using the tools",
                        "in your craft. Each type of artisan's tools requires a ",
                        "separate proficiency."
                ),
                BREWING_STAND,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Brewer's supplies",
                TOOL,
                COMMON,
                new CurrencyValue(20, GP),
                new Weight(9, LB),
                Arrays.asList(
                        "These special tools include the items",
                        "needed to pursue a craft or trade. Proficiency with a",
                        "set of artisan's tools lets you add your proficiency",
                        "bonus to any ability checks you can make using the tools",
                        "in your craft. Each type of artisan's tools requires a ",
                        "separate proficiency."
                ),
                BREWING_STAND,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Calligrapher's supplies",
                TOOL,
                COMMON,
                new CurrencyValue(10, GP),
                new Weight(5, LB),
                Arrays.asList(
                        "These special tools include the items",
                        "needed to pursue a craft or trade. Proficiency with a",
                        "set of artisan's tools lets you add your proficiency",
                        "bonus to any ability checks you can make using the tools",
                        "in your craft. Each type of artisan's tools requires a ",
                        "separate proficiency."
                ),
                FEATHER,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Carpenter's tools",
                TOOL,
                COMMON,
                new CurrencyValue(8, GP),
                new Weight(6, LB),
                Arrays.asList(
                        "These special tools include the items",
                        "needed to pursue a craft or trade. Proficiency with a",
                        "set of artisan's tools lets you add your proficiency",
                        "bonus to any ability checks you can make using the tools",
                        "in your craft. Each type of artisan's tools requires a ",
                        "separate proficiency."
                ),
                CRAFTING_TABLE,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Cartographer's tools",
                TOOL,
                COMMON,
                new CurrencyValue(15, GP),
                new Weight(6, LB),
                Arrays.asList(
                        "These special tools include the items",
                        "needed to pursue a craft or trade. Proficiency with a",
                        "set of artisan's tools lets you add your proficiency",
                        "bonus to any ability checks you can make using the tools",
                        "in your craft. Each type of artisan's tools requires a ",
                        "separate proficiency."
                ),
                COMPASS,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Cobbler's tools",
                TOOL,
                COMMON,
                new CurrencyValue(5, GP),
                new Weight(5, LB),
                Arrays.asList(
                        "These special tools include the items",
                        "needed to pursue a craft or trade. Proficiency with a",
                        "set of artisan's tools lets you add your proficiency",
                        "bonus to any ability checks you can make using the tools",
                        "in your craft. Each type of artisan's tools requires a ",
                        "separate proficiency."
                ),
                IRON_AXE,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Cook's utensils",
                TOOL,
                COMMON,
                new CurrencyValue(1, GP),
                new Weight(8, LB),
                Arrays.asList(
                        "These special tools include the items",
                        "needed to pursue a craft or trade. Proficiency with a",
                        "set of artisan's tools lets you add your proficiency",
                        "bonus to any ability checks you can make using the tools",
                        "in your craft. Each type of artisan's tools requires a ",
                        "separate proficiency."
                ),
                EGG,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Glassblower's tools",
                TOOL,
                COMMON,
                new CurrencyValue(30, GP),
                new Weight(5, LB),
                Arrays.asList(
                        "These special tools include the items",
                        "needed to pursue a craft or trade. Proficiency with a",
                        "set of artisan's tools lets you add your proficiency",
                        "bonus to any ability checks you can make using the tools",
                        "in your craft. Each type of artisan's tools requires a ",
                        "separate proficiency."
                ),
                SHEARS,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Jeweler's tools",
                TOOL,
                COMMON,
                new CurrencyValue(25, GP),
                new Weight(2, LB),
                Arrays.asList(
                        "These special tools include the items",
                        "needed to pursue a craft or trade. Proficiency with a",
                        "set of artisan's tools lets you add your proficiency",
                        "bonus to any ability checks you can make using the tools",
                        "in your craft. Each type of artisan's tools requires a ",
                        "separate proficiency."
                ),
                SHEARS,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Leatherworker's tools",
                TOOL,
                COMMON,
                new CurrencyValue(5, GP),
                new Weight(5, LB),
                Arrays.asList(
                        "These special tools include the items",
                        "needed to pursue a craft or trade. Proficiency with a",
                        "set of artisan's tools lets you add your proficiency",
                        "bonus to any ability checks you can make using the tools",
                        "in your craft. Each type of artisan's tools requires a ",
                        "separate proficiency."
                ),
                IRON_SWORD,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Mason's tools",
                TOOL,
                COMMON,
                new CurrencyValue(10, GP),
                new Weight(8, LB),
                Arrays.asList(
                        "These special tools include the items",
                        "needed to pursue a craft or trade. Proficiency with a",
                        "set of artisan's tools lets you add your proficiency",
                        "bonus to any ability checks you can make using the tools",
                        "in your craft. Each type of artisan's tools requires a ",
                        "separate proficiency."
                ),
                IRON_SHOVEL,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Painter's supplies",
                TOOL,
                COMMON,
                new CurrencyValue(10, GP),
                new Weight(5, LB),
                Arrays.asList(
                        "These special tools include the items",
                        "needed to pursue a craft or trade. Proficiency with a",
                        "set of artisan's tools lets you add your proficiency",
                        "bonus to any ability checks you can make using the tools",
                        "in your craft. Each type of artisan's tools requires a ",
                        "separate proficiency."
                ),
                PINK_DYE,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Potter's tools",
                TOOL,
                COMMON,
                new CurrencyValue(10, GP),
                new Weight(3, LB),
                Arrays.asList(
                        "These special tools include the items",
                        "needed to pursue a craft or trade. Proficiency with a",
                        "set of artisan's tools lets you add your proficiency",
                        "bonus to any ability checks you can make using the tools",
                        "in your craft. Each type of artisan's tools requires a ",
                        "separate proficiency."
                ),
                CLAY_BALL,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Smith's tools",
                TOOL,
                COMMON,
                new CurrencyValue(20, GP),
                new Weight(8, LB),
                Arrays.asList(
                        "These special tools include the items",
                        "needed to pursue a craft or trade. Proficiency with a",
                        "set of artisan's tools lets you add your proficiency",
                        "bonus to any ability checks you can make using the tools",
                        "in your craft. Each type of artisan's tools requires a ",
                        "separate proficiency."
                ),
                IRON_AXE,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Tinker's tools",
                TOOL,
                COMMON,
                new CurrencyValue(50, GP),
                new Weight(10, LB),
                Arrays.asList(
                        "These special tools include the items",
                        "needed to pursue a craft or trade. Proficiency with a",
                        "set of artisan's tools lets you add your proficiency",
                        "bonus to any ability checks you can make using the tools",
                        "in your craft. Each type of artisan's tools requires a ",
                        "separate proficiency."
                ),
                STRING,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Weaver's tools",
                TOOL,
                COMMON,
                new CurrencyValue(1, GP),
                new Weight(5, LB),
                Arrays.asList(
                        "These special tools include the items",
                        "needed to pursue a craft or trade. Proficiency with a",
                        "set of artisan's tools lets you add your proficiency",
                        "bonus to any ability checks you can make using the tools",
                        "in your craft. Each type of artisan's tools requires a ",
                        "separate proficiency."
                ),
                STRING,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Woodcarver's tools",
                TOOL,
                COMMON,
                new CurrencyValue(1, GP),
                new Weight(5, LB),
                Arrays.asList(
                        "These special tools include the items",
                        "needed to pursue a craft or trade. Proficiency with a",
                        "set of artisan's tools lets you add your proficiency",
                        "bonus to any ability checks you can make using the tools",
                        "in your craft. Each type of artisan's tools requires a ",
                        "separate proficiency."
                ),
                IRON_SWORD,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Disguise kit",
                TOOL,
                COMMON,
                new CurrencyValue(25, GP),
                new Weight(3, LB),
                Arrays.asList(
                        "This pouch of cosmetics, hair dye, and",
                        "small props lets you create disguises that change your",
                        "physical appearance. Proficiency with this kit lets you",
                        "add your proficiency bonus to any ability checks you",
                        "make to create a visual disguise."
                ),
                PLAYER_HEAD,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Forgery kit",
                TOOL,
                COMMON,
                new CurrencyValue(15, GP),
                new Weight(5, LB),
                Arrays.asList(
                        "This small box contains a variety of",
                        "papers and parchments, pens and inks, seals and",
                        "sealing wax, gold and silver leaf, and other supplies",
                        "necessary to create convincing forgeries of physical",
                        "documents. Proficiency with this kit lets you add your",
                        "proficiency bonus to any ability checks you make to",
                        "create a physical forgery of a document."
                ),
                PAPER,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Dice set",
                TOOL,
                COMMON,
                new CurrencyValue(1, SP),
                new Weight(0, LB),
                Collections.emptyList(),
                MELON_SEEDS,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Dragonchess set",
                TOOL,
                COMMON,
                new CurrencyValue(1, GP),
                new Weight(0.5, LB),
                Arrays.asList(
                        "This item encompasses a wide range",
                        "of game pieces, including dice and decks of cards (for",
                        "games such as Three-Dragon Ante). A few common",
                        "examples appear on the Tools table, but other kinds of",
                        "gaming sets exist. If you are proficient with a gaming",
                        "set, you can add your proficiency bonus to ability checks",
                        "you make to play a game with that set. Each type of",
                        "gaming set reequires a separate proficiency."
                ),
                DARK_OAK_PRESSURE_PLATE,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Playing card set",
                TOOL,
                COMMON,
                new CurrencyValue(5, SP),
                new Weight(0, LB),
                Arrays.asList(
                        "This item encompasses a wide range",
                        "of game pieces, including dice and decks of cards (for",
                        "games such as Three-Dragon Ante). A few common",
                        "examples appear on the Tools table, but other kinds of",
                        "gaming sets exist. If you are proficient with a gaming",
                        "set, you can add your proficiency bonus to ability checks",
                        "you make to play a game with that set. Each type of",
                        "gaming set reequires a separate proficiency."
                ),
                PAPER,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Three-Dragon Ante set",
                TOOL,
                COMMON,
                new CurrencyValue(1, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "This item encompasses a wide range",
                        "of game pieces, including dice and decks of cards (for",
                        "games such as Three-Dragon Ante). A few common",
                        "examples appear on the Tools table, but other kinds of",
                        "gaming sets exist. If you are proficient with a gaming",
                        "set, you can add your proficiency bonus to ability checks",
                        "you make to play a game with that set. Each type of",
                        "gaming set reequires a separate proficiency."
                ),
                PAPER,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Herbalism kit",
                TOOL,
                COMMON,
                new CurrencyValue(5, GP),
                new Weight(3, LB),
                Arrays.asList(
                        "This kit contains a cariety of",
                        "instruments such as clippers, mortar and pestle, and",
                        "pouches and vials used by herbalists to create remedies",
                        "and potions. Proficiency with this kit lets you add your",
                        "proficiency bonus to any ability checks you make to",
                        "identify or apply herbs. Also, proficiency with this kit is",
                        "required to create antitoxin and potions of healing."
                ),
                SHEARS,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Bagpipes",
                TOOL,
                COMMON,
                new CurrencyValue(30, GP),
                new Weight(6, LB),
                Arrays.asList(
                        "If you have proficiency with a given musical",
                        "instrument, you can add your proficiency bonus to",
                        "any ability checks you make to play music with the",
                        "instrument. A bard can use a musical instrument as a",
                        "spellcasting focus. Each type of musical instrument",
                        "requires a separate proficiency."
                ),
                PUFFERFISH,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Drum",
                TOOL,
                COMMON,
                new CurrencyValue(6, GP),
                new Weight(3, LB),
                Arrays.asList(
                        "If you have proficiency with a given musical",
                        "instrument, you can add your proficiency bonus to",
                        "any ability checks you make to play music with the",
                        "instrument. A bard can use a musical instrument as a",
                        "spellcasting focus. Each type of musical instrument",
                        "requires a separate proficiency."
                ),
                ACACIA_LOG,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Dulcimer",
                TOOL,
                COMMON,
                new CurrencyValue(25, GP),
                new Weight(10, LB),
                Arrays.asList(
                        "If you have proficiency with a given musical",
                        "instrument, you can add your proficiency bonus to",
                        "any ability checks you make to play music with the",
                        "instrument. A bard can use a musical instrument as a",
                        "spellcasting focus. Each type of musical instrument",
                        "requires a separate proficiency."
                ),
                MUSIC_DISC_WAIT,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Flute",
                TOOL,
                COMMON,
                new CurrencyValue(2, GP),
                new Weight(1, LB),
                Arrays.asList(
                        "If you have proficiency with a given musical",
                        "instrument, you can add your proficiency bonus to",
                        "any ability checks you make to play music with the",
                        "instrument. A bard can use a musical instrument as a",
                        "spellcasting focus. Each type of musical instrument",
                        "requires a separate proficiency."
                ),
                STICK,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Lute",
                TOOL,
                COMMON,
                new CurrencyValue(35, GP),
                new Weight(2, LB),
                Arrays.asList(
                        "If you have proficiency with a given musical",
                        "instrument, you can add your proficiency bonus to",
                        "any ability checks you make to play music with the",
                        "instrument. A bard can use a musical instrument as a",
                        "spellcasting focus. Each type of musical instrument",
                        "requires a separate proficiency."
                ),
                MUSIC_DISC_WAIT,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Lyre",
                TOOL,
                COMMON,
                new CurrencyValue(30, GP),
                new Weight(2, LB),
                Arrays.asList(
                        "If you have proficiency with a given musical",
                        "instrument, you can add your proficiency bonus to",
                        "any ability checks you make to play music with the",
                        "instrument. A bard can use a musical instrument as a",
                        "spellcasting focus. Each type of musical instrument",
                        "requires a separate proficiency."
                ),
                MUSIC_DISC_WAIT,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Horn",
                TOOL,
                COMMON,
                new CurrencyValue(3, GP),
                new Weight(2, LB),
                Arrays.asList(
                        "If you have proficiency with a given musical",
                        "instrument, you can add your proficiency bonus to",
                        "any ability checks you make to play music with the",
                        "instrument. A bard can use a musical instrument as a",
                        "spellcasting focus. Each type of musical instrument",
                        "requires a separate proficiency."
                ),
                CHORUS_FRUIT,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Pan flute",
                TOOL,
                COMMON,
                new CurrencyValue(12, GP),
                new Weight(2, LB),
                Arrays.asList(
                        "If you have proficiency with a given musical",
                        "instrument, you can add your proficiency bonus to",
                        "any ability checks you make to play music with the",
                        "instrument. A bard can use a musical instrument as a",
                        "spellcasting focus. Each type of musical instrument",
                        "requires a separate proficiency."
                ),
                BIRCH_SLAB,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Shawm",
                TOOL,
                COMMON,
                new CurrencyValue(2, GP),
                new Weight(1, LB),
                Arrays.asList(
                        "If you have proficiency with a given musical",
                        "instrument, you can add your proficiency bonus to",
                        "any ability checks you make to play music with the",
                        "instrument. A bard can use a musical instrument as a",
                        "spellcasting focus. Each type of musical instrument",
                        "requires a separate proficiency."
                ),
                BLAZE_ROD,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Viol",
                TOOL,
                COMMON,
                new CurrencyValue(30, GP),
                new Weight(1, LB),
                Arrays.asList(
                        "If you have proficiency with a given musical",
                        "instrument, you can add your proficiency bonus to",
                        "any ability checks you make to play music with the",
                        "instrument. A bard can use a musical instrument as a",
                        "spellcasting focus. Each type of musical instrument",
                        "requires a separate proficiency."
                ),
                MUSIC_DISC_WAIT,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Navigator's tools",
                TOOL,
                COMMON,
                new CurrencyValue(25, GP),
                new Weight(2, LB),
                Arrays.asList(
                        "This set of instruments is used",
                        "for navigation at sea. Proficiency with navigator's tools",
                        "lets you chart a ship's course and follow navigation",
                        "charts. In addition, these tools allow you to add your",
                        "proficiency bonus to any ability check you make to avoid",
                        "getting lost at sea."
                ),
                COMPASS,
                new BlockedInteraction()
        ));
        PotionMeta poisonersKitMeta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.POTION);
        if (poisonersKitMeta != null) {
            poisonersKitMeta.setBasePotionData(new PotionData(PotionType.POISON));
        }
        saveItem(toolsFolder, new GenericItemType(
                "Poisoner's kit",
                TOOL,
                COMMON,
                new CurrencyValue(50, GP),
                new Weight(2, LB),
                Arrays.asList(
                        "A poisoner's kit includes the vials,",
                        "chemicals, and other equipment necessary for the",
                        "creation of poisons. Proficiency with this kit lets you add",
                        "your proficiency bonus to any ability checks you make to",
                        "craft or use potions."
                ),
                Material.POTION,
                poisonersKitMeta,
                new BlockedInteraction()
        ));
        saveItem(toolsFolder, new GenericItemType(
                "Thieves' tools",
                TOOL,
                COMMON,
                new CurrencyValue(25, GP),
                new Weight(1, LB),
                Arrays.asList(
                        "This set of tools includes a small file,",
                        "a set of lock picks, a small mirror mounted on a metal",
                        "handle, a set of narrow-bladed scissors, and a pair of",
                        "pliers. Proficiency with these tools lets you add your",
                        "proficiency bonus to any ability checks you make to",
                        "disarm traps or open locks."
                ),
                TRIPWIRE_HOOK,
                new BlockedInteraction()
        ));
        // Trade goods
        File tradeGoodsFolder = new File(itemFolder, "trade_goods");
        saveItem(tradeGoodsFolder, new GenericItemType(
                "Wheat",
                TRADE_GOOD,
                COMMON,
                new CurrencyValue(1, CP),
                new Weight(1, LB),
                Collections.emptyList(),
                WHEAT,
                new BlockedInteraction()
        ));
        saveItem(tradeGoodsFolder, new GenericItemType(
                "Flour",
                TRADE_GOOD,
                COMMON,
                new CurrencyValue(2, CP),
                new Weight(1, LB),
                Collections.emptyList(),
                SUGAR,
                new BlockedInteraction()
        ));
        saveItem(tradeGoodsFolder, new GenericItemType(
                "Salt",
                TRADE_GOOD,
                COMMON,
                new CurrencyValue(5, CP),
                new Weight(1, LB),
                Collections.emptyList(),
                SUGAR,
                new BlockedInteraction()
        ));
        saveItem(tradeGoodsFolder, new GenericItemType(
                "Iron Ingot",
                TRADE_GOOD,
                COMMON,
                new CurrencyValue(1, SP),
                new Weight(1, LB),
                Collections.emptyList(),
                IRON_INGOT,
                new BlockedInteraction()
        ));
        saveItem(tradeGoodsFolder, new GenericItemType(
                "Canvas",
                TRADE_GOOD,
                COMMON,
                new CurrencyValue(1, SP),
                new Weight(1, LB),
                Collections.emptyList(),
                PAPER,
                new BlockedInteraction()
        ));
        saveItem(tradeGoodsFolder, new GenericItemType(
                "Copper",
                TRADE_GOOD,
                COMMON,
                new CurrencyValue(5, SP),
                new Weight(1, LB),
                Collections.emptyList(),
                BRICK,
                new BlockedInteraction()
        ));
        saveItem(tradeGoodsFolder, new GenericItemType(
                "Cotton cloth",
                TRADE_GOOD,
                COMMON,
                new CurrencyValue(5, SP),
                new Weight(1, LB),
                Collections.emptyList(),
                WHITE_CARPET,
                new BlockedInteraction()
        ));
        saveItem(tradeGoodsFolder, new GenericItemType(
                "Ginger",
                TRADE_GOOD,
                COMMON,
                new CurrencyValue(1, GP),
                new Weight(1, LB),
                Collections.emptyList(),
                POISONOUS_POTATO,
                new BlockedInteraction()
        ));
        saveItem(tradeGoodsFolder, new GenericItemType(
                "Cinnamon",
                TRADE_GOOD,
                COMMON,
                new CurrencyValue(2, GP),
                new Weight(1, LB),
                Collections.emptyList(),
                STICK,
                new BlockedInteraction()
        ));
        saveItem(tradeGoodsFolder, new GenericItemType(
                "Pepper",
                TRADE_GOOD,
                COMMON,
                new CurrencyValue(2, GP),
                new Weight(1, LB),
                Collections.emptyList(),
                MELON_SEEDS,
                new BlockedInteraction()
        ));
        saveItem(tradeGoodsFolder, new GenericItemType(
                "Cloves",
                TRADE_GOOD,
                COMMON,
                new CurrencyValue(3, GP),
                new Weight(1, LB),
                Collections.emptyList(),
                STICK,
                new BlockedInteraction()
        ));
        saveItem(tradeGoodsFolder, new GenericItemType(
                "Silver",
                TRADE_GOOD,
                COMMON,
                new CurrencyValue(3, GP),
                new Weight(1, LB),
                Collections.emptyList(),
                IRON_INGOT,
                new BlockedInteraction()
        ));
        saveItem(tradeGoodsFolder, new GenericItemType(
                "Linen",
                TRADE_GOOD,
                COMMON,
                new CurrencyValue(5, GP),
                new Weight(1, LB),
                Collections.emptyList(),
                WHITE_CARPET,
                new BlockedInteraction()
        ));
        saveItem(tradeGoodsFolder, new GenericItemType(
                "Silk",
                TRADE_GOOD,
                COMMON,
                new CurrencyValue(10, GP),
                new Weight(1, LB),
                Collections.emptyList(),
                WHITE_CARPET,
                new BlockedInteraction()
        ));
        saveItem(tradeGoodsFolder, new GenericItemType(
                "Saffron",
                TRADE_GOOD,
                COMMON,
                new CurrencyValue(15, GP),
                new Weight(1, LB),
                Collections.emptyList(),
                RED_DYE,
                new BlockedInteraction()
        ));
        saveItem(tradeGoodsFolder, new GenericItemType(
                "Gold",
                TRADE_GOOD,
                COMMON,
                new CurrencyValue(50, GP),
                new Weight(1, LB),
                Collections.emptyList(),
                GOLD_INGOT,
                new BlockedInteraction()
        ));
        saveItem(tradeGoodsFolder, new GenericItemType(
                "Platinum",
                TRADE_GOOD,
                COMMON,
                new CurrencyValue(500, GP),
                new Weight(1, LB),
                Collections.emptyList(),
                IRON_INGOT,
                new BlockedInteraction()
        ));
        // Food and drink
        File foodFolder = new File(itemFolder, "food");
        PotionMeta gallonOfAleMeta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.POTION);
        if (gallonOfAleMeta != null) {
            gallonOfAleMeta.setBasePotionData(new PotionData(PotionType.FIRE_RESISTANCE));
        }
        saveItem(foodFolder, new GenericItemType(
                "Gallon of Ale",
                FOOD,
                COMMON,
                new CurrencyValue(2, SP),
                new Weight(0, LB),
                Collections.emptyList(),
                Material.POTION,
                gallonOfAleMeta,
                new BlockedInteraction()
        ));
        PotionMeta mugOfAle = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.POTION);
        if (mugOfAle != null) {
            mugOfAle.setBasePotionData(new PotionData(PotionType.FIRE_RESISTANCE));
        }
        saveItem(foodFolder, new GenericItemType(
                "Mug of Ale",
                FOOD,
                COMMON,
                new CurrencyValue(4, CP),
                new Weight(0, LB),
                Collections.emptyList(),
                Material.POTION,
                mugOfAle,
                new BlockedInteraction()
        ));
        saveItem(foodFolder, new GenericItemType(
                "Banquet",
                FOOD,
                COMMON,
                new CurrencyValue(10, GP),
                new Weight(0, LB),
                Collections.emptyList(),
                COOKED_CHICKEN,
                new BlockedInteraction()
        ));
        saveItem(foodFolder, new GenericItemType(
                "Bread, loaf",
                FOOD,
                COMMON,
                new CurrencyValue(2, CP),
                new Weight(0, LB),
                Collections.emptyList(),
                BREAD,
                new BlockedInteraction()
        ));
        saveItem(foodFolder, new GenericItemType(
                "Cheese, hunk",
                FOOD,
                COMMON,
                new CurrencyValue(1, SP),
                new Weight(0, LB),
                Collections.emptyList(),
                SPONGE,
                new BlockedInteraction()
        ));
        saveItem(foodFolder, new GenericItemType(
                "Meat, chunk",
                FOOD,
                COMMON,
                new CurrencyValue(3, SP),
                new Weight(0, LB),
                Collections.emptyList(),
                COOKED_BEEF,
                new BlockedInteraction()
        ));
        PotionMeta commonWineMeta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.POTION);
        if (commonWineMeta != null) {
            commonWineMeta.setBasePotionData(new PotionData(PotionType.STRENGTH));
        }
        saveItem(foodFolder, new GenericItemType(
                "Common wine (pitcher)",
                FOOD,
                COMMON,
                new CurrencyValue(2, SP),
                new Weight(0, LB),
                Collections.emptyList(),
                Material.POTION,
                commonWineMeta,
                new BlockedInteraction()
        ));
        PotionMeta fineWineMeta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.POTION);
        if (fineWineMeta != null) {
            fineWineMeta.setBasePotionData(new PotionData(PotionType.STRENGTH));
        }
        saveItem(foodFolder, new GenericItemType(
                "Fine wine (bottle)",
                FOOD,
                COMMON,
                new CurrencyValue(10, GP),
                new Weight(0, LB),
                Collections.emptyList(),
                Material.POTION,
                fineWineMeta,
                new BlockedInteraction()
        ));
        // Existing custom items
        File customItemFolder = new File(itemFolder, "custom");
        saveItem(customItemFolder, new GenericItemType(
                "Oak log",
                MATERIAL,
                COMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "A thick piece of Oak. Perfect for",
                        "building, or crafting."
                ),
                OAK_LOG,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Poplar log",
                MATERIAL,
                COMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "A long and rigid piece of material.",
                        "These trees grow fast and tall. Perfect",
                        "for construction."
                ),
                SPRUCE_LOG,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Ash log",
                MATERIAL,
                COMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "A thick, but light piece of wood. Perfect",
                        "for the basic structure of a home."
                ),
                STRIPPED_BIRCH_LOG,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Walnut log",
                MATERIAL,
                COMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "A dark log, excellent for crafting furniture or fine materials"
                ),
                DARK_OAK_LOG,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Oak stick",
                MATERIAL,
                COMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Collections.emptyList(),
                STICK,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Suidae Hide",
                MATERIAL,
                UNCOMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "The hide from a dead suidae."
                ),
                LEATHER,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Fish Carcass",
                MATERIAL,
                COMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "A rigid carcass from a small fish."
                ),
                RABBIT_FOOT,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Knucklehead Trout",
                MATERIAL,
                UNCOMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "A small but unique looking trout."
                ),
                COD,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Fish Scale",
                MATERIAL,
                COMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "A scale from a dead fish."
                ),
                PRISMARINE_SHARD,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Raw Rat",
                FOOD,
                UNCOMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "This would definitely be best in stew... or not eaten at all..."
                ),
                PORKCHOP,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Rat Hide",
                MATERIAL,
                UNCOMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "A small piece of rat hide. It feels strong, but it's such a small",
                        "piece of material."
                ),
                DRIED_KELP,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Rodent Tail",
                MATERIAL,
                COMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "A tail lopped off a rodent."
                ),
                STRING,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Rodent Teeth",
                MATERIAL,
                COMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "A set of teeth from a dead rodent."
                ),
                BONE_MEAL,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Rodent Hide",
                MATERIAL,
                COMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "The hide from a dead rodent."
                ),
                LEATHER,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Small Bone",
                MATERIAL,
                COMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "A small bone. Could be from a small rodent, or creature"
                ),
                BONE,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Suidae Meat",
                FOOD,
                COMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "Fresh meat from a suidae."
                ),
                PORKCHOP,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Raw Rabbit",
                FOOD,
                UNCOMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "A small rabbit carcass.",
                        "It would be easier to cook it whole;",
                        "it should still taste good, though."
                ),
                RABBIT,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Rabbit Pelt",
                MATERIAL,
                UNCOMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "A small rabbit pelt.",
                        "Needs to be cleaned, and dried out before use."
                ),
                RABBIT_HIDE,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Unusable Meat Chunk",
                GARBAGE,
                CRAP,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "This chunk of meat is too torn up",
                        "to use for anything. Best to just throw",
                        "it away."
                ),
                ROTTEN_FLESH,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Beast Fat",
                MATERIAL,
                COMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "The fat from a slain beast."
                ),
                YELLOW_DYE,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Sage",
                HERB,
                UNCOMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "A shrub with grayish leaves, wooden stems, and flowers",
                        "with colors ranging from blue to purple.",
                        "Uses: Often used for purification",
                        "purposes as well as basic healing concoctions."
                ),
                SEAGRASS,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Venomcleanse",
                HERB,
                UNCOMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "This odd herb looks very menacing for what it does,",
                        "its dark green color almost warning you away from it.",
                        "Uses: This herb helps cleanse the body of toxins."
                ),
                FERN,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Silver Ingot",
                MATERIAL,
                UNCOMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "A glistening ingot of solid silver.",
                        "Slightly lighter hued than steel,",
                        "silver is renowned for its",
                        "effectiveness against cursed ones.",
                        "Used to enchant items."
                ),
                SUGAR,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Charcoal",
                MATERIAL,
                COMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "A good chunk of coal, it has many uses, but",
                        "mainly, it is burnt to heat metals or food."
                ),
                CHARCOAL,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Bronze Ingot",
                MATERIAL,
                COMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "A bar of bronze. It's a bit hefty,",
                        "and a little hard to hold",
                        "though the metal itself is as sturdy as",
                        "it is malleable.",
                        "Combine it with iron to make steel!"
                ),
                BRICK,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Stone Pebbles",
                MATERIAL,
                CRAP,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "Just a small pebble. Completely useless."
                ),
                STONE_BUTTON,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Cobblestone",
                MATERIAL,
                COMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "A chunk of raw cobblestone."
                ),
                COBBLESTONE,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Gravel Chunk",
                MATERIAL,
                CRAP,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "A small chunk of gravel. This material is",
                        "considered trash"
                ),
                BIRCH_BUTTON,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Iron Chunk",
                MATERIAL,
                COMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "A rough chunk of iron pulled",
                        "right out of the ground. Not",
                        "the best material.",
                        "Suitable for crafting.",
                        "Eight needed for iron ingot."
                ),
                IRON_ORE,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Wild Cherries",
                FOOD,
                UNCOMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "A bright red wild cherry.",
                        "Great for a quick snack, or for baking purposes.",
                        "Uses: Used for baking, drinks, or a quick snack."
                ),
                SWEET_BERRIES,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Valerian",
                HERB,
                COMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "Description: Dried root, use in tea.",
                        "Uses: Burning this, or the leaves, can leave a good smell.",
                        "It's often used for perfume."
                ),
                AZURE_BLUET,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Karan",
                HERB,
                COMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "Description: Dried wood.",
                        "Uses: Burning this, or the leaves, can leave a good smell.",
                        "It's often used for perfume."
                ),
                DANDELION,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Mastic",
                HERB,
                COMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "Description: A dry leaf that",
                        "releases an oily resin.",
                        "Uses: Used to enhance things like",
                        "essential oils."
                ),
                POPPY,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Fools' Weed",
                HERB,
                COMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "Description: Dried leaves or flowers.",
                        "Uses: Chewing this provides a",
                        "calming effect, ending rage or",
                        "fear effects.",
                        "Ends Frightened condition"
                ),
                LARGE_FERN,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Hops",
                HERB,
                COMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "Description: Specific aromas are produced",
                        "from this unique plant.",
                        "Uses: Used to preserve beer. Chewing",
                        "on them acts as a sedative. Lowers stress",
                        "and creates a calm effect."
                ),
                OAK_LEAVES,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Dried Grass",
                MISC,
                COMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "This grass has been in the sun for too long...",
                        "No known uses."
                ),
                DEAD_BUSH,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Dried Grass",
                MISC,
                COMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "This grass has been in the sun for too long...",
                        "No known uses."
                ),
                DEAD_BUSH,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Stinky Worm",
                MISC,
                COMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "It stinks, but I guess I could eat it?",
                        "Nasty food."
                ),
                OAK_BUTTON,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Handful of dirt",
                MISC,
                COMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "Just a handful of dirt.",
                        "No known uses."
                ),
                COARSE_DIRT,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Echinacea",
                HERB,
                UNCOMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "Description: Dried pink petals.",
                        "Uses: Medicine! Prevents infections",
                        "and helps resist illnesses. Most chew this.",
                        "Grants resistance to poison and gives 5 temp HP."
                ),
                PINK_TULIP,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Juniper",
                HERB,
                UNCOMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "Description: Tree with black berries on it.",
                        "Uses: Used in poison antidotes, can keep",
                        "skin smooth, it also helps keep wounds clean.",
                        "Ends the Poisoned condition."
                ),
                ACACIA_SAPLING,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Coriander",
                HERB,
                UNCOMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "Description: Small seeds.",
                        "Uses: Commonly used to spice up food",
                        "but also helps with mild healing.",
                        "Gives 2 temporary hit points. Doesn't stack."
                ),
                LILY_PAD,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Dittany",
                HERB,
                UNCOMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "Description: Small purple buds.",
                        "Uses: Usually used in teas. Good for",
                        "deliveries and flushing out poisons.",
                        "Ends the poisoned condition."
                ),
                ALLIUM,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Cloth of Gold",
                HERB,
                UNCOMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "Description: Dry yellow seeds.",
                        "Uses: Used to expand the mind, allows",
                        "brief communications with animals.",
                        "Allows the consumer to speak with two animals",
                        "of their choice for 1 hour."
                ),
                SUNFLOWER,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Eyebright",
                HERB,
                UNCOMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "Description: A pretty flower with",
                        "white and yellow petals.",
                        "Uses: Helps clear clouded eyes and helps with focus.",
                        "You gain advantage in Perception and ",
                        "Investigation checks for 1 hour."
                ),
                OXEYE_DAISY,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Yarrow",
                HERB,
                UNCOMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "Description: Whole white combs.",
                        "Uses: Used on major wounds. The",
                        "leaves contain oil that can help stop",
                        "bleeding once lathered on a wound.",
                        "Gives 1d4+2 HP"
                ),
                AZURE_BLUET,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Cloth of Emerald",
                HERB,
                RARE,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "Description: Dry, green seeds.",
                        "Uses: Used to expand the mind, allowing brief communications with plants.",
                        "Allows the consumer to speak with 2 plants of their choice",
                        "for 1 hour."
                ),
                SUNFLOWER,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Greater Healing Potion",
                GenericItemCategory.POTION,
                RARE,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "Restores 4d4+4 HP",
                        "Uses one action to drink/administer."
                ),
                Material.POTION,
                healingPotionMeta,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Supreme Healing Potion",
                GenericItemCategory.POTION,
                RARE,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "Restores 8d4+8 HP",
                        "Uses one action to drink/administer."
                ),
                Material.POTION,
                healingPotionMeta,
                new BlockedInteraction()
        ));
        PotionMeta lightnessPotionMeta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.POTION);
        if (lightnessPotionMeta != null) {
            lightnessPotionMeta.setBasePotionData(new PotionData(PotionType.FIRE_RESISTANCE));
        }
        saveItem(customItemFolder, new GenericItemType(
                "Potion of Lightness",
                GenericItemCategory.POTION,
                UNCOMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "For 1 hour, when you fall, you",
                        "slowly descend, taking no damage from falls.",
                        "Uses one action to drink/administer."
                ),
                Material.POTION,
                lightnessPotionMeta,
                new BlockedInteraction()
        ));
        PotionMeta climbingPotionMeta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.POTION);
        if (climbingPotionMeta != null) {
            climbingPotionMeta.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE));
        }
        saveItem(customItemFolder, new GenericItemType(
                "Potion of Climbing",
                GenericItemCategory.POTION,
                UNCOMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "For 1 hour, your climbing speed",
                        "equals that of your walking speed."
                ),
                Material.POTION,
                climbingPotionMeta,
                new BlockedInteraction()
        ));
        PotionMeta vigorPotionMeta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.POTION);
        if (vigorPotionMeta != null) {
            vigorPotionMeta.setBasePotionData(new PotionData(PotionType.SPEED));
        }
        saveItem(customItemFolder, new GenericItemType(
                "Potion of Vigor",
                GenericItemCategory.POTION,
                UNCOMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "You gain 10 temporary HP."
                ),
                Material.POTION,
                vigorPotionMeta,
                new BlockedInteraction()
        ));
        PotionMeta invisibilityPotionMeta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.POTION);
        if (invisibilityPotionMeta != null) {
            invisibilityPotionMeta.setBasePotionData(new PotionData(PotionType.SLOW_FALLING));
        }
        saveItem(customItemFolder, new GenericItemType(
                "Potion of Invisibility",
                GenericItemCategory.POTION,
                UNCOMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "For 1 hour, you are invisible, unless",
                        "you attack or cast a spell before",
                        "that time. Grants advantage on",
                        "Stealth checks."
                ),
                Material.POTION,
                invisibilityPotionMeta,
                new BlockedInteraction()
        ));
        PotionMeta heroismPotionMeta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.POTION);
        if (heroismPotionMeta != null) {
            heroismPotionMeta.setBasePotionData(new PotionData(PotionType.FIRE_RESISTANCE));
        }
        saveItem(customItemFolder, new GenericItemType(
                "Potion of Heroism",
                GenericItemCategory.POTION,
                UNCOMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "For 10 minutes, you cannot be frightened or charmed."
                ),
                Material.POTION,
                heroismPotionMeta,
                new BlockedInteraction()
        ));
        PotionMeta leaperPotionMeta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.POTION);
        if (leaperPotionMeta != null) {
            leaperPotionMeta.setBasePotionData(new PotionData(PotionType.JUMP));
        }
        saveItem(customItemFolder, new GenericItemType(
                "Potion of the leaper",
                GenericItemCategory.POTION,
                UNCOMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "For 1 hour, you can jump in an",
                        "upwards direction up to 25 ft,",
                        "and take no damage from falling up to 30 ft of distance"
                ),
                Material.POTION,
                leaperPotionMeta,
                new BlockedInteraction()
        ));
        PotionMeta swiftnessPotionMeta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.POTION);
        if (swiftnessPotionMeta != null) {
            swiftnessPotionMeta.setBasePotionData(new PotionData(PotionType.SPEED));
        }
        saveItem(customItemFolder, new GenericItemType(
                "Potion of Swiftness",
                GenericItemCategory.POTION,
                UNCOMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "Your walking speed increases",
                        "by 15 ft for 1 hour."
                ),
                Material.POTION,
                swiftnessPotionMeta,
                new BlockedInteraction()
        ));
        PotionMeta vialOfPoisonMeta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.POTION);
        if (vialOfPoisonMeta != null) {
            vialOfPoisonMeta.setBasePotionData(new PotionData(PotionType.POISON));
        }
        saveItem(customItemFolder, new GenericItemType(
                "Vial of Poison",
                GenericItemCategory.POTION,
                UNCOMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Arrays.asList(
                        "Anyone who drinks this takes",
                        "3d6 poison damage and must",
                        "make a constution saving",
                        "throw DC 15 or be poisoned",
                        "until the next long rest."
                ),
                Material.POTION,
                vialOfPoisonMeta,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Gem Shard",
                GEODE,
                CRAP,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Collections.emptyList(),
                PRISMARINE_SHARD,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Broken Flint Piece",
                GEODE,
                CRAP,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Collections.emptyList(),
                FLINT,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Quartz Crystal",
                GEODE,
                COMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Collections.emptyList(),
                QUARTZ,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Citrine Crystal",
                GEODE,
                COMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Collections.emptyList(),
                CLAY_BALL,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Garnet Crystal",
                GEODE,
                UNCOMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Collections.emptyList(),
                GLOWSTONE_DUST,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Spinel Crystal",
                GEODE,
                UNCOMMON,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Collections.emptyList(),
                PRISMARINE_CRYSTALS,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Lapis Crystal",
                GEODE,
                RARE,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Collections.emptyList(),
                LAPIS_LAZULI,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Jade Crystal",
                GEODE,
                RARE,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Collections.emptyList(),
                EMERALD,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Diamond Crystal",
                GEODE,
                LEGENDARY,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Collections.emptyList(),
                DIAMOND,
                new BlockedInteraction()
        ));
        saveItem(customItemFolder, new GenericItemType(
                "Obsidian Crystal",
                GEODE,
                MYTHIC,
                new CurrencyValue(0, GP),
                new Weight(0, LB),
                Collections.emptyList(),
                POPPED_CHORUS_FRUIT,
                new BlockedInteraction()
        ));
    }

    private void saveItem(File itemFolder, CasusItemType itemType) {
        File itemFile = new File(itemFolder, itemType.getName() + ".yml");
        if (!itemFile.getParentFile().exists()) {
            itemFile.getParentFile().mkdirs();
        }
        YamlConfiguration itemConfiguration = new YamlConfiguration();
        itemConfiguration.set("item", itemType);
        try {
            itemConfiguration.save(itemFile);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public CasusItemStack fromBukkitItemStack(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            CasusItemType itemType = getItemType(ChatColor.stripColor(meta.getDisplayName()));
            if (itemType != null) {
                return new CasusItemStack(itemType, itemStack.getAmount());
            }
        }
        return null;
    }

}
