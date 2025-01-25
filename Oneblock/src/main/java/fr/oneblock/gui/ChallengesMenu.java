package fr.oneblock.gui;

import fr.oneblock.OneBlockPlugin;
import fr.oneblock.challenge.Challenge;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class ChallengesMenu extends GUIManager.GUI {

    public ChallengesMenu(OneBlockPlugin plugin) {
        super(plugin, "§8• §eDéfis Disponibles §8•", 54);
    }

    @Override
    public Inventory createInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, size, title);

        // Obtenir tous les défis disponibles pour le joueur
        List<Challenge> challenges = plugin.getChallengeManager().getAvailableChallenges(player);

        // Diviser les défis en catégories
        List<Challenge> miningChallenges = challenges.stream()
                .filter(c -> c.getType().name().contains("BREAK"))
                .collect(Collectors.toList());
        List<Challenge> combatChallenges = challenges.stream()
                .filter(c -> c.getType().name().contains("KILL"))
                .collect(Collectors.toList());
        List<Challenge> progressionChallenges = challenges.stream()
                .filter(c -> c.getType().name().contains("REACH"))
                .collect(Collectors.toList());

        // Ajouter les défis au menu
        int slot = 0;

        slot = addCategory(inv, miningChallenges, slot, "§bDéfis de Minage", Material.DIAMOND_PICKAXE);
        slot = addCategory(inv, combatChallenges, slot, "§cDéfis de Combat", Material.IRON_SWORD);
        addCategory(inv, progressionChallenges, slot, "§eDéfis de Progression", Material.EXPERIENCE_BOTTLE);

        return inv;
    }

    private int addCategory(Inventory inv, List<Challenge> challenges, int startSlot, String title, Material icon) {
        if (!challenges.isEmpty()) {
            inv.setItem(startSlot++, createGuiItem(icon, title));
            for (Challenge challenge : challenges) {
                if (startSlot >= size) break;
                inv.setItem(startSlot++, createGuiItem(
                        Material.BOOK,
                        "§a" + challenge.getName(),
                        "§7" + challenge.getDescription(),
                        "§7Objectif : §b" + challenge.getAmount() + " " + challenge.getTarget(),
                        "§7Phase Requise : §6" + challenge.getPhaseRequired(),
                        "§7Récompenses : §e" + String.join(", ", challenge.getRewards())
                ));
            }
        }
        return startSlot;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        // Gestion des clics sur un défi
        if (clickedItem.getType() == Material.BOOK) {
            String challengeName = clickedItem.getItemMeta() != null ? clickedItem.getItemMeta().getDisplayName() : null;
            player.sendMessage("§aDétails du défi sélectionné : " + challengeName);
            // Optionnel : Ajouter des interactions comme s'inscrire au défi ou afficher des infos détaillées.
        }
    }
}
