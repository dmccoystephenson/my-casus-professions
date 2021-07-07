package com.worldofcasus.professions.money;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("CurrencyValue")
public final class CurrencyValue implements ConfigurationSerializable {

    private final int amount;
    private final Currency currency;

    public CurrencyValue(int amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("amount", amount);
        serialized.put("currency", currency.getName());
        return serialized;
    }

    public static CurrencyValue deserialize(Map<String, Object> serialized) {
        return new CurrencyValue(
                (int) serialized.get("amount"),
                Currency.getByName((String) serialized.get("currency"))
        );
    }
}
