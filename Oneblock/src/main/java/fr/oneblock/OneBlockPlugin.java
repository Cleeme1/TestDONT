package fr.oneblock;

import fr.oneblock.challenge.ChallengeManager;
import fr.oneblock.command.OneBlockCommand;
import fr.oneblock.config.Config;
import fr.oneblock.config.Messages;
import fr.oneblock.gui.GUIManager;
import fr.oneblock.phase.PhaseManager;
import fr.oneblock.player.PlayerManager;
import fr.oneblock.stats.StatsManager;
import fr.oneblock.world.WorldManager;
import org.bukkit.plugin.java.JavaPlugin;

public class OneBlockPlugin extends JavaPlugin {

    private static OneBlockPlugin instance;
    private Config config;
    private Messages messages;
    private GUIManager guiManager;
    private PhaseManager phaseManager;
    private PlayerManager playerManager;
    private WorldManager worldManager;
    private ChallengeManager challengeManager;
    private StatsManager statsManager;

    @Override
    public void onEnable() {
        instance = this;

        // Initialisation des configurations
        saveDefaultConfig();
        saveResource("messages.yml", false);
        saveResource("phases.yml", false);
        saveResource("challenges.yml", false);

        // Chargement des configurations
        this.config = new Config(this);
        this.messages = new Messages(this);

        // Initialisation des gestionnaires dans l'ordre requis
        this.worldManager = new WorldManager(this);
        this.phaseManager = new PhaseManager(this);
        this.playerManager = new PlayerManager(this);
        this.statsManager = new StatsManager(this);
        this.challengeManager = new ChallengeManager(this);
        this.guiManager = new GUIManager(this); // Dépend des autres managers

        // Enregistrement des commandes et des événements
        registerCommands();
        registerEvents();

        // Chargement des données
        loadData();

        getLogger().info("OneBlock a été activé avec succès !");
    }

    @Override
    public void onDisable() {
        // Sauvegarde des données
        saveData();

        getLogger().info("OneBlock a été désactivé avec succès !");
    }

    private void registerCommands() {
        if (getCommand("oneblock") != null) {
            OneBlockCommand commandExecutor = new OneBlockCommand(this);
            getCommand("oneblock").setExecutor(commandExecutor);
            getCommand("oneblock").setTabCompleter(commandExecutor);
        } else {
            getLogger().severe("La commande 'oneblock' n'est pas définie dans plugin.yml !");
        }
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(playerManager, this);
        getServer().getPluginManager().registerEvents(guiManager, this);
        getServer().getPluginManager().registerEvents(worldManager, this);
        getServer().getPluginManager().registerEvents(challengeManager, this);
    }

    private void loadData() {
        worldManager.loadWorld();
        phaseManager.loadPhases();
        challengeManager.loadChallenges();
        statsManager.loadStats();
    }

    private void saveData() {
        if (playerManager != null) playerManager.saveAllPlayers();
        if (worldManager != null) worldManager.saveWorld();
        if (statsManager != null) statsManager.saveStats();
        if (challengeManager != null) challengeManager.saveChallenges();
    }

    // Getters
    public static OneBlockPlugin getInstance() {
        return instance;
    }

    public Config getConfiguration() {
        return config;
    }

    public Messages getMessages() {
        return messages;
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }

    public PhaseManager getPhaseManager() {
        return phaseManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public ChallengeManager getChallengeManager() {
        return challengeManager;
    }

    public StatsManager getStatsManager() {
        return statsManager;
    }
}
