package org.src.brokolnnSWhitelist.commands;

import org.bukkit.command.*;
import org.src.brokolnnSWhitelist.storage.WhitelistStorage;

public class WhitelistAddCommand implements CommandExecutor {

    private final WhitelistStorage storage;

    public WhitelistAddCommand(WhitelistStorage storage) {
        this.storage = storage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) return false;
        String name = args[0];
        storage.addPlayer(name);
        sender.sendMessage("§aИгрок " + name + " добавлен в вайтлист.");
        return true;
    }
}
