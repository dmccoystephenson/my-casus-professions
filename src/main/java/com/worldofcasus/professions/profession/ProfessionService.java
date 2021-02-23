package com.worldofcasus.professions.profession;

import com.worldofcasus.professions.CasusProfessions;
import com.worldofcasus.professions.database.table.CharacterProfessionTable;
import com.worldofcasus.professions.database.table.ProfessionTable;
import com.rpkit.characters.bukkit.character.RPKCharacter;
import com.rpkit.core.service.Service;

import java.util.List;
import java.util.Optional;

public final class ProfessionService implements Service {

    private final CasusProfessions plugin;

    public ProfessionService(CasusProfessions plugin) {
        this.plugin = plugin;
    }

    @Override
    public CasusProfessions getPlugin() {
        return plugin;
    }

    public Optional<Profession> getProfession(String name) {
        return plugin.getDatabase().getTable(ProfessionTable.class).get(name);
    }

    public List<Profession> getProfessions() {
        return plugin.getDatabase().getTable(ProfessionTable.class).getAll();
    }

    public void addProfession(Profession profession) {
        plugin.getDatabase().getTable(ProfessionTable.class).insert(profession);
    }

    public Optional<Profession> getProfession(RPKCharacter character) {
        return plugin.getDatabase().getTable(CharacterProfessionTable.class).get(character);
    }

    public void setProfession(RPKCharacter character, Profession profession) {
        plugin.getDatabase().getTable(CharacterProfessionTable.class).insertOrUpdate(character, profession);
    }

}
