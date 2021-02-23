package com.worldofcasus.professions.node;

import com.worldofcasus.professions.CasusProfessions;
import com.worldofcasus.professions.database.table.NodeItemTable;
import com.worldofcasus.professions.database.table.NodeTable;
import com.rpkit.core.service.Service;
import org.bukkit.Location;

import java.util.List;
import java.util.Optional;

public final class NodeService implements Service {

    private final CasusProfessions plugin;

    public NodeService(CasusProfessions plugin) {
        this.plugin = plugin;
    }

    @Override
    public CasusProfessions getPlugin() {
        return plugin;
    }

    public void addNode(Node node) {
        plugin.getDatabase().getTable(NodeTable.class).insert(node);
    }

    public List<Node> getNodes() {
        return plugin.getDatabase().getTable(NodeTable.class).getAll();
    }

    public List<Node> getNodesAt(Location location) {
        return plugin.getDatabase().getTable(NodeTable.class).getAt(location);
    }

    public Optional<Node> getNode(NodeId nodeId) {
        return plugin.getDatabase().getTable(NodeTable.class).get(nodeId);
    }

    public Optional<Node> getNode(String name) {
        return plugin.getDatabase().getTable(NodeTable.class).get(name);
    }

    public void deleteNode(Node node) {
        plugin.getDatabase().getTable(NodeTable.class).delete(node);
    }

    public Optional<NodeItem> getNodeItem(NodeItemId nodeItemId) {
        return plugin.getDatabase().getTable(NodeItemTable.class).get(nodeItemId);
    }

    public void addNodeItem(Node node, NodeItem nodeItem) {
        plugin.getDatabase().getTable(NodeItemTable.class).insert(node.getId(), nodeItem);
    }

    public void removeNodeItem(NodeItem nodeItem) {
        plugin.getDatabase().getTable(NodeItemTable.class).delete(nodeItem);
    }
}
