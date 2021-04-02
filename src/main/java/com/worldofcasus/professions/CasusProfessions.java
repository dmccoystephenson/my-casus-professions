package com.worldofcasus.professions;

import com.rpkit.core.service.ServiceProvider;
import com.worldofcasus.professions.command.node.NodeCommand;
import com.worldofcasus.professions.command.profession.ProfessionCommand;
import com.worldofcasus.professions.command.stamina.StaminaCommand;
import com.worldofcasus.professions.database.Database;
import com.worldofcasus.professions.listener.PlayerInteractListener;
import com.worldofcasus.professions.node.NodeService;
import com.worldofcasus.professions.profession.ProfessionService;
import com.worldofcasus.professions.stamina.StaminaRestoreRunnable;
import com.worldofcasus.professions.stamina.StaminaService;
import com.rpkit.core.bukkit.plugin.RPKBukkitPlugin;

public final class CasusProfessions extends RPKBukkitPlugin {

    private Database database;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        database = new Database(
                this,
                getConfig().getString("database.url"),
                getConfig().getString("database.username"),
                getConfig().getString("database.password")
        );
        setServiceProviders(new ServiceProvider[] {
                new StaminaService(this),
                new NodeService(this),
                new ProfessionService(this)
        });
        new StaminaRestoreRunnable(this).runTaskTimer(this, 36000L, 72000L);
    }

    @Override
    public void registerCommands() {
        getCommand("node").setExecutor(new NodeCommand(this));
        getCommand("profession").setExecutor(new ProfessionCommand(this));
        getCommand("stamina").setExecutor(new StaminaCommand(this));
    }

    @Override
    public void registerListeners() {
        registerListeners(new PlayerInteractListener(this));
    }

    public Database getDatabase() {
        return database;
    }

}
