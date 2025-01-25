package fr.oneblock.gui;

import fr.oneblock.OneBlockPlugin;
import fr.oneblock.player.OneBlockPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MainMenu extends GUIManager.GUI {

    public MainMenu(OneBlockPlugin plugin) {
        super(plugin, "§8• §bOneBlock §8•", 54);
    }

    @Override
    public Inventory createInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, size, title);
        OneBlockPlayer obPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());

        // Informations du joueur
        inv.setItem(4, createGuiItem(Material.PLAYER_HEAD,
                "§b" + player.getName(),
                "§7Phase: §f" + obPlayer.getPhase(),
                "§7Blocs cassés: §f" + obPlayer.getBlocksBroken(),
                "§7Temps de jeu: §f" + formatPlayTime(obPlayer.getPlayTime())
        ));

        // Boutons de navigation
        inv.setItem(19, createGuiItem(Material.GRASS_BLOCK,
                "§aTéléportation à l'île",
                "§7Cliquez pour vous téléporter",
                "§7à votre île OneBlock"
        ));

        inv.setItem(21, createGuiItem(Material.DIAMOND_PICKAXE,
                "§bPhases et Progression",
                "§7Visualisez votre progression",
                "§7et les phases à venir"
        ));

        inv.setItem(23, createGuiItem(Material.BOOK,
                "§eDéfis quotidiens",
                "§7Accomplissez des défis pour",
                "§7obtenir des récompenses"
        ));

        inv.setItem(25, createGuiItem(Material.GOLDEN_APPLE,
                "§6Classement",
                "§7Voyez votre position dans",
                "§7le classement des joueurs"
        ));

        // Paramètres et informations
        inv.setItem(49, createGuiItem(Material.REDSTONE_TORCH,
                "§cParamètres",
                "§7Configurez vos préférences"
        ));

        return inv;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        switch (slot) {
            case 19 -> { // Téléportation
                player.closeInventory();
                plugin.getWorldManager().teleportToIsland(player);
            }
            case 21 -> plugin.getGuiManager().openGUI(player, "phases");
            case 23 -> plugin.getGuiManager().openGUI(player, "challenges");
            case 25 -> plugin.getGuiManager().openGUI(player, "stats");
            case 49 -> plugin.getGuiManager().openGUI(player, "settings");
        }
    }

    private String formatPlayTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        return String.format("%02dh %02dm", hours, minutes);
    }
}