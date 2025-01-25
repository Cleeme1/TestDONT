package fr.oneblock.phase;

public enum PhaseType {
    PLAINS("Plaines", "§a"),
    DESERT("Désert", "§e"),
    NETHER("Nether", "§c"),
    END("End", "§5"),
    MISC("Divers", "§7");

    private final String displayName;
    private final String color;

    PhaseType(String displayName, String color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColor() {
        return color;
    }
}