package fr.oneblock.config;

import fr.oneblock.OneBlockPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {
    private final OneBlockPlugin plugin;
    private FileConfiguration config;
    private File configFile;

    public Config(OneBlockPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }

        config = YamlConfiguration.loadConfiguration(configFile);
        addDefaults();
    }

    private void addDefaults() {
        // Configuration par défaut
        config.addDefault("world.name", "oneblock_world");
        config.addDefault("world.island-spacing", 1000);
        config.addDefault("world.build-limit", 256);

        // Configuration des phases
        config.addDefault("phases.start-phase", 1);
        config.addDefault("phases.blocks-per-phase", 128);

        // Configuration des bonus
        config.addDefault("bonuses.enabled", true);
        config.addDefault("bonuses.rare-block-chance", 0.1);
        config.addDefault("bonuses.mob-spawn-chance", 0.05);

        // Configuration des récompenses
        config.addDefault("rewards.phase-up.enabled", true);
        config.addDefault("rewards.phase-up.commands", new String[]{
                "give %player% diamond 1",
                "eco give %player% 1000"
        });

        // Sauvegarde des valeurs par défaut
        config.options().copyDefaults(true);
        save();
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder la configuration: " + e.getMessage());
        }
    }

    // Méthodes d'accès aux configurations
    public String getString(String path) {
        return config.getString(path);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public double getDouble(String path) {
        return config.getDouble(path);
    }

    public FileConfiguration getConfig() {
        return config;
    }
}