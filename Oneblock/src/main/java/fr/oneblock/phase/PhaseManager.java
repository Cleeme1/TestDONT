package fr.oneblock.phase;

import fr.oneblock.OneBlockPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PhaseManager {

    private final OneBlockPlugin plugin;
    private final List<Phase> phases;
    private final Random random;

    public PhaseManager(OneBlockPlugin plugin) {
        this.plugin = plugin;
        this.phases = new ArrayList<>();
        this.random = new Random();
        loadPhases();
    }

    public void loadPhases() {
        ConfigurationSection config = plugin.getConfig().getConfigurationSection("phases");
        if (config == null) return;

        for (String key : config.getKeys(false)) {
            if (!key.startsWith("phase-")) continue;

            ConfigurationSection phaseConfig = config.getConfigurationSection(key);
            if (phaseConfig == null) continue;

            String name = phaseConfig.getString("name", "Phase Inconnue");
            Map<Material, Integer> blocks = new HashMap<>();

            ConfigurationSection blocksSection = phaseConfig.getConfigurationSection("blocks");
            if (blocksSection != null) {
                for (String block : blocksSection.getKeys(false)) {
                    Material material = Material.getMaterial(block);
                    if (material != null) {
                        blocks.put(material, blocksSection.getInt(block));
                    }
                }
            }

            Phase phase = new Phase(
                    Integer.parseInt(key.split("-")[1]),
                    name,
                    blocks,
                    phaseConfig.getStringList("rewards"),
                    phaseConfig.getInt("blocks-required", 128)
            );

            phases.add(phase);
        }
    }

    public Material getNextBlock(int phase) {
        Phase currentPhase = getPhase(phase);
        if (currentPhase == null) return Material.STONE;

        Map<Material, Integer> blocks = currentPhase.getBlocks();
        int totalWeight = blocks.values().stream().mapToInt(Integer::intValue).sum();
        int randomValue = random.nextInt(totalWeight);

        int currentWeight = 0;
        for (Map.Entry<Material, Integer> entry : blocks.entrySet()) {
            currentWeight += entry.getValue();
            if (randomValue < currentWeight) {
                return entry.getKey();
            }
        }

        return Material.STONE;
    }

    public Phase getPhase(int level) {
        return phases.stream()
                .filter(phase -> phase.getLevel() == level)
                .findFirst()
                .orElse(null);
    }

    public boolean isLastPhase(int level) {
        return level >= phases.size();
    }

    public List<Phase> getAllPhases() {
        return new ArrayList<>(phases);
    }
}