package com.worldofcasus.professions.money;

public final class Currency {

    public static Currency PP = new Currency("pp");
    public static Currency GP = new Currency("gp");
    public static Currency EP = new Currency("ep");
    public static Currency SP = new Currency("sp");
    public static Currency CP = new Currency("cp");

    private final String name;

    private Currency(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Currency getByName(String name) {
        switch (name.toUpperCase()) {
            case "PP": return PP;
            case "GP": return GP;
            case "EP": return EP;
            case "SP": return SP;
            case "CP": return CP;
            default: return null;
        }
    }

}
