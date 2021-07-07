package com.worldofcasus.professions.distance;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("DistanceUnit")
public final class DistanceUnit implements ConfigurationSerializable {

    public static final DistanceUnit FEET = new DistanceUnit("ft", 50);
    public static final DistanceUnit INCHES = new DistanceUnit("in", 600);

    public static final DistanceUnit METRES = new DistanceUnit("m", 15);
    public static final DistanceUnit CENTIMETRES = new DistanceUnit("cm", 1500);

    private final String name;
    private final double scaleFactor;

    private DistanceUnit(String name, double scaleFactor) {
        this.name = name;
        this.scaleFactor = scaleFactor;
    }

    public String getName() {
        return name;
    }

    public double getScaleFactor() {
        return scaleFactor;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("name", name);
        serialized.put("scale-factor", scaleFactor);
        return serialized;
    }

    public static DistanceUnit deserialize(Map<String, Object> serialized) {
        return new DistanceUnit(
                (String) serialized.get("name"),
                (double) serialized.get("scale-factor")
        );
    }

}
