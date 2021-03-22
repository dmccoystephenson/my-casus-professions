package com.worldofcasus.professions.database.table;

import com.worldofcasus.professions.database.Database;
import com.worldofcasus.professions.profession.Profession;
import com.worldofcasus.professions.profession.ProfessionId;
import org.jooq.Record;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.worldofcasus.professions.database.jooq.casus.Tables.PROFESSION;
import static org.jooq.impl.DSL.constraint;

public final class ProfessionTable implements Table {

    private final Database database;

    public ProfessionTable(Database database) {
        this.database = database;
    }

    @Override
    public void create() {
        database.create()
                .createTableIfNotExists(PROFESSION)
                .columns(
                        PROFESSION.ID,
                        PROFESSION.NAME
                )
                .constraints(
                        constraint("pk_profession").primaryKey(PROFESSION.ID),
                        constraint("uk_profession_name").unique(PROFESSION.NAME)
                )
                .execute();
    }

    public CompletableFuture<Optional<Profession>> get(ProfessionId id) {
        return CompletableFuture.supplyAsync(() -> {
            Record result = database.create()
                    .select(PROFESSION.NAME)
                    .from(PROFESSION)
                    .where(PROFESSION.ID.eq(id.getValue()))
                    .fetchOne();
            if (result == null) return Optional.empty();
            return Optional.of(new Profession(
                    id,
                    result.get(PROFESSION.NAME)
            ));
        });
    }

    public CompletableFuture<Optional<Profession>> get(String name) {
        return CompletableFuture.supplyAsync(() -> {
            Record result = database.create()
                    .select(PROFESSION.ID)
                    .from(PROFESSION)
                    .where(PROFESSION.NAME.eq(name))
                    .fetchOne();
            if (result == null) return Optional.empty();
            return Optional.of(new Profession(
                    new ProfessionId(result.get(PROFESSION.ID)),
                    name
            ));
        });
    }

    public CompletableFuture<List<Profession>> getAll() {
        return CompletableFuture.supplyAsync(() ->
                database.create()
                        .select(
                                PROFESSION.ID,
                                PROFESSION.NAME
                        )
                        .from(PROFESSION)
                        .fetch()
                        .map(result -> new Profession(
                                new ProfessionId(result.get(PROFESSION.ID)),
                                result.get(PROFESSION.NAME)
                        ))
        );
    }

    public CompletableFuture<Optional<Profession>> insert(Profession profession) {
        return CompletableFuture.supplyAsync(() ->
                database.create()
                        .insertInto(
                                PROFESSION
                        )
                        .set(PROFESSION.NAME, profession.getName())
                        .returning()
                        .fetchOptional()
                        .map(this::toDomain)
        );
    }

    private Profession toDomain(Record record) {
        if (record == null) return null;
        return new Profession(
                new ProfessionId(record.get(PROFESSION.ID)),
                record.get(PROFESSION.NAME)
        );
    }
}
