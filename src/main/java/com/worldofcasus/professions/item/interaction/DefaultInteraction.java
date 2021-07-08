package com.worldofcasus.professions.item.interaction;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("DefaultInteraction")
public final class DefaultInteraction implements Interaction, ConfigurationSerializable {

    @Override
    public void interact(PlayerInteractEvent event) {}

    @Override
    public @NotNull Map<String, Object> serialize() {
        return new HashMap<>();
    }

    public static DefaultInteraction deserialize(Map<String, Object> serialized) {
        return new DefaultInteraction();
    }

}
