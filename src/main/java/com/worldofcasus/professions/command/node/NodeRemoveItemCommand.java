package com.worldofcasus.professions.command.node;

import com.rpkit.core.exception.UnregisteredServiceException;
import com.worldofcasus.professions.CasusProfessions;
import com.worldofcasus.professions.node.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

public final class NodeRemoveItemCommand implements CommandExecutor {

    private static final String USAGE_MESSAGE = RED + "Usage: /node additem [node]";
    private static final String MUST_BE_A_PLAYER = RED + "You must be a player to use this command.";
    private static final String NODE_SERVICE_NOT_REGISTERED_ERROR = RED + "No node service registered.";
    private static final String NODE_INVALID = RED + "Could not find a node by that ID or name.";
    private static final String ITEM_NOT_PRESENT = RED + "That item is not one of the drops for that node.";
    private static final String INVALID_NODE_ITEM_ID = RED + "Invalid node item ID.";
    private static final String NO_PERMISSION = RED + "You do not have permission to remove items from nodes.";

    private final CasusProfessions plugin;

    public NodeRemoveItemCommand(CasusProfessions plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("worldofcasus.professions.command.node.removeitem")) {
            sender.sendMessage(NO_PERMISSION);
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(USAGE_MESSAGE);
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(MUST_BE_A_PLAYER);
            return true;
        }
        Player player = (Player) sender;
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
                        CompletableFuture<Optional<NodeItem>> nodeItemToRemoveFuture;
                        if (args.length < 2) {
                            ItemStack item = player.getInventory().getItemInMainHand();
                            nodeItemToRemoveFuture = CompletableFuture.completedFuture(
                                    value.getItems().stream().filter(nodeItem -> nodeItem.getItem().isSimilar(item)).findFirst()
                            );
                        } else {
                            try {
                                int nodeItemId = Integer.parseInt(args[1]);
                                nodeItemToRemoveFuture = nodeService.getNodeItem(new NodeItemId(nodeItemId));
                            } catch (NumberFormatException exception) {
                                sender.sendMessage(INVALID_NODE_ITEM_ID);
                                return;
                            }
                        }
                        nodeItemToRemoveFuture.thenAccept((nodeItemToRemove) -> {
                            plugin.getServer().getScheduler().runTask(plugin, () -> {
                                if (nodeItemToRemove.isPresent()) {
                                    nodeService.removeNodeItem(nodeItemToRemove.get()).thenRun(() -> {
                                        sender.sendMessage(itemRemoved(nodeItemToRemove.get().getItem(), value));
                                    });
                                } else {
                                    sender.sendMessage(ITEM_NOT_PRESENT);
                                }
                            });
                        });
                    } else {
                        sender.sendMessage(NODE_INVALID);
                    }
                })
        );

        return true;
    }

    private String itemRemoved(ItemStack item, Node node) {
        return GREEN + "Item " + item.getType().toString().toLowerCase().replace('_', ' ') + " x " + item.getAmount() + " removed from drops for node " + node.getName() + ".";
    }
}
