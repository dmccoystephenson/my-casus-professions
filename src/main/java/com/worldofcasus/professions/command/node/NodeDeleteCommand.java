package com.worldofcasus.professions.command.node;

import com.rpkit.core.exception.UnregisteredServiceException;
import com.worldofcasus.professions.CasusProfessions;
import com.worldofcasus.professions.node.Node;
import com.worldofcasus.professions.node.NodeId;
import com.worldofcasus.professions.node.NodeService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
        if (!sender.hasPermission("worldofcasus.professions.command.node.delete")) {
            sender.sendMessage(NO_PERMISSION);
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(USAGE_MESSAGE);
            return true;
        }
        String name = args[0];
        NodeService nodeService;
        try {
            nodeService = plugin.core.getServiceManager().getServiceProvider(NodeService.class);
        } catch (UnregisteredServiceException e) {
            sender.sendMessage(NODE_SERVICE_NOT_REGISTERED_ERROR);
            return true;
        }
        CompletableFuture<Optional<Node>> nodeFuture;
        try {
            int nodeId = Integer.parseInt(args[0]);
            nodeFuture = nodeService.getNode(new NodeId(nodeId));
        } catch (NumberFormatException exception) {
            String nodeName = args[0];
            nodeFuture = nodeService.getNode(nodeName);
        }
        nodeFuture.thenAccept((node) ->
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (node.isPresent()) {
                    Node value = node.get();
                    nodeService.deleteNode(value).thenRun(() -> {
                        sender.sendMessage(nodeDeleted(value));
                    });
                } else {
                    sender.sendMessage(invalidNode(name));
                }
            })
        );

        return true;
    }

    private String nodeDeleted(Node node) {
        return GREEN + "Node " + node.getName() + " deleted.";
    }

    private String invalidNode(String nodeName) {
        return RED + "No node by the name " + nodeName + " exists.";
    }
}
