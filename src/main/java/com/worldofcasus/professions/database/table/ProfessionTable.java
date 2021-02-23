package com.worldofcasus.professions.database.table;

import com.worldofcasus.professions.CasusProfessions;
import com.worldofcasus.professions.database.Database;
import com.worldofcasus.professions.profession.Profession;
import com.worldofcasus.professions.profession.ProfessionId;
import org.jooq.Record;

import java.util.List;
import java.util.Optional;

import static com.worldofcasus.professions.database.jooq.casus.Tables.PROFESSION;
import static org.jooq.impl.DSL.constraint;

public final class ProfessionTable implements Table {

    private final CasusProfessions plugin;
    private final Database database;

    public ProfessionTable(CasusProfessions plugin, Database database) {
        this.plugin = plugin;
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

    public Optional<Profession> get(ProfessionId id) {
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
    }

    public Optional<Profession> get(String name) {
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
    }

    public List<Profession> getAll() {
        return database.create()
                .select(
                        PROFESSION.ID,
                        PROFESSION.NAME
                )
                .from(PROFESSION)
                .fetch()
                .map(result -> new Profession(
                        new ProfessionId(result.get(PROFESSION.ID)),
                        result.get(PROFESSION.NAME)
                ));
    }

    public void insert(Profession profession) {
        database.create()
                .insertInto(
                        PROFESSION,
                        PROFESSION.NAME
                )
                .values(profession.getName())
                .execute();
    }
}
