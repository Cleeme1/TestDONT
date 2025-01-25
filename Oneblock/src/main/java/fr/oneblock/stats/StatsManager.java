package fr.oneblock.stats;

import fr.oneblock.OneBlockPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class StatsManager {

    private final OneBlockPlugin plugin;
    private final Map<UUID, PlayerStats> playerStats;
    private final File statsFile;
    private final YamlConfiguration statsConfig;

    public StatsManager(OneBlockPlugin plugin) {
        this.plugin = plugin;
        this.playerStats = new HashMap<>();
        this.statsFile = new File(plugin.getDataFolder(), "stats.yml");
        this.statsConfig = YamlConfiguration.loadConfiguration(statsFile);
    }

    public void loadStats() {
        ConfigurationSection playersSection = statsConfig.getConfigurationSection("players");
        if (playersSection == null) return;

        for (String uuidStr : playersSection.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                ConfigurationSection playerSection = playersSection.getConfigurationSection(uuidStr);
                if (playerSection == null) continue;

                PlayerStats stats = new PlayerStats(
                        playerSection.getInt("blocks-broken", 0),
                        playerSection.getInt("mobs-killed", 0),
                        playerSection.getLong("playtime", 0),
                        playerSection.getInt("highest-phase", 1),
                        playerSection.getInt("challenges-completed", 0),
                        playerSection.getLong("last-login", 0)
                );

                playerStats.put(uuid, stats);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("UUID invalide dans le fichier de stats : " + uuidStr);
            }
        }
    }

    public void saveStats() {
        for (Map.Entry<UUID, PlayerStats> entry : playerStats.entrySet()) {
            String path = "players." + entry.getKey();
            PlayerStats stats = entry.getValue();

            statsConfig.set(path + ".blocks-broken", stats.getBlocksBroken());
            statsConfig.set(path + ".mobs-killed", stats.getMobsKilled());
            statsConfig.set(path + ".playtime", stats.getPlaytime());
            statsConfig.set(path + ".highest-phase", stats.getHighestPhase());
            statsConfig.set(path + ".challenges-completed", stats.getChallengesCompleted());
            statsConfig.set(path + ".last-login", stats.getLastLogin());
        }

        try {
            statsConfig.save(statsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Erreur lors de la sauvegarde des statistiques!");
            e.printStackTrace();
        }
    }

    public PlayerStats getStats(UUID uuid) {
        return playerStats.computeIfAbsent(uuid, k -> new PlayerStats());
    }

    public List<Map.Entry<UUID, PlayerStats>> getTopPlayers(StatType type, int limit) {
        return playerStats.entrySet().stream()
                .sorted((e1, e2) -> {
                    long val1 = getStatValue(e1.getValue(), type);
                    long val2 = getStatValue(e2.getValue(), type);
                    return Long.compare(val2, val1);
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    private long getStatValue(PlayerStats stats, StatType type) {
        return switch (type) {
            case BLOCKS_BROKEN -> stats.getBlocksBroken();
            case MOBS_KILLED -> stats.getMobsKilled();
            case PLAYTIME -> stats.getPlaytime();
            case HIGHEST_PHASE -> stats.getHighestPhase();
            case CHALLENGES_COMPLETED -> stats.getChallengesCompleted();
        };
    }

    public void incrementStat(UUID uuid, StatType type) {
        PlayerStats stats = getStats(uuid);
        switch (type) {
            case BLOCKS_BROKEN -> stats.incrementBlocksBroken();
            case MOBS_KILLED -> stats.incrementMobsKilled();
            case CHALLENGES_COMPLETED -> stats.incrementChallengesCompleted();
        }
    }

    public void updateHighestPhase(UUID uuid, int phase) {
        PlayerStats stats = getStats(uuid);
        if (phase > stats.getHighestPhase()) {
            stats.setHighestPhase(phase);
        }
    }

    public void updatePlaytime(UUID uuid) {
        PlayerStats stats = getStats(uuid);
        long lastLogin = stats.getLastLogin();
        if (lastLogin > 0) {
            long currentTime = System.currentTimeMillis();
            long sessionTime = (currentTime - lastLogin) / 1000; // Conversion en secondes
            stats.addPlaytime(sessionTime);
        }
        stats.setLastLogin(System.currentTimeMillis());
    }

    // Classe interne pour les statistiques d'un joueur
    public static class PlayerStats {
        private int blocksBroken;
        private int mobsKilled;
        private long playtime;
        private int highestPhase;
        private int challengesCompleted;
        private long lastLogin;

        public PlayerStats() {
            this(0, 0, 0, 1, 0, System.currentTimeMillis());
        }

        public PlayerStats(int blocksBroken, int mobsKilled, long playtime,
                           int highestPhase, int challengesCompleted, long lastLogin) {
            this.blocksBroken = blocksBroken;
            this.mobsKilled = mobsKilled;
            this.playtime = playtime;
            this.highestPhase = highestPhase;
            this.challengesCompleted = challengesCompleted;
            this.lastLogin = lastLogin;
        }

        // Getters
        public int getBlocksBroken() { return blocksBroken; }
        public int getMobsKilled() { return mobsKilled; }
        public long getPlaytime() { return playtime; }
        public int getHighestPhase() { return highestPhase; }
        public int getChallengesCompleted() { return challengesCompleted; }
        public long getLastLogin() { return lastLogin; }

        // Setters et méthodes d'incrémentation
        public void incrementBlocksBroken() { blocksBroken++; }
        public void incrementMobsKilled() { mobsKilled++; }
        public void incrementChallengesCompleted() { challengesCompleted++; }
        public void setHighestPhase(int phase) { highestPhase = phase; }
        public void setLastLogin(long time) { lastLogin = time; }
        public void addPlaytime(long seconds) { playtime += seconds; }
    }

    // Énumération des types de statistiques
    public enum StatType {
        BLOCKS_BROKEN,
        MOBS_KILLED,
        PLAYTIME,
        HIGHEST_PHASE,
        CHALLENGES_COMPLETED
    }
}