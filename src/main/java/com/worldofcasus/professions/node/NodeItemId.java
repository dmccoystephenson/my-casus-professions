package com.worldofcasus.professions.node;

import java.util.Objects;

public final class NodeItemId {

    private final int value;

    public NodeItemId(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeItemId that = (NodeItemId) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

}
