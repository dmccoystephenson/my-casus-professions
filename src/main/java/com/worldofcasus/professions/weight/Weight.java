package com.worldofcasus.professions.weight;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.signum;

@SerializableAs("Weight")
public final class Weight implements Comparable<Weight>, ConfigurationSerializable {

    private final double value;
    private final WeightUnit unit;

    public Weight(double value, WeightUnit unit) {
        this.value = value;
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }

    public WeightUnit getUnit() {
        return unit;
    }

    public Weight to(WeightUnit unit) {
        return new Weight((getValue() / getUnit().getScaleFactor()) * unit.getScaleFactor(), unit);
    }

    public Weight multiply(int amount) {
        return new Weight(getValue() * amount, getUnit());
    }

    @Override
    public int compareTo(@NotNull Weight weight) {
        return (int) signum((getValue() / getUnit().getScaleFactor()) - (weight.getValue() / weight.getUnit().getScaleFactor()));
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("value", value);
        serialized.put("unit", unit.getName());
        return serialized;
    }

    public static Weight deserialize(Map<String, Object> serialized) {
        return new Weight(
                (double) serialized.get("value"),
                WeightUnit.getByName((String) serialized.get("unit"))
        );
    }

    @Override
    public String toString() {
        if (getValue() == (long) getValue()) {
            return String.format("%.0f%s", getValue(), getUnit().getName());
        } else {
            return String.format("%s%s", getValue(), getUnit().getName());
        }
    }
}