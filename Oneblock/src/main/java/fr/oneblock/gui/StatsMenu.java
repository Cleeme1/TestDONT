package fr.oneblock.gui;

import fr.oneblock.OneBlockPlugin;
import fr.oneblock.player.OneBlockPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class StatsMenu extends GUIManager.GUI {

    public StatsMenu(OneBlockPlugin plugin) {
        super(plugin, "§8• §6Statistiques §8•", 27);
    }

    @Override
    public Inventory createInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, size, title);
        OneBlockPlayer obPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());

        // Afficher les statistiques du joueur
        inv.setItem(11, createGuiItem(Material.DIAMOND_PICKAXE, "§bBlocs cassés", "§7Vous avez cassé §e" + obPlayer.getBlocksBroken() + " §7blocs."));
        inv.setItem(13, createGuiItem(Material.IRON_SWORD, "§bMobs tués", "§7Vous avez tué §e" + obPlayer.getMobsKilled() + " §7mobs."));
        inv.setItem(15, createGuiItem(Material.EXPERIENCE_BOTTLE, "§bDéfis complétés", "§7Vous avez complété §e" + obPlayer.getChallengesCompleted() + " §7défis."));

        return inv;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        // Le menu de statistiques est informatif, donc aucun clic interactif n'est géré ici.
        event.setCancelled(true);
    }
}
