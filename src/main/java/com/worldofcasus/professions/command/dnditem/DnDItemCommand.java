package com.worldofcasus.professions.command.dnditem;

import com.worldofcasus.professions.CasusProfessions;
import com.worldofcasus.professions.item.gui.DnDItemGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static org.bukkit.ChatColor.RED;

public class DnDItemCommand implements CommandExecutor {

    private static final String NO_PERMISSION = RED + "You do not have permission to create D&D items.";
    private static final String NOT_FROM_CONSOLE = RED + "You may not use this command from console.";

    private final CasusProfessions plugin;

    public DnDItemCommand(CasusProfessions plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(NOT_FROM_CONSOLE);
            return true;
        }
        if (!sender.hasPermission("worldofcasus.professions.command.dnditem")) {
            sender.sendMessage(NO_PERMISSION);
            return true;
        }
        DnDItemGUI gui = new DnDItemGUI(plugin);
        gui.initializeItems((Player) sender);
        gui.openInventory((Player) sender);
        return true;
    }
}
