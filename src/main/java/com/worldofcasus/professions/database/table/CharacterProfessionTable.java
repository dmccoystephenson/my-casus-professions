package com.worldofcasus.professions.database.table;

import com.rpkit.characters.bukkit.character.RPKCharacter;
import com.worldofcasus.professions.database.Database;
import com.worldofcasus.professions.profession.Profession;
import com.worldofcasus.professions.profession.ProfessionId;
import org.jooq.Record;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.worldofcasus.professions.database.jooq.casus.Tables.CHARACTER_PROFESSION;
import static org.jooq.impl.DSL.constraint;

public final class CharacterProfessionTable implements Table {

    private final Database database;

    public CharacterProfessionTable(Database database) {
        this.database = database;
    }

    @Override
    public void create() {
        database.create()
                .createTableIfNotExists(CHARACTER_PROFESSION)
                .columns(
                        CHARACTER_PROFESSION.CHARACTER_ID,
                        CHARACTER_PROFESSION.PROFESSION_ID
                )
                .constraints(
                        constraint("pk_character_profession").primaryKey(CHARACTER_PROFESSION.CHARACTER_ID)
                )
                .execute();
    }

    public CompletableFuture<Optional<Profession>> get(RPKCharacter character) {
        return CompletableFuture.supplyAsync(() -> {
            Record result = database.create()
                    .select(CHARACTER_PROFESSION.PROFESSION_ID)
                    .from(CHARACTER_PROFESSION)
                    .where(CHARACTER_PROFESSION.CHARACTER_ID.eq(character.getId()))
                    .fetchOne();
            if (result == null) return Optional.empty();
            ProfessionId professionId = new ProfessionId(result.get(CHARACTER_PROFESSION.PROFESSION_ID));
            return database.getTable(ProfessionTable.class)
                    .get(professionId).join();
        });
    }

    public CompletableFuture<Void> insertOrUpdate(RPKCharacter character, Profession profession) {
        return CompletableFuture.runAsync(() ->
                database.create()
                        .insertInto(
                                CHARACTER_PROFESSION,
                                CHARACTER_PROFESSION.CHARACTER_ID,
                                CHARACTER_PROFESSION.PROFESSION_ID
                        )
                        .values(
                                character.getId(),
                                profession.getId().getValue()
                        )
                        .onDuplicateKeyUpdate()
                        .set(CHARACTER_PROFESSION.PROFESSION_ID, profession.getId().getValue())
                        .where(CHARACTER_PROFESSION.CHARACTER_ID.eq(character.getId()))
                        .execute()
        );
    }
}
