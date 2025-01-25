package fr.oneblock.gui;

import fr.oneblock.OneBlockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIManager implements Listener {

    private final OneBlockPlugin plugin;
    private final Map<UUID, GUI> openGUIs;
    private final Map<String, GUI> registeredGUIs;

    public GUIManager(OneBlockPlugin plugin) {
        this.plugin = plugin;
        this.openGUIs = new HashMap<>();
        this.registeredGUIs = new HashMap<>();

        // Enregistrement des GUIs par défaut
        registerGUI("main", new MainMenu(plugin));
        registerGUI("phases", new PhaseMenu(plugin));
        registerGUI("settings", new SettingsMenu(plugin));
        registerGUI("stats", new StatsMenu(plugin));
        registerGUI("challenges", new ChallengesMenu(plugin));
    }

    public void registerGUI(String id, GUI gui) {
        registeredGUIs.put(id, gui);
    }

    public void openGUI(Player player, String guiId) {
        GUI gui = registeredGUIs.get(guiId);
        if (gui != null) {
            Inventory inv = gui.createInventory(player);
            player.openInventory(inv);
            openGUIs.put(player.getUniqueId(), gui);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        GUI gui = openGUIs.get(player.getUniqueId());
        if (gui != null) {
            event.setCancelled(true);
            gui.handleClick(event);
        }
    }

    public abstract static class GUI {
        protected final OneBlockPlugin plugin;
        protected final String title;
        protected final int size;

        public GUI(OneBlockPlugin plugin, String title, int size) {
            this.plugin = plugin;
            this.title = title;
            this.size = size;
        }

        public abstract Inventory createInventory(Player player);
        public abstract void handleClick(InventoryClickEvent event);

        protected ItemStack createGuiItem(Material material, String name, String... lore) {
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§r" + name);
                if (lore.length > 0) {
                    meta.setLore(java.util.Arrays.asList(lore));
                }
                item.setItemMeta(meta);
            }
            return item;
        }
    }
}