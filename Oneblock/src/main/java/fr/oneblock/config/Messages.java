package fr.oneblock.config;

import fr.oneblock.OneBlockPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Messages {
    private final OneBlockPlugin plugin;
    private final Map<String, String> messages;
    private final File messagesFile;

    public Messages(OneBlockPlugin plugin) {
        this.plugin = plugin;
        this.messages = new HashMap<>();
        this.messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        loadMessages();
    }

    private void loadMessages() {
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(messagesFile);
        for (String key : config.getKeys(true)) {
            if (config.isString(key)) {
                messages.put(key, ChatColor.translateAlternateColorCodes('&', config.getString(key)));
            }
        }

        // Messages par défaut
        addDefaultMessage("prefix", "&8[&bOneBlock&8] &7");
        addDefaultMessage("welcome", "%prefix%&aBienvenue sur OneBlock !");
        addDefaultMessage("phase-up", "%prefix%&aVous passez à la phase &e%phase% &a!");
        addDefaultMessage("no-permission", "%prefix%&cVous n'avez pas la permission !");
        addDefaultMessage("unknown-command", "%prefix%&cCommande inconnue !");
        addDefaultMessage("island-created", "%prefix%&aVotre île a été créée !");
        addDefaultMessage("island-teleported", "%prefix%&aVous avez été téléporté à votre île !");
        addDefaultMessage("island-reset", "%prefix%&aVotre île a été réinitialisée !");
    }

    private void addDefaultMessage(String key, String message) {
        if (!messages.containsKey(key)) {
            messages.put(key, ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    public String get(String key) {
        plugin.getLogger().info("Récupération du message pour la clé : " + key);

        // Vérification si la clé existe
        if (!messages.containsKey(key)) {
            plugin.getLogger().warning("Le message avec la clé '" + key + "' est introuvable dans messages.yml !");
            return "§cMessage manquant : " + key;
        }

        // Récupération du message
        String message = messages.get(key);

        // Éviter une boucle infinie pour la clé "prefix"
        if (key.equals("prefix")) {
            return message;
        }

        // Remplacement de %prefix% dans les autres messages
        return message.replace("%prefix%", get("prefix"));
    }

    public String get(String key, Map<String, String> placeholders) {
        String message = get(key);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return message;
    }

    public void reload() {
        messages.clear();
        loadMessages();
    }
}
