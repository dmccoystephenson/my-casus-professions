package com.worldofcasus.professions;

import com.rpkit.core.bukkit.plugin.RPKBukkitPlugin;
import com.rpkit.core.service.Services;
import com.worldofcasus.professions.ability.AbilityRequirement;
import com.worldofcasus.professions.ability.StrengthRequirement;
import com.worldofcasus.professions.armorclass.ArmorClassCalculation;
import com.worldofcasus.professions.command.dnditem.DnDItemCommand;
import com.worldofcasus.professions.command.node.NodeCommand;
import com.worldofcasus.professions.command.profession.ProfessionCommand;
import com.worldofcasus.professions.command.stamina.StaminaCommand;
import com.worldofcasus.professions.database.Database;
import com.worldofcasus.professions.distance.Distance;
import com.worldofcasus.professions.distance.DistanceUnit;
import com.worldofcasus.professions.item.ItemService;
import com.worldofcasus.professions.item.armor.ArmorType;
import com.worldofcasus.professions.item.generic.GenericItemType;
import com.worldofcasus.professions.item.interaction.BlockedInteraction;
import com.worldofcasus.professions.item.interaction.DefaultInteraction;
import com.worldofcasus.professions.item.weapon.WeaponDamage;
import com.worldofcasus.professions.item.weapon.WeaponProperty;
import com.worldofcasus.professions.item.weapon.WeaponType;
import com.worldofcasus.professions.listener.InventoryClickListener;
import com.worldofcasus.professions.listener.PlayerInteractListener;
import com.worldofcasus.professions.money.CurrencyValue;
import com.worldofcasus.professions.node.NodeService;
import com.worldofcasus.professions.profession.ProfessionService;
import com.worldofcasus.professions.stamina.StaminaRestoreRunnable;
import com.worldofcasus.professions.stamina.StaminaService;
import com.worldofcasus.professions.weight.Weight;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

public final class CasusProfessions extends RPKBukkitPlugin {

    private Database database;

    @Override
    public void onEnable() {
        // Item types
        ConfigurationSerialization.registerClass(ArmorType.class, "ArmorType");
        ConfigurationSerialization.registerClass(WeaponType.class, "WeaponType");
        ConfigurationSerialization.registerClass(GenericItemType.class, "GenericItemType");

        // Ability requirements
        ConfigurationSerialization.registerClass(AbilityRequirement.class, "AbilityRequirement");
        ConfigurationSerialization.registerClass(StrengthRequirement.class, "StrengthRequirement");

        // Weapon damage
        ConfigurationSerialization.registerClass(WeaponDamage.class, "WeaponDamage");

        // Armor class calculation
        ConfigurationSerialization.registerClass(ArmorClassCalculation.class, "ArmorClassCalculation");
        ConfigurationSerialization.registerClass(ArmorClassCalculation.AbilityModifierArmorClassComponent.class, "AbilityModifierArmorClassComponent");
        ConfigurationSerialization.registerClass(ArmorClassCalculation.BaseArmorClassComponent.class, "BaseArmorClassComponent");
        ConfigurationSerialization.registerClass(ArmorClassCalculation.CappedAbilityModifierArmorClassComponent.class, "CappedAbilityModifierArmorClassComponent");

        // Weapon properties
        ConfigurationSerialization.registerClass(WeaponProperty.Ammunition.class, "Ammunition");
        ConfigurationSerialization.registerClass(WeaponProperty.Finesse.class, "Finesse");
        ConfigurationSerialization.registerClass(WeaponProperty.Heavy.class, "Heavy");
        ConfigurationSerialization.registerClass(WeaponProperty.Light.class, "Light");
        ConfigurationSerialization.registerClass(WeaponProperty.Loading.class, "Loading");
        ConfigurationSerialization.registerClass(WeaponProperty.Range.class, "Range");
        ConfigurationSerialization.registerClass(WeaponProperty.Reach.class, "Reach");
        ConfigurationSerialization.registerClass(WeaponProperty.Special.class, "Special");
        ConfigurationSerialization.registerClass(WeaponProperty.Thrown.class, "Thrown");
        ConfigurationSerialization.registerClass(WeaponProperty.TwoHanded.class, "TwoHanded");
        ConfigurationSerialization.registerClass(WeaponProperty.Versatile.class, "Versatile");

        // Measurement
        ConfigurationSerialization.registerClass(CurrencyValue.class, "CurrencyValue");
        ConfigurationSerialization.registerClass(Distance.class, "Distance");
        ConfigurationSerialization.registerClass(DistanceUnit.class, "DistanceUnit");
        ConfigurationSerialization.registerClass(Weight.class, "Weight");

        // Interactions
        ConfigurationSerialization.registerClass(BlockedInteraction.class, "BlockedInteraction");
        ConfigurationSerialization.registerClass(DefaultInteraction.class, "DefaultInteraction");

        saveDefaultConfig();
        database = new Database(
                this,
                getConfig().getString("database.url"),
                getConfig().getString("database.username"),
                getConfig().getString("database.password")
        );

        Services.INSTANCE.set(StaminaService.class, new StaminaService(this));
        Services.INSTANCE.set(NodeService.class, new NodeService(this));
        Services.INSTANCE.set(ProfessionService.class, new ProfessionService(this));
        Services.INSTANCE.set(ItemService.class, new ItemService(this));

        // getServer().clearRecipes(); # commented out at the request of TheRanger

        new StaminaRestoreRunnable().runTaskTimer(this, 36000L, 72000L);

        registerCommands();
        registerListeners();
    }

    private void registerCommands() {
        getCommand("node").setExecutor(new NodeCommand(this));
        getCommand("profession").setExecutor(new ProfessionCommand(this));
        getCommand("stamina").setExecutor(new StaminaCommand(this));
        getCommand("dnditem").setExecutor(new DnDItemCommand(this));
    }

    private void registerListeners() {
        registerListeners(new PlayerInteractListener(this), new InventoryClickListener());
    }

    public Database getDatabase() {
        return database;
    }

}
