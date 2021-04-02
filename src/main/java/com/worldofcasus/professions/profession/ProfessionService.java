package com.worldofcasus.professions.profession;

import com.rpkit.characters.bukkit.character.RPKCharacter;
import com.rpkit.core.service.ServiceProvider;
import com.worldofcasus.professions.CasusProfessions;
import com.worldofcasus.professions.database.table.CharacterProfessionTable;
import com.worldofcasus.professions.database.table.ProfessionTable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class ProfessionService implements ServiceProvider {

    private final CasusProfessions plugin;

    public ProfessionService(CasusProfessions plugin) {
        this.plugin = plugin;
    }

    public CasusProfessions getPlugin() {
        return plugin;
    }

    public CompletableFuture<Optional<Profession>> getProfession(String name) {
        return plugin.getDatabase().getTable(ProfessionTable.class).get(name);
    }

    public CompletableFuture<List<Profession>> getProfessions() {
        return plugin.getDatabase().getTable(ProfessionTable.class).getAll();
    }

    public CompletableFuture<Optional<Profession>> addProfession(Profession profession) {
        return plugin.getDatabase().getTable(ProfessionTable.class).insert(profession);
    }

    public CompletableFuture<Optional<Profession>> getProfession(RPKCharacter character) {
        return plugin.getDatabase().getTable(CharacterProfessionTable.class).get(character);
    }

    public CompletableFuture<Void> setProfession(RPKCharacter character, Profession profession) {
        return plugin.getDatabase().getTable(CharacterProfessionTable.class).insertOrUpdate(character, profession);
    }

    public CompletableFuture<Void> unsetProfession(RPKCharacter character) {
        return plugin.getDatabase().getTable(CharacterProfessionTable.class).delete(character);
    }

}
