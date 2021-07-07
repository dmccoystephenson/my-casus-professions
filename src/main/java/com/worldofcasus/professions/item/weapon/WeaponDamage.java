package com.worldofcasus.professions.item.weapon;

import com.worldofcasus.professions.damage.DamageType;
import com.worldofcasus.professions.roll.Roll;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("WeaponDamage")
public final class WeaponDamage implements ConfigurationSerializable {

    private final Roll roll;
    private final DamageType damageType;

    public WeaponDamage(Roll roll, DamageType damageType) {
        this.roll = roll;
        this.damageType = damageType;
    }

    public Roll getRoll() {
        return roll;
    }

    public DamageType getDamageType() {
        return damageType;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("roll", roll.toString());
        serialized.put("damage-type", damageType.name());
        return serialized;
    }

    public static WeaponDamage deserialize(Map<String, Object> serialized) {
        return new WeaponDamage(
                Roll.parse((String) serialized.get("roll")),
                DamageType.valueOf((String) serialized.get("damage-type"))
        );
    }
}
