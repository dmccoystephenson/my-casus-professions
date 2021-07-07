package com.worldofcasus.professions.ability;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("AbilityRequirement")
public class AbilityRequirement implements ConfigurationSerializable {

    private final Ability ability;
    private final int minimum;

    public AbilityRequirement(Ability ability, int minimum) {
        this.ability = ability;
        this.minimum = minimum;
    }

    public Ability getAbility() {
        return ability;
    }

    public int getMinimum() {
        return minimum;
    }

    @Override
    public String toString() {
        return ">" + getMinimum() + " " + getAbility().getName();
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("ability", ability.name());
        serialized.put("minimum", minimum);
        return serialized;
    }

    public static AbilityRequirement deserialize(Map<String, Object> serialized) {
        return new AbilityRequirement(
                Ability.valueOf((String) serialized.get("ability")),
                (int) serialized.get("name")
        );
    }
}
