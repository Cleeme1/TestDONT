package fr.oneblock.challenge;

import fr.oneblock.OneBlockPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ChallengeManager implements Listener {
    private final OneBlockPlugin plugin;
    private final Map<String, Challenge> challenges;
    private final Map<UUID, Map<String, Integer>> playerProgress;
    private final Map<UUID, Set<String>> completedChallenges;
    private final File progressFile;

    public ChallengeManager(OneBlockPlugin plugin) {
        this.plugin = plugin;
        this.challenges = new HashMap<>();
        this.playerProgress = new HashMap<>();
        this.completedChallenges = new HashMap<>();
        this.progressFile = new File(plugin.getDataFolder(), "challenge_progress.yml");
        loadChallenges();
    }

    public void loadChallenges() {
        ConfigurationSection config = plugin.getConfig().getConfigurationSection("challenges");
        if (config == null) return;

        challenges.clear(); // Réinitialiser les défis avant de les recharger

        for (String key : config.getKeys(false)) {
            ConfigurationSection challengeSection = config.getConfigurationSection(key);
            if (challengeSection == null) continue;

            Challenge challenge = new Challenge(
                    key,
                    challengeSection.getString("name", "Unknown Challenge"),
                    challengeSection.getString("description", ""),
                    ChallengeType.valueOf(challengeSection.getString("type", "BREAK_BLOCK")),
                    challengeSection.getString("target", "ANY"),
                    challengeSection.getInt("amount", 1),
                    challengeSection.getInt("phase-required", 1),
                    challengeSection.getStringList("rewards")
            );

            challenges.put(key, challenge);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (!playerProgress.containsKey(uuid)) {
            playerProgress.put(uuid, new HashMap<>());
            completedChallenges.put(uuid, new HashSet<>());
            loadPlayerProgress(uuid);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material blockType = event.getBlock().getType();

        challenges.values().stream()
                .filter(challenge -> challenge.getType() == ChallengeType.BREAK_BLOCK)
                .filter(challenge -> !isCompleted(player.getUniqueId(), challenge.getId()))
                .filter(challenge -> challenge.getTarget().equals("ANY") || challenge.getTarget().equals(blockType.name()))
                .forEach(challenge -> incrementProgress(player, challenge));
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;

        Player player = event.getEntity().getKiller();
        EntityType entityType = event.getEntityType();

        challenges.values().stream()
                .filter(challenge -> challenge.getType() == ChallengeType.KILL_ENTITY)
                .filter(challenge -> !isCompleted(player.getUniqueId(), challenge.getId()))
                .filter(challenge -> challenge.getTarget().equals(entityType.name()))
                .forEach(challenge -> incrementProgress(player, challenge));
    }

    public void checkPhaseProgress(Player player, int newPhase) {
        challenges.values().stream()
                .filter(challenge -> challenge.getType() == ChallengeType.REACH_PHASE)
                .filter(challenge -> !isCompleted(player.getUniqueId(), challenge.getId()))
                .filter(challenge -> Integer.parseInt(challenge.getTarget()) == newPhase)
                .forEach(challenge -> completeChallenge(player, challenge));
    }

    private void incrementProgress(Player player, Challenge challenge) {
        UUID uuid = player.getUniqueId();
        Map<String, Integer> progress = playerProgress.computeIfAbsent(uuid, k -> new HashMap<>());

        int currentProgress = progress.getOrDefault(challenge.getId(), 0) + 1;
        progress.put(challenge.getId(), currentProgress);

        // Vérifier si le défi est complété
        if (currentProgress >= challenge.getAmount()) {
            completeChallenge(player, challenge);
        } else {
            // Envoyer le message de progression
            String progressMessage = plugin.getMessages().get("challenge-progress")
                    .replace("%challenge%", challenge.getName())
                    .replace("%progress%", String.valueOf(currentProgress))
                    .replace("%max%", String.valueOf(challenge.getAmount()));
            player.sendMessage(progressMessage);
        }

        savePlayerProgress(uuid);
    }

    private void completeChallenge(Player player, Challenge challenge) {
        UUID uuid = player.getUniqueId();
        if (isCompleted(uuid, challenge.getId())) return;

        completedChallenges.computeIfAbsent(uuid, k -> new HashSet<>()).add(challenge.getId());

        // Message de complétion
        player.sendMessage(plugin.getMessages().get("challenge-complete")
                .replace("%challenge%", challenge.getName()));

        // Distribution des récompenses
        for (String reward : challenge.getRewards()) {
            String command = reward.replace("%player%", player.getName());
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
        }

        player.sendMessage(plugin.getMessages().get("reward-received"));
        savePlayerProgress(uuid);
    }

    private void loadPlayerProgress(UUID uuid) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(progressFile);
        String uuidStr = uuid.toString();

        if (config.contains(uuidStr + ".progress")) {
            ConfigurationSection progressSection = config.getConfigurationSection(uuidStr + ".progress");
            if (progressSection != null) {
                for (String challenge : progressSection.getKeys(false)) {
                    playerProgress.get(uuid).put(challenge, progressSection.getInt(challenge));
                }
            }
        }

        if (config.contains(uuidStr + ".completed")) {
            completedChallenges.put(uuid, new HashSet<>(config.getStringList(uuidStr + ".completed")));
        }
    }

    private void savePlayerProgress(UUID uuid) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(progressFile);
        String uuidStr = uuid.toString();

        // Sauvegarder la progression
        Map<String, Integer> progress = playerProgress.get(uuid);
        if (progress != null) {
            for (Map.Entry<String, Integer> entry : progress.entrySet()) {
                config.set(uuidStr + ".progress." + entry.getKey(), entry.getValue());
            }
        }

        // Sauvegarder les défis complétés
        Set<String> completed = completedChallenges.get(uuid);
        if (completed != null) {
            config.set(uuidStr + ".completed", new ArrayList<>(completed));
        }

        try {
            config.save(progressFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Erreur lors de la sauvegarde de la progression des défis: " + e.getMessage());
        }
    }

    public void saveChallenges() {
        for (UUID uuid : playerProgress.keySet()) {
            savePlayerProgress(uuid);
        }
    }

    public boolean isCompleted(UUID uuid, String challengeId) {
        Set<String> completed = completedChallenges.get(uuid);
        return completed != null && completed.contains(challengeId);
    }

    public List<Challenge> getAvailableChallenges(Player player) {
        int playerPhase = plugin.getPlayerManager().getPlayer(player.getUniqueId()).getPhase();
        return challenges.values().stream()
                .filter(challenge -> challenge.getPhaseRequired() <= playerPhase)
                .filter(challenge -> !isCompleted(player.getUniqueId(), challenge.getId()))
                .toList();
    }

    public Map<String, Integer> getPlayerProgress(UUID uuid) {
        return playerProgress.getOrDefault(uuid, new HashMap<>());
    }
}
