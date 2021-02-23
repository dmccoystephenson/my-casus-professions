package com.worldofcasus.professions.database.table;

import com.worldofcasus.professions.CasusProfessions;
import com.worldofcasus.professions.database.Database;
import com.worldofcasus.professions.stamina.StaminaService;
import com.rpkit.characters.bukkit.character.RPKCharacter;
import org.jooq.Record;

import java.util.Optional;

import static com.worldofcasus.professions.database.jooq.casus.Tables.CHARACTER_STAMINA;
import static org.jooq.impl.DSL.*;

public final class CharacterStaminaTable implements Table {

    private final CasusProfessions plugin;
    private final Database database;

    public CharacterStaminaTable(CasusProfessions plugin, Database database) {
        this.plugin = plugin;
        this.database = database;
    }

    @Override
    public void create() {
        database.create()
                .createTableIfNotExists(CHARACTER_STAMINA)
                .columns(
                        CHARACTER_STAMINA.CHARACTER_ID,
                        CHARACTER_STAMINA.STAMINA
                )
                .constraints(
                    constraint("pk_character_stamina").primaryKey(CHARACTER_STAMINA.CHARACTER_ID)
                )
                .execute();
    }

    public void insertOrUpdate(RPKCharacter character, int stamina) {
        database.create()
                .insertInto(
                        CHARACTER_STAMINA,
                        CHARACTER_STAMINA.CHARACTER_ID,
                        CHARACTER_STAMINA.STAMINA
                )
                .values(
                        character.getId().getValue(),
                        stamina
                )
                .onDuplicateKeyUpdate()
                .set(CHARACTER_STAMINA.STAMINA, stamina)
                .where(CHARACTER_STAMINA.CHARACTER_ID.eq(character.getId().getValue()))
                .execute();
    }

    public Optional<Integer> get(RPKCharacter character) {
        Record result = database.create()
                .select(CHARACTER_STAMINA.STAMINA)
                .from(CHARACTER_STAMINA)
                .where(CHARACTER_STAMINA.CHARACTER_ID.eq(character.getId().getValue()))
                .fetchOne();
        if (result == null) return Optional.empty();
        return Optional.of(result.get(CHARACTER_STAMINA.STAMINA));
    }

    public void restoreStamina() {
        database.create()
                .update(CHARACTER_STAMINA)
                .set(CHARACTER_STAMINA.STAMINA, value(100).minus(
                        greatest(
                                CHARACTER_STAMINA.STAMINA
                                        .minus(
                                                value(11)
                                                        .minus(
                                                                CHARACTER_STAMINA.STAMINA
                                                                        .multiply(CHARACTER_STAMINA.STAMINA)
                                                                        .divide(value(1000))
                                                        )
                                        ),
                                value(0)
                        )
                ))
                .where(CHARACTER_STAMINA.STAMINA.lt(StaminaService.MAX_STAMINA))
                .execute();

    }

}
