package com.worldofcasus.professions.profession;

import java.util.Objects;

public final class ProfessionId {

    private final int value;

    public ProfessionId(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfessionId that = (ProfessionId) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

}
