package com.worldofcasus.professions.profession;

public final class Profession {

    private final ProfessionId id;
    private final String name;

    public Profession(ProfessionId id, String name) {
        this.id = id;
        this.name = name;
    }

    public Profession(String name) {
        this(null, name);
    }

    public ProfessionId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
