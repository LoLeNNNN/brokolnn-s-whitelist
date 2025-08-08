package org.src.brokolnnSWhitelist.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.src.brokolnnSWhitelist.storage.WhitelistStorage;
import org.src.brokolnnSWhitelist.utils.Messages;
import org.src.brokolnnSWhitelist.BrokolnnSWhitelist;
import org.bukkit.ChatColor;
import org.src.brokolnnSWhitelist.utils.MigrationManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WhitelistCommand implements CommandExecutor, TabCompleter {

    private final WhitelistStorage storage;
    private final BrokolnnSWhitelist plugin;

    // Старый конструктор для обратной совместимости с предыдущей версии(для себя)
    public WhitelistCommand(WhitelistStorage storage) {
        this.storage = storage;
        this.plugin = null; // Будет null, если используется старый конструктор
    }

    // Новый конструктор с плагином
    public WhitelistCommand(WhitelistStorage storage, BrokolnnSWhitelist plugin) {
        this.storage = storage;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!hasPermission(sender, "whitelist.manage")) {
            sender.sendMessage(Messages.getLocalizedMessage(sender, "no-permission"));
            return true;
        }

        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }

        String action = args[0].toLowerCase();
        switch (action) {
            case "add":
                return handleAddCommand(sender, args);
            case "remove":
                return handleRemoveCommand(sender, args);
            case "list":
                return handleListCommand(sender);
            case "reload":
                return handleReloadCommand(sender);
            case "migrate":
                return handleMigrateCommand(sender, args);
            default:
                sender.sendMessage(Messages.getLocalizedMessage(sender, "unknown-command")
                        .replace("%command%", action));
                return true;
        }
    }

    private boolean hasPermission(CommandSender sender, String permission) {
        boolean requirePermission = plugin.getConfig().getBoolean("permissions.require-permission", true);

        if (!requirePermission) {
            return true;
        }

        return sender.hasPermission(permission);
    }

    private boolean handleAddCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Messages.getLocalizedMessage(sender, "command-usage")
                    .replace("%command%", getCommandLabel())
                    .replace("%usage%", "add <name>"));
            return true;
        }

        String name = args[1];
        if (storage.isWhitelisted(name)) {
            sender.sendMessage(Messages.getLocalizedMessage(sender, "player-already-whitelisted")
                    .replace("%player%", name));
        } else {
            storage.addPlayer(name);
            sender.sendMessage(Messages.getLocalizedMessage(sender, "player-added")
                    .replace("%player%", name));
        }
        return true;
    }

    private boolean handleRemoveCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Messages.getLocalizedMessage(sender, "command-usage")
                    .replace("%command%", getCommandLabel())
                    .replace("%usage%", "remove <name>"));
            return true;
        }

        String name = args[1];
        if (!storage.isWhitelisted(name)) {
            sender.sendMessage(Messages.getLocalizedMessage(sender, "player-not-found")
                    .replace("%player%", name));
        } else {
            storage.removePlayer(name);
            sender.sendMessage(Messages.getLocalizedMessage(sender, "player-removed")
                    .replace("%player%", name));
        }
        return true;
    }

    private boolean handleListCommand(CommandSender sender) {
        List<String> players = storage.getWhitelistedPlayers();

        if (players.isEmpty()) {
            sender.sendMessage(Messages.getLocalizedMessage(sender, "whitelist-empty"));
        } else {
            sender.sendMessage(Messages.getLocalizedMessage(sender, "whitelist-header")
                    .replace("%count%", String.valueOf(players.size())));

            StringBuilder sb = new StringBuilder("&f");
            for (int i = 0; i < players.size(); i++) {
                sb.append("&7").append(i + 1).append(". &f").append(players.get(i));
                if (i < players.size() - 1) {
                    sb.append("\n");
                }
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', sb.toString()));
        }
        return true;
    }

    private boolean handleReloadCommand(CommandSender sender) {
        storage.reload();
        Messages.reload();
        plugin.reloadConfig();
        sender.sendMessage(Messages.getLocalizedMessage(sender, "whitelist-reloaded"));
        return true;
    }

    private boolean handleMigrateCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(Messages.getLocalizedMessage(sender, "command-usage")
                    .replace("%command%", getCommandLabel())
                    .replace("%usage%", "migrate <from> <to>"));
            return true;
        }

        String fromType = args[1].toLowerCase();
        String toType = args[2].toLowerCase();

        List<String> validTypes = Arrays.asList("file", "sqlite", "mysql");
        if (!validTypes.contains(fromType)) {
            sender.sendMessage(Messages.getLocalizedMessage(sender, "migration-invalid-type")
                    .replace("%type%", fromType));
            return true;
        }
        if (!validTypes.contains(toType)) {
            sender.sendMessage(Messages.getLocalizedMessage(sender, "migration-invalid-type")
                    .replace("%type%", toType));
            return true;
        }

        if (fromType.equals(toType)) {
            sender.sendMessage(Messages.getLocalizedMessage(sender, "migration-same-type"));
            return true;
        }

        sender.sendMessage(Messages.getLocalizedMessage(sender, "migration-started")
                .replace("%from%", fromType)
                .replace("%to%", toType));

        try {
            MigrationManager migrationManager = plugin.getMigrationManager();
            migrationManager.forceMigration(fromType, toType);

            List<String> players = storage.getWhitelistedPlayers();
            sender.sendMessage(Messages.getLocalizedMessage(sender, "migration-completed")
                    .replace("%count%", String.valueOf(players.size())));
        } catch (Exception e) {
            sender.sendMessage(Messages.getLocalizedMessage(sender, "migration-failed")
                    .replace("%error%", e.getMessage()));
        }

        return true;
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(Messages.getLocalizedMessage(sender, "help-header"));
        sender.sendMessage(Messages.getLocalizedMessage(sender, "help-add")
                .replace("%command%", getCommandLabel()));
        sender.sendMessage(Messages.getLocalizedMessage(sender, "help-remove")
                .replace("%command%", getCommandLabel()));
        sender.sendMessage(Messages.getLocalizedMessage(sender, "help-list")
                .replace("%command%", getCommandLabel()));
        sender.sendMessage(Messages.getLocalizedMessage(sender, "help-reload")
                .replace("%command%", getCommandLabel()));
        sender.sendMessage(Messages.getLocalizedMessage(sender, "help-migrate")
                .replace("%command%", getCommandLabel()));
    }

    private String getCommandLabel() {
        return "whitelist";
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!hasPermission(sender, "whitelist.manage")) {
            return completions;
        }

        if (args.length == 1) {
            String input = args[0].toLowerCase();
            List<String> subCommands = Arrays.asList("add", "remove", "list", "reload", "migrate");

            for (String subCommand : subCommands) {
                if (subCommand.startsWith(input)) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            String input = args[1].toLowerCase();

            if (subCommand.equals("remove")) {
                List<String> whitelistedPlayers = storage.getWhitelistedPlayers();
                for (String playerName : whitelistedPlayers) {
                    if (playerName.toLowerCase().startsWith(input)) {
                        completions.add(playerName);
                    }
                }
            } else if (subCommand.equals("add")) {
                for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                    String playerName = onlinePlayer.getName();
                    if (!storage.isWhitelisted(playerName) &&
                            playerName.toLowerCase().startsWith(input)) {
                        completions.add(playerName);
                    }
                }
            } else if (subCommand.equals("migrate")) {
                List<String> storageTypes = Arrays.asList("file", "sqlite", "mysql");
                for (String type : storageTypes) {
                    if (type.startsWith(input)) {
                        completions.add(type);
                    }
                }
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("migrate")) {
            String input = args[2].toLowerCase();
            List<String> storageTypes = Arrays.asList("file", "sqlite", "mysql");
            for (String type : storageTypes) {
                if (type.startsWith(input)) {
                    completions.add(type);
                }
            }
        }

        return completions;
    }
}