package com.worldofcasus.professions.armorclass;

import com.worldofcasus.professions.ability.Ability;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("ArmorClassCalculation")
public final class ArmorClassCalculation implements ConfigurationSerializable {

    private final List<Component> components;

    public ArmorClassCalculation(ArmorClassCalculation.Component... components) {
        this.components = Arrays.asList(components);
    }

    public ArmorClassCalculation(List<ArmorClassCalculation.Component> components) {
        this.components = components;
    }

    public interface Component extends ConfigurationSerializable {
//        public int calculateValue(RPKCharacter character);
    }

    @SerializableAs("BaseArmorClassComponent")
    public static final class BaseArmorClassComponent implements Component {

        private final int value;

        public BaseArmorClassComponent(int value) {
            this.value = value;
        }

//        @Override
//        public int calculateValue(RPKCharacter character) {
//            return value;
//        }


        @Override
        public @NotNull Map<String, Object> serialize() {
            Map<String, Object> serialized = new HashMap<>();
            serialized.put("value", value);
            return serialized;
        }

        public static BaseArmorClassComponent deserialize(Map<String, Object> serialized) {
            return new BaseArmorClassComponent((int) serialized.get("value"));
        }

        @Override
        public String toString() {
            return Integer.toString(value);
        }

    }

    @SerializableAs("AbilityModifierArmorClassComponent")
    public static final class AbilityModifierArmorClassComponent implements Component {

        private final Ability ability;

        public AbilityModifierArmorClassComponent(Ability ability) {
            this.ability = ability;
        }

//        @Override
//        public int calculateValue(RPKCharacter character) {
//            return character.getModifier(ability);
//        }


        @Override
        public @NotNull Map<String, Object> serialize() {
            Map<String, Object> serialized = new HashMap<>();
            serialized.put("ability", ability.name());
            return serialized;
        }

        public static AbilityModifierArmorClassComponent deserialize(Map<String, Object> serialized) {
            return new AbilityModifierArmorClassComponent(Ability.valueOf((String) serialized.get("ability")));
        }

        @Override
        public String toString() {
            return ability.getAbbreviation() + " Modifier";
        }

    }

    @SerializableAs("CappedAbilityModifierArmorClassComponent")
    public static final class CappedAbilityModifierArmorClassComponent implements Component {

        private final Ability ability;
        private final int cap;

        public CappedAbilityModifierArmorClassComponent(Ability ability, int cap) {
            this.ability = ability;
            this.cap = cap;
        }

//        @Override
//        public int calculateValue(RPKCharacter character) {
//            return Math.min(cap, character.getModifier(ability));
//        }


        @Override
        public @NotNull Map<String, Object> serialize() {
            Map<String, Object> serialized = new HashMap<>();
            serialized.put("ability", ability.name());
            serialized.put("cap", cap);
            return serialized;
        }

        public static CappedAbilityModifierArmorClassComponent deserialize(Map<String, Object> serialized) {
            return new CappedAbilityModifierArmorClassComponent(
                    Ability.valueOf((String) serialized.get("ability")),
                    (int) serialized.get("cap")
            );
        }

        @Override
        public String toString() {
            return ability.getAbbreviation() + " Modifier (max of " + cap + ")";
        }

    }

//    public ArmorClass calculate(RPKCharacter character) {
//        return new ArmorClass(components.stream()
//                .map(component -> component.calculateValue(character))
//                .reduce(0, Integer::sum));
//    }


    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("components", components);
        return serialized;
    }

    public static ArmorClassCalculation deserialize(Map<String, Object> serialized) {
        return new ArmorClassCalculation((List<Component>) serialized.get("components"));
    }

    @Override
    public String toString() {
        return components.stream().map(Component::toString).reduce((a, b) -> a + " + " + b).orElse("0");
    }

}
