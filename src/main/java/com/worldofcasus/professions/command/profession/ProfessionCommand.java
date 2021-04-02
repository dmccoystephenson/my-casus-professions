package com.worldofcasus.professions.command.profession;

import com.worldofcasus.professions.CasusProfessions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static org.bukkit.ChatColor.RED;

public final class ProfessionCommand implements CommandExecutor {

    private static final String USAGE_MESSAGE = RED + "Usage: /profession [create|set|list]";

    private final ProfessionCreateCommand professionCreateCommand;
    private final ProfessionSetCommand professionSetCommand;
    private final ProfessionListCommand professionListCommand;
    private final ProfessionShowCommand professionShowCommand;
    private final ProfessionResetCommand professionResetCommand;

    public ProfessionCommand(CasusProfessions plugin) {
        professionCreateCommand = new ProfessionCreateCommand(plugin);
        professionSetCommand = new ProfessionSetCommand(plugin);
        professionListCommand = new ProfessionListCommand(plugin);
        professionShowCommand = new ProfessionShowCommand(plugin);
        professionResetCommand = new ProfessionResetCommand(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(USAGE_MESSAGE);
            return true;
        }
        String[] newArgs = Arrays.stream(args).skip(1).toArray(String[]::new);
        switch (args[0]) {
            case "create": return professionCreateCommand.onCommand(sender, command, label, newArgs);
            case "set": return professionSetCommand.onCommand(sender, command, label, newArgs);
            case "list": return professionListCommand.onCommand(sender, command, label, newArgs);
            case "show": return professionShowCommand.onCommand(sender, command, label, newArgs);
            case "reset": return professionResetCommand.onCommand(sender, command, label, newArgs);
            default:
                sender.sendMessage(USAGE_MESSAGE);
                return true;
        }
    }
}
