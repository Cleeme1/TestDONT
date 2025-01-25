package fr.oneblock.player;

import fr.oneblock.OneBlockPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager implements Listener {

    private final OneBlockPlugin plugin;
    private final Map<UUID, OneBlockPlayer> players;
    private final File playerDataFolder;

    public PlayerManager(OneBlockPlugin plugin) {
        this.plugin = plugin;
        this.players = new HashMap<>();
        this.playerDataFolder = new File(plugin.getDataFolder(), "playerdata");

        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        loadPlayer(player);

        // Afficher le message de bienvenue
        OneBlockPlayer obPlayer = getPlayer(player.getUniqueId());
        if (obPlayer.isFirstJoin()) {
            player.sendMessage(plugin.getMessages().get("welcome"));
            plugin.getWorldManager().createIsland(player);
            obPlayer.setFirstJoin(false);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        savePlayer(event.getPlayer().getUniqueId());
        players.remove(event.getPlayer().getUniqueId());
    }

    public void loadPlayer(Player player) {
        File playerFile = new File(playerDataFolder, player.getUniqueId() + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

        OneBlockPlayer obPlayer = new OneBlockPlayer(
                player.getUniqueId(),
                config.getInt("phase", 1),
                config.getInt("blocks-broken", 0),
                config.getLong("play-time", 0),
                config.getBoolean("first-join", true)
        );

        players.put(player.getUniqueId(), obPlayer);
    }

    public void savePlayer(UUID uuid) {
        OneBlockPlayer player = players.get(uuid);
        if (player == null) return;

        File playerFile = new File(playerDataFolder, uuid + ".yml");
        YamlConfiguration config = new YamlConfiguration();

        config.set("phase", player.getPhase());
        config.set("blocks-broken", player.getBlocksBroken());
        config.set("play-time", player.getPlayTime());
        config.set("first-join", player.isFirstJoin());

        try {
            config.save(playerFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Erreur lors de la sauvegarde des données du joueur: " + uuid);
            e.printStackTrace();
        }
    }

    public void saveAllPlayers() {
        for (UUID uuid : players.keySet()) {
            savePlayer(uuid);
        }
    }

    public OneBlockPlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }
}