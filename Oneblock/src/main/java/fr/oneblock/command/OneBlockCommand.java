package fr.oneblock.command;

import fr.oneblock.OneBlockPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OneBlockCommand implements CommandExecutor, TabCompleter {

    private final OneBlockPlugin plugin;
    private final List<String> adminCommands = Arrays.asList("reload", "setphase", "reset");

    public OneBlockCommand(OneBlockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cCette commande ne peut être utilisée que par un joueur !");
            return true;
        }

        if (args.length == 0) {
            // Ouvre le menu principal
            plugin.getGuiManager().openGUI(player, "main");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help" -> sendHelp(player);
            case "menu" -> plugin.getGuiManager().openGUI(player, "main");
            case "stats" -> plugin.getGuiManager().openGUI(player, "stats");
            case "challenges" -> plugin.getGuiManager().openGUI(player, "challenges");
            case "home" -> plugin.getWorldManager().teleportToIsland(player);

            case "reload" -> {
                if (!player.hasPermission("oneblock.admin")) {
                    player.sendMessage(plugin.getMessages().get("no-permission"));
                    return true;
                }
                plugin.reloadConfig();
                plugin.getPhaseManager().loadPhases();
                plugin.getChallengeManager().loadChallenges();
                player.sendMessage("§aConfiguration rechargée !");
            }
            case "setphase" -> {
                if (!player.hasPermission("oneblock.admin")) {
                    player.sendMessage(plugin.getMessages().get("no-permission"));
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage("§cUtilisation: /ob setphase <phase>");
                    return true;
                }
                try {
                    int phase = Integer.parseInt(args[1]);
                    plugin.getPlayerManager().getPlayer(player.getUniqueId()).setPhase(phase);
                    player.sendMessage("§aPhase définie sur " + phase);
                } catch (NumberFormatException e) {
                    player.sendMessage("§cVeuillez entrer un nombre valide !");
                }
            }
            case "reset" -> {
                if (!player.hasPermission("oneblock.admin")) {
                    player.sendMessage(plugin.getMessages().get("no-permission"));
                    return true;
                }
                if (args.length < 2) {
                    plugin.getWorldManager().resetIsland(player);
                    return true;
                }
                Player target = plugin.getServer().getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage("§cJoueur non trouvé !");
                    return true;
                }
                plugin.getWorldManager().resetIsland(target);
                player.sendMessage("§aÎle de " + target.getName() + " réinitialisée !");
            }
            default -> player.sendMessage(plugin.getMessages().get("unknown-command"));
        }

        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage("§8§l━━━━━━━━━━ §b§lOneBlock §8§l━━━━━━━━━━");
        player.sendMessage("§b/ob §8- §7Ouvre le menu principal");
        player.sendMessage("§b/ob help §8- §7Affiche cette aide");
        player.sendMessage("§b/ob menu §8- §7Ouvre le menu principal");
        player.sendMessage("§b/ob stats §8- §7Affiche vos statistiques");
        player.sendMessage("§b/ob challenges §8- §7Affiche les défis");
        player.sendMessage("§b/ob home §8- §7Téléporte à votre île");

        if (player.hasPermission("oneblock.admin")) {
            player.sendMessage("§c/ob reload §8- §7Recharge la configuration");
            player.sendMessage("§c/ob setphase <phase> §8- §7Définit la phase");
            player.sendMessage("§c/ob reset [joueur] §8- §7Réinitialise une île");
        }

        player.sendMessage("§8§l━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> commands = new ArrayList<>(Arrays.asList("help", "menu", "stats", "challenges", "home"));
            if (sender.hasPermission("oneblock.admin")) {
                commands.addAll(adminCommands);
            }
            String input = args[0].toLowerCase();
            completions.addAll(commands.stream().filter(cmd -> cmd.startsWith(input)).toList());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("reset") && sender.hasPermission("oneblock.admin")) {
            String input = args[1].toLowerCase();
            completions.addAll(plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(input))
                    .toList());
        }

        return completions;
    }
}
