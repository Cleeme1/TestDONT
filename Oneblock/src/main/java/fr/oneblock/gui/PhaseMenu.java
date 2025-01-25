package fr.oneblock.gui;

import fr.oneblock.OneBlockPlugin;
import fr.oneblock.phase.Phase;
import fr.oneblock.player.OneBlockPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PhaseMenu extends GUIManager.GUI {

    public PhaseMenu(OneBlockPlugin plugin) {
        super(plugin, "§8• §bPhases OneBlock §8•", 54);
    }

    @Override
    public Inventory createInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, size, title);
        OneBlockPlayer obPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        List<Phase> phases = plugin.getPhaseManager().getAllPhases();

        // Informations de la phase actuelle
        Phase currentPhase = plugin.getPhaseManager().getPhase(obPlayer.getPhase());
        if (currentPhase != null) {
            inv.setItem(4, createGuiItem(Material.BEACON,
                    "§bPhase actuelle: " + currentPhase.getName(),
                    "§7Niveau: §f" + currentPhase.getLevel(),
                    "§7Progression: §f" + obPlayer.getBlocksBroken() + "/" + currentPhase.getBlocksRequired(),
                    "§7Type: §f" + currentPhase.getType().getDisplayName()
            ));
        }

        // Affichage des phases
        int slot = 19;
        for (Phase phase : phases) {
            if (slot >= 53) break;

            Material icon = getPhaseIcon(phase);
            boolean unlocked = phase.getLevel() <= obPlayer.getPhase();
            String name = (unlocked ? "§a" : "§c") + phase.getName();

            String[] lore = {
                    "§7Niveau: §f" + phase.getLevel(),
                    "§7Type: §f" + phase.getType().getDisplayName(),
                    "",
                    unlocked ? "§aDébloquée" : "§cBloquée",
                    "§7Blocs requis: §f" + phase.getBlocksRequired()
            };

            inv.setItem(slot, createGuiItem(icon, name, lore));

            if ((slot + 2) % 9 == 0) {
                slot += 3;
            } else {
                slot++;
            }
        }

        // Bouton retour
        inv.setItem(49, createGuiItem(Material.BARRIER, "§cRetour", "§7Retour au menu principal"));

        return inv;
    }

    private Material getPhaseIcon(Phase phase) {
        return switch (phase.getType()) {
            case PLAINS -> Material.GRASS_BLOCK;
            case DESERT -> Material.SAND;
            case NETHER -> Material.NETHERRACK;
            case END -> Material.END_STONE;
            default -> Material.STONE;
        };
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        if (slot == 49) {
            plugin.getGuiManager().openGUI(player, "main");
        }
    }
}