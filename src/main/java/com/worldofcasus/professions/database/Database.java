package com.worldofcasus.professions.database;

import com.worldofcasus.professions.CasusProfessions;
import com.worldofcasus.professions.database.table.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.jooq.DSLContext;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static org.jooq.SQLDialect.MYSQL;

public final class Database {

    private final DataSource dataSource;
    private final CacheManager cacheManager;
    private final Settings settings;
    private final Map<Class<? extends Table>, Table> tables;

    public Database(CasusProfessions plugin, String url, String username, String password) {

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        dataSource = new HikariDataSource(hikariConfig);

        cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);

        settings = new Settings().withRenderSchema(false);

        tables = new HashMap<>();
        addTable(new NodeTable(plugin, this));
        addTable(new NodeItemTable(plugin, this));
        addTable(new CharacterStaminaTable(this));
        addTable(new ProfessionTable(this));
        addTable(new CharacterProfessionTable(this));
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public DSLContext create() {
        return DSL.using(dataSource, MYSQL, settings);
    }

    public <T extends Table> void addTable(T table) {
        table.create();
        tables.put(table.getClass(), table);
    }

    public <T extends Table> T getTable(Class<T> type) {
        return (T) tables.get(type);
    }
}
