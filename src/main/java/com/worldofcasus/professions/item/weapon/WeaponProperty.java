package com.worldofcasus.professions.item.weapon;

import com.worldofcasus.professions.distance.Distance;
import com.worldofcasus.professions.roll.Roll;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public interface WeaponProperty {

    @SerializableAs("Ammunition")
    final class Ammunition implements WeaponProperty, ConfigurationSerializable {
        @Override
        public String toString() {
            return "Ammunition";
        }

        @Override
        public @NotNull Map<String, Object> serialize() {
            return new HashMap<>();
        }

        public static Ammunition deserialize(Map<String, Object> serialized) {
            return new Ammunition();
        }
    }

    @SerializableAs("Finesse")
    final class Finesse implements WeaponProperty, ConfigurationSerializable {
        @Override
        public String toString() {
            return "Finesse";
        }

        @Override
        public @NotNull Map<String, Object> serialize() {
            return new HashMap<>();
        }

        public static Finesse deserialize(Map<String, Object> serialized) {
            return new Finesse();
        }
    }

    @SerializableAs("Heavy")
    final class Heavy implements WeaponProperty, ConfigurationSerializable {
        @Override
        public String toString() {
            return "Heavy";
        }

        @Override
        public @NotNull Map<String, Object> serialize() {
            return new HashMap<>();
        }

        public static Heavy deserialize(Map<String, Object> serialized) {
            return new Heavy();
        }
    }

    @SerializableAs("Light")
    final class Light implements WeaponProperty, ConfigurationSerializable {
        @Override
        public String toString() {
            return "Light";
        }

        @Override
        public @NotNull Map<String, Object> serialize() {
            return new HashMap<>();
        }

        public static Light deserialize(Map<String, Object> serialized) {
            return new Light();
        }
    }

    @SerializableAs("Loading")
    final class Loading implements WeaponProperty, ConfigurationSerializable {
        @Override
        public String toString() {
            return "Loading";
        }

        @Override
        public @NotNull Map<String, Object> serialize() {
            return new HashMap<>();
        }

        public static Loading deserialize(Map<String, Object> serialized) {
            return new Loading();
        }
    }

    @SerializableAs("Range")
    final class Range implements WeaponProperty, ConfigurationSerializable {

        private final Distance normalRange;
        private final Distance longRange;

        public Range(Distance normalRange, Distance longRange) {
            this.normalRange = normalRange;
            this.longRange = longRange;
        }

        public Distance getNormalRange() {
            return normalRange;
        }

        public Distance getLongRange() {
            return longRange;
        }

        @Override
        public String toString() {
            return "Range (" +
                    "normal " + getNormalRange().toString() +
                    ", long " + getLongRange().toString() +
                    ')';
        }

        @Override
        public @NotNull Map<String, Object> serialize() {
            Map<String, Object> serialized = new HashMap<>();
            serialized.put("normal-range", normalRange);
            serialized.put("long-range", longRange);
            return serialized;
        }

        public static Range deserialize(Map<String, Object> serialized) {
            return new Range(
                    (Distance) serialized.get("normal-range"),
                    (Distance) serialized.get("long-range")
            );
        }
    }

    @SerializableAs("Reach")
    final class Reach implements WeaponProperty, ConfigurationSerializable {
        @Override
        public String toString() {
            return "Reach";
        }

        @Override
        public @NotNull Map<String, Object> serialize() {
            return new HashMap<>();
        }

        public static Reach deserialize(Map<String, Object> serialized) {
            return new Reach();
        }
    }

    @SerializableAs("Special")
    final class Special implements WeaponProperty, ConfigurationSerializable {

        private final String description;

        public Special(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return "Special (" + getDescription() + ')';
        }

        @Override
        public @NotNull Map<String, Object> serialize() {
            Map<String, Object> serialized = new HashMap<>();
            serialized.put("description", description);
            return serialized;
        }

        public static Special deserialize(Map<String, Object> serialized) {
            return new Special((String) serialized.get("description"));
        }
    }

    @SerializableAs("Thrown")
    final class Thrown implements WeaponProperty, ConfigurationSerializable {
        @Override
        public String toString() {
            return "Thrown";
        }

        @Override
        public @NotNull Map<String, Object> serialize() {
            return new HashMap<>();
        }

        public static Thrown deserialize(Map<String, Object> serialized) {
            return new Thrown();
        }
    }

    @SerializableAs("TwoHanded")
    final class TwoHanded implements WeaponProperty, ConfigurationSerializable {
        @Override
        public String toString() {
            return "Two-handed";
        }

        @Override
        public @NotNull Map<String, Object> serialize() {
            return new HashMap<>();
        }

        public static TwoHanded deserialize(Map<String, Object> serialized) {
            return new TwoHanded();
        }
    }

    @SerializableAs("Versatile")
    final class Versatile implements WeaponProperty, ConfigurationSerializable {

        private final Roll twoHandedRoll;

        public Versatile(Roll twoHandedRoll) {
            this.twoHandedRoll = twoHandedRoll;
        }

        public Roll getTwoHandedRoll() {
            return twoHandedRoll;
        }

        @Override
        public String toString() {
            return "Versatile (" +
                    "two-handed roll " + getTwoHandedRoll().toString() +
                    ')';
        }

        @Override
        public @NotNull Map<String, Object> serialize() {
            Map<String, Object> serialized = new HashMap<>();
            serialized.put("two-handed-roll", twoHandedRoll.toString());
            return serialized;
        }

        public static Versatile deserialize(Map<String, Object> serialized) {
            return new Versatile(Roll.parse((String) serialized.get("two-handed-roll")));
        }
    }
}

