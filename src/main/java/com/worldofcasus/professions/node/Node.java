package com.worldofcasus.professions.node;

import com.worldofcasus.professions.profession.Profession;
import org.bukkit.Location;

import java.util.Collections;
import java.util.List;

public final class Node {

    private final NodeId id;
    private final String name;
    private final Location minLocation;
    private final Location maxLocation;
    private final Profession requiredProfession;
    private final List<NodeItem> items;

    public Node(NodeId id,
                String name,
                Location minLocation,
                Location maxLocation,
                Profession requiredProfession,
                List<NodeItem> items) {
        this.id = id;
        this.name = name;
        this.minLocation = minLocation;
        this.maxLocation = maxLocation;
        this.requiredProfession = requiredProfession;
        this.items = items;
    }

    public Node(String name,
                Location minLocation,
                Location maxLocation,
                Profession requiredProfession,
                List<NodeItem> items) {
        this(null, name, minLocation, maxLocation, requiredProfession, items);
    }

    public NodeId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getMinLocation() {
        return minLocation;
    }

    public Location getMaxLocation() {
        return maxLocation;
    }

    public Profession getRequiredProfession() {
        return requiredProfession;
    }

    public List<NodeItem> getItems() {
        return Collections.unmodifiableList(items);
    }

}
