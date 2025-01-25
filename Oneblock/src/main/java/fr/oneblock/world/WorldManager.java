package fr.oneblock.world;

import fr.oneblock.OneBlockPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorldManager implements Listener {
    private final OneBlockPlugin plugin;
    private final World oneblockWorld;
    private final Map<UUID, Location> islandLocations;
    private final File worldDataFile;
    private final int ISLAND_SPACING = 1000;

    public WorldManager(OneBlockPlugin plugin) {
        this.plugin = plugin;
        this.worldDataFile = new File(plugin.getDataFolder(), "islands.yml");
        this.islandLocations = new HashMap<>();

        // Création ou chargement du monde
        WorldCreator creator = new WorldCreator("oneblock_world");
        creator.generator(new VoidGenerator());
        creator.environment(World.Environment.NORMAL);
        this.oneblockWorld = Bukkit.createWorld(creator);

        if (this.oneblockWorld != null) {
            this.oneblockWorld.setSpawnLocation(0, 100, 0);
            this.oneblockWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            this.oneblockWorld.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            this.oneblockWorld.setTime(6000);
        }

        loadWorld();
    }

    public World getWorld() {
        return oneblockWorld;
    }

    public void createIsland(Player player) {
        // Calcul d'une nouvelle position pour l'île
        int currentIslands = islandLocations.size();
        int x = ((currentIslands % 10) * ISLAND_SPACING);
        int z = ((currentIslands / 10) * ISLAND_SPACING);

        Location islandLoc = new Location(oneblockWorld, x, 100, z);

        // Création du bloc initial
        islandLoc.getBlock().setType(Material.GRASS_BLOCK);

        // Sauvegarde de la position
        islandLocations.put(player.getUniqueId(), islandLoc);

        // Téléportation du joueur
        Location spawnLoc = islandLoc.clone().add(0.5, 1, 0.5);
        player.teleport(spawnLoc);

        saveWorld();
    }

    public void resetIsland(Player player) {
        UUID playerUUID = player.getUniqueId();
        Location islandLocation = islandLocations.get(playerUUID);

        if (islandLocation == null) {
            player.sendMessage("§cVous n'avez pas d'île à réinitialiser !");
            return;
        }

        // Supprimer l'île actuelle (remplacer par de l'air)
        for (int x = -10; x <= 10; x++) {
            for (int y = 100; y <= 110; y++) {
                for (int z = -10; z <= 10; z++) {
                    Location blockLocation = islandLocation.clone().add(x, y - 100, z);
                    blockLocation.getBlock().setType(Material.AIR);
                }
            }
        }

        // Recréer le bloc central
        Location newIslandCenter = islandLocation.clone();
        newIslandCenter.getBlock().setType(Material.GRASS_BLOCK);

        // Téléporter le joueur sur l'île
        Location spawnLocation = newIslandCenter.clone().add(0.5, 1, 0.5);
        player.teleport(spawnLocation);

        player.sendMessage("§aVotre île a été réinitialisée avec succès !");
    }

    public void teleportToIsland(Player player) {
        Location islandLoc = islandLocations.get(player.getUniqueId());
        if (islandLoc != null) {
            Location spawnLoc = islandLoc.clone().add(0.5, 1, 0.5);
            player.teleport(spawnLoc);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        // Vérifier si le bloc cassé est un bloc OneBlock
        Location blockLoc = block.getLocation();
        Location islandLoc = islandLocations.get(player.getUniqueId());

        if (islandLoc != null && isSameBlock(blockLoc, islandLoc)) {
            event.setCancelled(true);
            // Obtenir le prochain bloc
            Material nextBlock = plugin.getPhaseManager().getNextBlock(
                    plugin.getPlayerManager().getPlayer(player.getUniqueId()).getPhase()
            );
            block.setType(nextBlock);

            // Mettre à jour les statistiques du joueur
            plugin.getPlayerManager().getPlayer(player.getUniqueId()).incrementBlocksBroken();
        }
    }

    private boolean isSameBlock(Location loc1, Location loc2) {
        return loc1.getBlockX() == loc2.getBlockX() &&
                loc1.getBlockY() == loc2.getBlockY() &&
                loc1.getBlockZ() == loc2.getBlockZ();
    }

    public void loadWorld() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(worldDataFile);
        for (String uuidStr : config.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidStr);
            int x = config.getInt(uuidStr + ".x");
            int y = config.getInt(uuidStr + ".y");
            int z = config.getInt(uuidStr + ".z");
            islandLocations.put(uuid, new Location(oneblockWorld, x, y, z));
        }
    }

    public void saveWorld() {
        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<UUID, Location> entry : islandLocations.entrySet()) {
            String uuidStr = entry.getKey().toString();
            Location loc = entry.getValue();
            config.set(uuidStr + ".x", loc.getBlockX());
            config.set(uuidStr + ".y", loc.getBlockY());
            config.set(uuidStr + ".z", loc.getBlockZ());
        }
        try {
            config.save(worldDataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Erreur lors de la sauvegarde des données du monde!");
            e.printStackTrace();
        }
    }
}
