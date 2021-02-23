package com.worldofcasus.professions.stamina;

import com.worldofcasus.professions.CasusProfessions;
import com.worldofcasus.professions.database.table.CharacterStaminaTable;
import com.rpkit.characters.bukkit.character.RPKCharacter;
import com.rpkit.core.service.Service;

public final class StaminaService implements Service {

    public static final int MAX_STAMINA = 100;

    private final CasusProfessions plugin;

    public StaminaService(CasusProfessions plugin) {
        this.plugin = plugin;
    }

    @Override
    public CasusProfessions getPlugin() {
        return plugin;
    }

    public int getStamina(RPKCharacter character) {
        return plugin.getDatabase().getTable(CharacterStaminaTable.class).get(character).orElse(MAX_STAMINA);
    }

    public void setStamina(RPKCharacter character, int stamina) {
        plugin.getDatabase().getTable(CharacterStaminaTable.class).insertOrUpdate(character, stamina);
    }

    public void restoreStamina() {
        plugin.getDatabase().getTable(CharacterStaminaTable.class).restoreStamina();
    }

}
