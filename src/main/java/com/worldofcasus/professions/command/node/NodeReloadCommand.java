package com.worldofcasus.professions.command.node;

import com.worldofcasus.professions.CasusProfessions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static org.bukkit.ChatColor.RED;

public class NodeReloadCommand implements CommandExecutor {

    private static final String NO_PERMISSION = RED + "You do not have permission to reload the node harvesting config.";

    private final CasusProfessions plugin;

    public NodeReloadCommand(CasusProfessions plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("worldofcasus.professions.command.reload")) {
            sender.sendMessage(NO_PERMISSION);
            return true;
        }
        plugin.reloadConfig();
        return true;
    }
}
