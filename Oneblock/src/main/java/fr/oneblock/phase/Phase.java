package fr.oneblock.phase;

import org.bukkit.Material;
import java.util.List;
import java.util.Map;

public class Phase {
    private final int level;
    private final String name;
    private final Map<Material, Integer> blocks;
    private final List<String> rewards;
    private final int blocksRequired;
    private final PhaseType type;

    public Phase(int level, String name, Map<Material, Integer> blocks, List<String> rewards, int blocksRequired) {
        this.level = level;
        this.name = name;
        this.blocks = blocks;
        this.rewards = rewards;
        this.blocksRequired = blocksRequired;
        this.type = determinePhaseType();
    }

    private PhaseType determinePhaseType() {
        // Logique pour déterminer le type de phase basé sur les blocs présents
        if (blocks.containsKey(Material.GRASS_BLOCK) || blocks.containsKey(Material.DIRT)) {
            return PhaseType.PLAINS;
        } else if (blocks.containsKey(Material.SAND) || blocks.containsKey(Material.SANDSTONE)) {
            return PhaseType.DESERT;
        } else if (blocks.containsKey(Material.NETHERRACK)) {
            return PhaseType.NETHER;
        } else if (blocks.containsKey(Material.END_STONE)) {
            return PhaseType.END;
        }
        return PhaseType.MISC;
    }

    // Getters
    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public Map<Material, Integer> getBlocks() {
        return blocks;
    }

    public List<String> getRewards() {
        return rewards;
    }

    public int getBlocksRequired() {
        return blocksRequired;
    }

    public PhaseType getType() {
        return type;
    }

    // Méthodes utilitaires
    public boolean containsBlock(Material material) {
        return blocks.containsKey(material);
    }

    public int getBlockWeight(Material material) {
        return blocks.getOrDefault(material, 0);
    }

    public boolean isCompleted(int blocksBroken) {
        return blocksBroken >= blocksRequired;
    }

    public double getProgress(int blocksBroken) {
        return Math.min(1.0, (double) blocksBroken / blocksRequired);
    }
}