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
import java.util.concurrent.CompletableFuture;
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

    public CompletableFuture<List<Node>> getAll() {
        return CompletableFuture.supplyAsync(() ->
                database.create()
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
                        .map((record) -> toDomain(record).join())
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );
    }

    public CompletableFuture<List<Node>> getAt(Location location) {
        return CompletableFuture.supplyAsync(() ->
                database.create()
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
                        .map((record) -> toDomain(record).join())
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );
    }

    public CompletableFuture<Optional<Node>> get(NodeId id) {
        return CompletableFuture.supplyAsync(() ->
                database.create()
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
                        .fetchOptional()
                        .map((record) -> toDomain(record).join())
        );
    }

    public CompletableFuture<Optional<Node>> get(String name) {
        return CompletableFuture.supplyAsync(() ->
                database.create()
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
                        .fetchOptional()
                        .map((record) -> toDomain(record).join()));
    }

    public CompletableFuture<Optional<Node>> insert(Node node) {
        return CompletableFuture.supplyAsync(() ->
                database.create()
                        .insertInto(NODE)
                        .set(NODE.NAME, node.getName())
                        .set(NODE.WORLD, node.getMinLocation().getWorld().getName())
                        .set(NODE.MIN_X, node.getMinLocation().getBlockX())
                        .set(NODE.MIN_Y, node.getMinLocation().getBlockY())
                        .set(NODE.MIN_Z, node.getMinLocation().getBlockZ())
                        .set(NODE.MAX_X, node.getMaxLocation().getBlockX())
                        .set(NODE.MAX_Y, node.getMaxLocation().getBlockY())
                        .set(NODE.MAX_Z, node.getMaxLocation().getBlockZ())
                        .set(NODE.REQUIRED_PROFESSION_ID, node.getRequiredProfession().getId().getValue())
                        .returning()
                        .fetchOptional()
                        .map((record) -> toDomain(record).join())
        );

    }

    public CompletableFuture<Void> delete(Node node) {
        return CompletableFuture.runAsync(() ->
                database.create()
                        .deleteFrom(NODE)
                        .where(NODE.ID.eq(node.getId().getValue()))
                        .execute()
        );
    }

    private CompletableFuture<Node> toDomain(Record result) {
        NodeId id = new NodeId(result.get(NODE.ID));
        World world = plugin.getServer().getWorld(result.get(NODE.WORLD));
        ProfessionId requiredProfessionId = new ProfessionId(result.get(NODE.REQUIRED_PROFESSION_ID));
        return CompletableFuture.supplyAsync(() -> {
            Optional<Profession> profession = database.getTable(ProfessionTable.class).get(requiredProfessionId).join();
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
                    database.getTable(NodeItemTable.class).get(id).join()
            )).orElse(null);
        });
    }
}
