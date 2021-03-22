package com.worldofcasus.professions.stamina;

import com.rpkit.characters.bukkit.character.RPKCharacter;
import com.rpkit.core.service.ServiceProvider;
import com.worldofcasus.professions.CasusProfessions;
import com.worldofcasus.professions.database.table.CharacterStaminaTable;

import java.util.concurrent.CompletableFuture;

public final class StaminaService implements ServiceProvider {

    public static final int MAX_STAMINA = 100;

    private final CasusProfessions plugin;

    public StaminaService(CasusProfessions plugin) {
        this.plugin = plugin;
    }

    public CasusProfessions getPlugin() {
        return plugin;
    }

    public CompletableFuture<Integer> getStamina(RPKCharacter character) {
        return plugin.getDatabase().getTable(CharacterStaminaTable.class).get(character)
                .thenApply((characterStamina) -> characterStamina.orElse(MAX_STAMINA));
    }

    public CompletableFuture<Void> setStamina(RPKCharacter character, int stamina) {
        return plugin.getDatabase().getTable(CharacterStaminaTable.class).insertOrUpdate(character, stamina);
    }

    public CompletableFuture<Void> restoreStamina() {
        return plugin.getDatabase().getTable(CharacterStaminaTable.class).restoreStamina();
    }

}
