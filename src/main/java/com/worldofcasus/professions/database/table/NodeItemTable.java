package com.worldofcasus.professions.database.table;

import com.worldofcasus.professions.CasusProfessions;
import com.worldofcasus.professions.database.Database;
import com.worldofcasus.professions.node.NodeId;
import com.worldofcasus.professions.node.NodeItem;
import com.worldofcasus.professions.node.NodeItemId;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jooq.Record;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.worldofcasus.professions.database.jooq.casus.Tables.NODE;
import static com.worldofcasus.professions.database.jooq.casus.Tables.NODE_ITEM;
import static java.util.logging.Level.SEVERE;
import static org.jooq.impl.DSL.constraint;

public final class NodeItemTable implements Table {

    private final CasusProfessions plugin;
    private final Database database;

    public NodeItemTable(CasusProfessions plugin, Database database) {
        this.plugin = plugin;
        this.database = database;
    }

    @Override
    public void create() {
        database.create()
                .createTableIfNotExists(NODE_ITEM)
                .columns(
                        NODE_ITEM.ID,
                        NODE_ITEM.NODE_ID,
                        NODE_ITEM.ITEM,
                        NODE_ITEM.CHANCE
                )
                .constraints(
                        constraint("pk_node_item").primaryKey(NODE_ITEM.ID),
                        constraint("fk_node_item_node_id").foreignKey(NODE_ITEM.NODE_ID).references(NODE, NODE.ID)
                )
                .execute();
    }

    public List<NodeItem> get(NodeId nodeId) {
        return database.create()
                .select(
                        NODE_ITEM.ID,
                        NODE_ITEM.ITEM,
                        NODE_ITEM.CHANCE
                )
                .from(NODE_ITEM)
                .where(NODE_ITEM.NODE_ID.eq(nodeId.getValue()))
                .fetch()
                .map(result -> new NodeItem(
                        new NodeItemId(result.get(NODE_ITEM.ID)),
                        itemStackFromByteArray(result.get(NODE_ITEM.ITEM)),
                        result.get(NODE_ITEM.CHANCE)
                ));
    }

    public Optional<NodeItem> get(NodeItemId nodeItemId) {
        Record result = database.create()
                .select(
                        NODE_ITEM.ID,
                        NODE_ITEM.ITEM,
                        NODE_ITEM.CHANCE
                )
                .from(NODE_ITEM)
                .where(NODE_ITEM.ID.eq(nodeItemId.getValue()))
                .fetchOne();
        if (result == null) return Optional.empty();
        return Optional.of(new NodeItem(
                new NodeItemId(result.get(NODE_ITEM.ID)),
                itemStackFromByteArray(result.get(NODE_ITEM.ITEM)),
                result.get(NODE_ITEM.CHANCE)
        ));
    }

    public void insert(NodeId nodeId, NodeItem item) {
        database.create()
                .insertInto(
                        NODE_ITEM,
                        NODE_ITEM.NODE_ID,
                        NODE_ITEM.ITEM,
                        NODE_ITEM.CHANCE
                )
                .values(
                        nodeId.getValue(),
                        byteArrayFromItemStack(item.getItem()),
                        item.getChance()
                )
                .execute();
    }

    public void delete(NodeItem item) {
        database.create()
                .deleteFrom(NODE_ITEM)
                .where(NODE_ITEM.ID.eq(item.getId().getValue()))
                .execute();
    }

    private ItemStack itemStackFromByteArray(byte[] bytes) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes); BukkitObjectInputStream bois = new BukkitObjectInputStream(bais)) {
            return (ItemStack) bois.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            plugin.getLogger().log(SEVERE, "Failed to load ItemStack", exception);
        }
        return null;
    }

    private byte[] byteArrayFromItemStack(ItemStack item) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); BukkitObjectOutputStream boos = new BukkitObjectOutputStream(baos)) {
            boos.writeObject(item);
            return baos.toByteArray();
        } catch (IOException exception) {
            plugin.getLogger().log(SEVERE, "Failed to save ItemStack", exception);
        }
        return new byte[0];
    }

}
