package com.worldofcasus.professions.node;

import com.rpkit.core.service.ServiceProvider;
import com.worldofcasus.professions.CasusProfessions;
import com.worldofcasus.professions.database.table.NodeItemTable;
import com.worldofcasus.professions.database.table.NodeTable;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class NodeService implements ServiceProvider {

    private final CasusProfessions plugin;
    private List<Node> nodes = new ArrayList<>();

    public NodeService(CasusProfessions plugin) {
        this.plugin = plugin;
        plugin.getDatabase().getTable(NodeTable.class).getAll().thenAccept(
                (nodes) -> {
                    synchronized (this) {
                        this.nodes = nodes;
                    }
                }
        );
    }

    public CasusProfessions getPlugin() {
        return plugin;
    }

    public CompletableFuture<Optional<Node>> addNode(Node node) {
        return plugin.getDatabase().getTable(NodeTable.class).insert(node)
                .thenApply((insertedNode) -> {
                    if (insertedNode.isPresent()) {
                        synchronized (this) {
                            nodes.add(insertedNode.get());
                        }
                    }
                    return insertedNode;
                });
    }

    public synchronized List<Node> getNodes() {
        return nodes;
    }

    public synchronized List<Node> getNodesAt(Location location) {
        return nodes.stream().filter((node) ->
                node.getMinLocation().getWorld().equals(location.getWorld())
                        && node.getMinLocation().getBlockX() <= location.getBlockX()
                        && node.getMinLocation().getBlockY() <= location.getBlockY()
                        && node.getMinLocation().getBlockZ() <= location.getBlockZ()
                        && node.getMaxLocation().getBlockX() >= location.getBlockX()
                        && node.getMaxLocation().getBlockY() >= location.getBlockY()
                        && node.getMaxLocation().getBlockZ() >= location.getBlockZ()
        ).collect(Collectors.toList());
    }

    public CompletableFuture<Optional<Node>> getNode(NodeId nodeId) {
        return plugin.getDatabase().getTable(NodeTable.class).get(nodeId);
    }

    public CompletableFuture<Optional<Node>> getNode(String name) {
        return plugin.getDatabase().getTable(NodeTable.class).get(name);
    }

    public CompletableFuture<Void> deleteNode(Node node) {
        return plugin.getDatabase().getTable(NodeTable.class).delete(node).thenRun(() -> {
            synchronized (this) {
                nodes.remove(node);
            }
        });
    }

    public CompletableFuture<Optional<NodeItem>> getNodeItem(NodeItemId nodeItemId) {
        return plugin.getDatabase().getTable(NodeItemTable.class).get(nodeItemId);
    }

    public CompletableFuture<Optional<NodeItem>> addNodeItem(Node node, NodeItem nodeItem) {
        return plugin.getDatabase().getTable(NodeItemTable.class).insert(node.getId(), nodeItem);
    }

    public CompletableFuture<Void> removeNodeItem(NodeItem nodeItem) {
        return plugin.getDatabase().getTable(NodeItemTable.class).delete(nodeItem);
    }
}
