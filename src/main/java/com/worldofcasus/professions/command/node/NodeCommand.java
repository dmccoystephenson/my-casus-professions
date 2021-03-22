package com.worldofcasus.professions.command.node;

import com.worldofcasus.professions.CasusProfessions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static org.bukkit.ChatColor.RED;

public final class NodeCommand implements CommandExecutor {

    private static final String USAGE_MESSAGE = RED + "Usage: /node [create|delete|view|additem|removeitem|list|reload]";

    private final NodeCreateCommand nodeCreateCommand;
    private final NodeDeleteCommand nodeDeleteCommand;
    private final NodeViewCommand nodeViewCommand;
    private final NodeAddItemCommand nodeAddItemCommand;
    private final NodeRemoveItemCommand nodeRemoveItemCommand;
    private final NodeListCommand nodeListCommand;
    private final NodeReloadCommand nodeReloadCommand;

    public NodeCommand(CasusProfessions plugin) {
        nodeCreateCommand = new NodeCreateCommand(plugin);
        nodeDeleteCommand = new NodeDeleteCommand(plugin);
        nodeViewCommand = new NodeViewCommand(plugin);
        nodeAddItemCommand = new NodeAddItemCommand(plugin);
        nodeRemoveItemCommand = new NodeRemoveItemCommand(plugin);
        nodeListCommand = new NodeListCommand(plugin);
        nodeReloadCommand = new NodeReloadCommand(plugin);
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(USAGE_MESSAGE);
            return true;
        }
        String[] newArgs = Arrays.stream(args).skip(1).toArray(String[]::new);
        switch (args[0]) {
            case "create": return nodeCreateCommand.onCommand(sender, command, label, newArgs);
            case "delete": return nodeDeleteCommand.onCommand(sender, command, label, newArgs);
            case "view": return nodeViewCommand.onCommand(sender, command, label, newArgs);
            case "additem": return nodeAddItemCommand.onCommand(sender, command, label, newArgs);
            case "removeitem": return nodeRemoveItemCommand.onCommand(sender, command, label, newArgs);
            case "list": return nodeListCommand.onCommand(sender, command, label, newArgs);
            case "reload": return nodeReloadCommand.onCommand(sender, command, label, newArgs);
            default:
                sender.sendMessage(USAGE_MESSAGE);
                return true;
        }
    }
}
