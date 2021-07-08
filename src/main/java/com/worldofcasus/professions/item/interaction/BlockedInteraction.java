package com.worldofcasus.professions.item.interaction;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("BlockedInteraction")
public final class BlockedInteraction implements Interaction, ConfigurationSerializable {

    @Override
    public void interact(PlayerInteractEvent event) {
        event.setCancelled(true);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return new HashMap<>();
    }

    public static BlockedInteraction deserialize(Map<String, Object> serialized) {
        return new BlockedInteraction();
    }

}
