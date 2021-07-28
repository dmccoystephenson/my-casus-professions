package com.worldofcasus.professions.command.node;

import com.rpkit.core.service.Services;
import com.worldofcasus.professions.node.NodeService;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.md_5.bungee.api.ChatColor.RED;
import static net.md_5.bungee.api.ChatColor.WHITE;
import static net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND;
import static net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT;

public final class NodeListCommand implements CommandExecutor {

    private static final String NO_PERMISSION = RED + "You do not have permission to view the node list.";
    private static final String NODE_SERVICE_NOT_REGISTERED_ERROR = RED + "No node service registered.";
    private static final String NODE_LIST_TITLE = WHITE + "Nodes:";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("worldofcasus.professions.command.node.list")) {
            sender.sendMessage(NO_PERMISSION);
            return true;
        }
        NodeService nodeService = Services.INSTANCE.get(NodeService.class);
        if (nodeService == null) {
            sender.sendMessage(NODE_SERVICE_NOT_REGISTERED_ERROR);
            return true;
        }
        sender.sendMessage(NODE_LIST_TITLE);
        nodeService.getNodes().forEach(node -> {
            TextComponent nodeButton = new TextComponent(node.getName());
            nodeButton.setHoverEvent(
                    new HoverEvent(
                            SHOW_TEXT,
                            new ComponentBuilder()
                                    .append("Show node " + node.getName())
                                    .create()
                    )
            );
            nodeButton.setClickEvent(
                    new ClickEvent(
                            RUN_COMMAND,
                            "/node view " + node.getId().getValue()
                    )
            );
            nodeButton.setColor(WHITE);
            TextComponent nodeDeleteButton = new TextComponent("(x)");
            nodeDeleteButton.setHoverEvent(
                    new HoverEvent(
                            SHOW_TEXT,
                            new ComponentBuilder()
                                    .append("Delete node " + node.getName())
                                    .create()
                    )
            );
            nodeDeleteButton.setClickEvent(
                    new ClickEvent(
                            RUN_COMMAND,
                            "/node delete " + node.getId().getValue()
                    )
            );
            nodeDeleteButton.setColor(RED);
            sender.spigot().sendMessage(new ComponentBuilder()
                    .append(nodeButton)
                    .append(" ")
                    .append(nodeDeleteButton)
                    .create());
        });
        return true;
    }
}
