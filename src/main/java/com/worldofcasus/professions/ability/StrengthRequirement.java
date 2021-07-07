package com.worldofcasus.professions.ability;

import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.worldofcasus.professions.ability.Ability.STRENGTH;

@SerializableAs("StrengthRequirement")
public class StrengthRequirement extends AbilityRequirement {
    public StrengthRequirement(int minimum) {
        super(STRENGTH, minimum);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("minimum", getMinimum());
        return serialized;
    }

    public static StrengthRequirement deserialize(Map<String, Object> serialized) {
        return new StrengthRequirement((int) serialized.get("minimum"));
    }
}
