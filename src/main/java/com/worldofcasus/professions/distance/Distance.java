package com.worldofcasus.professions.distance;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.round;

@SerializableAs("Distance")
public final class Distance implements Comparable<Distance>, ConfigurationSerializable {

    private final double value;
    private final DistanceUnit unit;

    public Distance(double value, DistanceUnit unit) {
        this.value = value;
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }

    public DistanceUnit getUnit() {
        return unit;
    }

    public Distance to(DistanceUnit unit) {
        return new Distance((getValue() / getUnit().getScaleFactor()) * unit.getScaleFactor(), unit);
    }

    @Override
    public int compareTo(@NotNull Distance distance) {
        return (int) round((getValue() / getUnit().getScaleFactor()) - (distance.getValue() / distance.getUnit().getScaleFactor()));
    }

    @Override
    public String toString() {
        if (getValue() == (long) getValue()) {
            return String.format("%.0f%s", getValue(), getUnit().getName());
        } else {
            return String.format("%s%s", getValue(), getUnit().getName());
        }
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("value", value);
        serialized.put("unit", unit);
        return serialized;
    }

    public static Distance deserialize(Map<String, Object> serialized) {
        return new Distance(
                (double) serialized.get("value"),
                (DistanceUnit) serialized.get("unit")
        );
    }

}
