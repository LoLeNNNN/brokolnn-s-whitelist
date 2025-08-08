package org.src.brokolnnSWhitelist.commands;

import org.bukkit.command.*;
import org.src.brokolnnSWhitelist.storage.WhitelistStorage;

public class WhitelistRemoveCommand implements CommandExecutor {

    private final WhitelistStorage storage;

    public WhitelistRemoveCommand(WhitelistStorage storage) {
        this.storage = storage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) return false;

        String name = args[0];
        storage.removePlayer(name);
        sender.sendMessage("§cИгрок " + name + " удалён из вайтлиста.");
        return true;
    }
}
