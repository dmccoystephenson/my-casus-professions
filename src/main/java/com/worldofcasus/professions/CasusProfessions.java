package com.worldofcasus.professions;

import com.worldofcasus.professions.command.node.NodeCommand;
import com.worldofcasus.professions.command.profession.ProfessionCommand;
import com.worldofcasus.professions.database.Database;
import com.worldofcasus.professions.listener.PlayerInteractListener;
import com.worldofcasus.professions.node.NodeService;
import com.worldofcasus.professions.profession.ProfessionService;
import com.worldofcasus.professions.stamina.StaminaRestoreRunnable;
import com.worldofcasus.professions.stamina.StaminaService;
import com.rpkit.core.bukkit.plugin.RPKBukkitPlugin;
import com.rpkit.core.service.Services;

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
        Services.INSTANCE.set(StaminaService.class, new StaminaService(this));
        Services.INSTANCE.set(NodeService.class, new NodeService(this));
        Services.INSTANCE.set(ProfessionService.class, new ProfessionService(this));
        new StaminaRestoreRunnable(this).runTaskTimer(this, 36000L, 72000L);
        registerCommands();
        registerListeners();
    }

    private void registerCommands() {
        getCommand("node").setExecutor(new NodeCommand(this));
        getCommand("profession").setExecutor(new ProfessionCommand(this));
    }

    private void registerListeners() {
        registerListeners(new PlayerInteractListener(this));
    }

    public Database getDatabase() {
        return database;
    }

}
