package com.worldofcasus.professions.database.table;

import com.worldofcasus.professions.CasusProfessions;
import com.worldofcasus.professions.database.Database;
import com.worldofcasus.professions.node.Node;
import com.worldofcasus.professions.node.NodeId;
import com.worldofcasus.professions.profession.Profession;
import com.worldofcasus.professions.profession.ProfessionId;
import org.bukkit.Location;
import org.bukkit.World;
import org.jooq.Record;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.worldofcasus.professions.database.jooq.casus.Tables.NODE;
import static org.jooq.impl.DSL.constraint;

public final class NodeTable implements Table{

    private final CasusProfessions plugin;
    private final Database database;

    public NodeTable(CasusProfessions plugin, Database database) {
        this.plugin = plugin;
        this.database = database;
    }

    @Override
    public void create() {
        database.create()
                .createTableIfNotExists(NODE)
                .columns(
                        NODE.ID,
                        NODE.NAME,
                        NODE.WORLD,
                        NODE.MIN_X,
                        NODE.MIN_Y,
                        NODE.MIN_Z,
                        NODE.MAX_X,
                        NODE.MAX_Y,
                        NODE.MAX_Z,
                        NODE.REQUIRED_PROFESSION_ID
                )
                .constraints(
                        constraint("pk_node").primaryKey(NODE.ID),
                        constraint("uk_node_name").unique(NODE.NAME)
                )
                .execute();
    }

    public List<Node> getAll() {
        return database.create()
                .select(
                        NODE.ID,
                        NODE.NAME,
                        NODE.WORLD,
                        NODE.MIN_X,
                        NODE.MIN_Y,
                        NODE.MIN_Z,
                        NODE.MAX_X,
                        NODE.MAX_Y,
                        NODE.MAX_Z,
                        NODE.REQUIRED_PROFESSION_ID
                )
                .from(NODE)
                .fetch()
                .stream()
                .map(this::toNode)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Node> getAt(Location location) {
        return database.create()
                .select(
                        NODE.ID,
                        NODE.NAME,
                        NODE.WORLD,
                        NODE.MIN_X,
                        NODE.MIN_Y,
                        NODE.MIN_Z,
                        NODE.MAX_X,
                        NODE.MAX_Y,
                        NODE.MAX_Z,
                        NODE.REQUIRED_PROFESSION_ID
                )
                .from(NODE)
                .where(NODE.WORLD.eq(location.getWorld().getName()))
                .and(NODE.MIN_X.le(location.getBlockX()))
                .and(NODE.MIN_Y.le(location.getBlockY()))
                .and(NODE.MIN_Z.le(location.getBlockZ()))
                .and(NODE.MAX_X.ge(location.getBlockX()))
                .and(NODE.MAX_Y.ge(location.getBlockY()))
                .and(NODE.MAX_Z.ge(location.getBlockZ()))
                .fetch()
                .stream()
                .map(this::toNode)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Optional<Node> get(NodeId id) {
        Record result = database.create()
                .select(
                        NODE.ID,
                        NODE.NAME,
                        NODE.WORLD,
                        NODE.MIN_X,
                        NODE.MIN_X,
                        NODE.MIN_Y,
                        NODE.MIN_Z,
                        NODE.MAX_X,
                        NODE.MAX_Y,
                        NODE.MAX_Z,
                        NODE.REQUIRED_PROFESSION_ID
                )
                .from(NODE)
                .where(NODE.ID.eq(id.getValue()))
                .fetchOne();
        if (result == null) return Optional.empty();
        return Optional.of(toNode(result));
    }

    public Optional<Node> get(String name) {
        Record result = database.create()
                .select(
                        NODE.ID,
                        NODE.NAME,
                        NODE.WORLD,
                        NODE.MIN_X,
                        NODE.MIN_Y,
                        NODE.MIN_Z,
                        NODE.MAX_X,
                        NODE.MAX_Y,
                        NODE.MAX_Z,
                        NODE.REQUIRED_PROFESSION_ID
                )
                .from(NODE)
                .where(NODE.NAME.eq(name))
                .fetchOne();
        if (result == null) return Optional.empty();
        return Optional.of(toNode(result));
    }

    private Node toNode(Record result) {
        NodeId id = new NodeId(result.get(NODE.ID));
        World world = plugin.getServer().getWorld(result.get(NODE.WORLD));
        ProfessionId requiredProfessionId = new ProfessionId(result.get(NODE.REQUIRED_PROFESSION_ID));
        Optional<Profession> profession = database.getTable(ProfessionTable.class).get(requiredProfessionId);
        return profession.map(value -> new Node(
                id,
                result.get(NODE.NAME),
                new Location(
                        world,
                        result.get(NODE.MIN_X),
                        result.get(NODE.MIN_Y),
                        result.get(NODE.MIN_Z)
                ),
                new Location(
                        world,
                        result.get(NODE.MAX_X),
                        result.get(NODE.MAX_Y),
                        result.get(NODE.MAX_Z)
                ),
                value,
                database.getTable(NodeItemTable.class).get(id)
        )).orElse(null);
    }

    public void insert(Node node) {
        database.create()
                .insertInto(
                        NODE,
                        NODE.NAME,
                        NODE.WORLD,
                        NODE.MIN_X,
                        NODE.MIN_Y,
                        NODE.MIN_Z,
                        NODE.MAX_X,
                        NODE.MAX_Y,
                        NODE.MAX_Z,
                        NODE.REQUIRED_PROFESSION_ID
                )
                .values(
                        node.getName(),
                        node.getMinLocation().getWorld().getName(),
                        node.getMinLocation().getBlockX(),
                        node.getMinLocation().getBlockY(),
                        node.getMinLocation().getBlockZ(),
                        node.getMaxLocation().getBlockX(),
                        node.getMaxLocation().getBlockY(),
                        node.getMaxLocation().getBlockZ(),
                        node.getRequiredProfession().getId().getValue()
                )
                .execute();
    }

    public void delete(Node node) {
        database.create()
                .deleteFrom(NODE)
                .where(NODE.ID.eq(node.getId().getValue()))
                .execute();
    }
}
