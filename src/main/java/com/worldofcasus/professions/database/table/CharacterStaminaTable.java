package com.worldofcasus.professions.database.table;

import com.rpkit.characters.bukkit.character.RPKCharacter;
import com.worldofcasus.professions.database.Database;
import com.worldofcasus.professions.stamina.StaminaService;
import org.jooq.Record;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.worldofcasus.professions.database.jooq.casus.Tables.CHARACTER_STAMINA;
import static org.jooq.impl.DSL.*;

public final class CharacterStaminaTable implements Table {

    private final Database database;

    public CharacterStaminaTable(Database database) {
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

    public CompletableFuture<Void> insertOrUpdate(RPKCharacter character, int stamina) {
        return CompletableFuture.runAsync(() ->
                database.create()
                        .insertInto(CHARACTER_STAMINA)
                        .set(CHARACTER_STAMINA.CHARACTER_ID, character.getId())
                        .set(CHARACTER_STAMINA.STAMINA, stamina)
                        .onDuplicateKeyUpdate()
                        .set(CHARACTER_STAMINA.STAMINA, stamina)
                        .where(CHARACTER_STAMINA.CHARACTER_ID.eq(character.getId()))
                        .execute()
        );

    }

    public CompletableFuture<Optional<Integer>> get(RPKCharacter character) {
        return CompletableFuture.supplyAsync(() -> {
            Record result = database.create()
                    .select(CHARACTER_STAMINA.STAMINA)
                    .from(CHARACTER_STAMINA)
                    .where(CHARACTER_STAMINA.CHARACTER_ID.eq(character.getId()))
                    .fetchOne();
            if (result == null) return Optional.empty();
            return Optional.of(result.get(CHARACTER_STAMINA.STAMINA));
        });

    }

    public CompletableFuture<Void> restoreStamina() {
        return CompletableFuture.runAsync(() ->
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
                        .execute()
        );
    }

}
