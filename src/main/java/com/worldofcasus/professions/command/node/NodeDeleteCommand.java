package com.worldofcasus.professions.command.node;

import com.worldofcasus.professions.CasusProfessions;
import com.worldofcasus.professions.node.Node;
import com.worldofcasus.professions.node.NodeId;
import com.worldofcasus.professions.node.NodeService;
import com.rpkit.core.service.Services;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

public final class NodeDeleteCommand implements CommandExecutor {

    private static final String USAGE_MESSAGE = RED + "Usage: /node delete [node]";
    private static final String NODE_SERVICE_NOT_REGISTERED_ERROR = RED + "No node service registered.";
    private static final String NO_PERMISSION = RED + "You do not have permission to delete nodes.";

    private final CasusProfessions plugin;

    public NodeDeleteCommand(CasusProfessions plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("professions.command.node.delete")) {
            sender.sendMessage(NO_PERMISSION);
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(USAGE_MESSAGE);
            return true;
        }
        String name = args[0];
        NodeService nodeService = Services.INSTANCE.get(NodeService.class);
        if (nodeService == null) {
            sender.sendMessage(NODE_SERVICE_NOT_REGISTERED_ERROR);
            return true;
        }
        Optional<Node> node;
        try {
            int nodeId = Integer.parseInt(args[0]);
            node = nodeService.getNode(new NodeId(nodeId));
        } catch (NumberFormatException exception) {
            String nodeName = args[0];
            node = nodeService.getNode(nodeName);
        }
        if (node.isPresent()) {
            Node value = node.get();
            nodeService.deleteNode(value);
            sender.sendMessage(nodeDeleted(value));
        } else {
            sender.sendMessage(invalidNode(name));
        }
        return true;
    }

    private String nodeDeleted(Node node) {
        return GREEN + "Node " + node.getName() + " deleted.";
    }

    private String invalidNode(String nodeName) {
        return RED + "No node by the name " + nodeName + "exists.";
    }
}
