package com.worldofcasus.professions.database.table;

import com.worldofcasus.professions.CasusProfessions;
import com.worldofcasus.professions.database.Database;
import com.worldofcasus.professions.profession.Profession;
import com.worldofcasus.professions.profession.ProfessionId;
import com.rpkit.characters.bukkit.character.RPKCharacter;
import org.jooq.Record;

import java.util.Optional;

import static com.worldofcasus.professions.database.jooq.casus.Tables.CHARACTER_PROFESSION;
import static org.jooq.impl.DSL.constraint;

public final class CharacterProfessionTable implements Table {

    private final CasusProfessions plugin;
    private final Database database;

    public CharacterProfessionTable(CasusProfessions plugin, Database database) {
        this.plugin = plugin;
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

    public Optional<Profession> get(RPKCharacter character) {
        Record result = database.create()
                .select(CHARACTER_PROFESSION.PROFESSION_ID)
                .from(CHARACTER_PROFESSION)
                .where(CHARACTER_PROFESSION.CHARACTER_ID.eq(character.getId().getValue()))
                .fetchOne();
        if (result == null) return Optional.empty();
        ProfessionId professionId = new ProfessionId(result.get(CHARACTER_PROFESSION.PROFESSION_ID));
        return database.getTable(ProfessionTable.class)
                .get(professionId);
    }

    public void insertOrUpdate(RPKCharacter character, Profession profession) {
        database.create()
                .insertInto(
                        CHARACTER_PROFESSION,
                        CHARACTER_PROFESSION.CHARACTER_ID,
                        CHARACTER_PROFESSION.PROFESSION_ID
                )
                .values(
                        character.getId().getValue(),
                        profession.getId().getValue()
                )
                .onDuplicateKeyUpdate()
                .set(CHARACTER_PROFESSION.PROFESSION_ID, profession.getId().getValue())
                .where(CHARACTER_PROFESSION.CHARACTER_ID.eq(character.getId().getValue()))
                .execute();
    }
}
