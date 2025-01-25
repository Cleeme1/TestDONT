package fr.oneblock.gui;

import fr.oneblock.OneBlockPlugin;
import fr.oneblock.player.OneBlockPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class SettingsMenu extends GUIManager.GUI {

    public SettingsMenu(OneBlockPlugin plugin) {
        super(plugin, "§8• §cParamètres §8•", 27);
    }

    @Override
    public Inventory createInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, size, title);

        // Exemple de paramètres avec options activées/désactivées
        inv.setItem(11, createGuiItem(Material.REDSTONE_TORCH, "§cActiver/Desactiver la TNT", "§7Cliquez pour activer/désactiver", "§7la destruction par la TNT."));
        inv.setItem(13, createGuiItem(Material.CLOCK, "§eChanger l'heure du jour", "§7Cliquez pour basculer entre", "§7le jour et la nuit."));
        inv.setItem(15, createGuiItem(Material.BOOK, "§aAfficher les tutoriels", "§7Cliquez pour afficher/masquer", "§7les messages d'aide."));

        return inv;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        switch (slot) {
            case 11 -> {
                // Activer/Désactiver la TNT (exemple)
                boolean isTntEnabled = plugin.getConfig().getBoolean("settings.tnt", true);
                plugin.getConfig().set("settings.tnt", !isTntEnabled);
                plugin.saveConfig();
                player.sendMessage("§aDestruction par TNT: " + (!isTntEnabled ? "§aActivée" : "§cDésactivée"));
            }
            case 13 -> {
                // Basculer l'heure du jour
                boolean isDay = plugin.getWorldManager().getWorld().getTime() < 12300;
                plugin.getWorldManager().getWorld().setTime(isDay ? 18000 : 1000);
                player.sendMessage("§aHeure changée en: " + (isDay ? "§cNuit" : "§eJour"));
            }
            case 15 -> {
                // Activer/Désactiver les tutoriels
                boolean showTutorials = plugin.getConfig().getBoolean("settings.tutorials", true);
                plugin.getConfig().set("settings.tutorials", !showTutorials);
                plugin.saveConfig();
                player.sendMessage("§aTutoriels: " + (!showTutorials ? "§cMasqués" : "§aAffichés"));
            }
        }
        player.closeInventory();
    }
}