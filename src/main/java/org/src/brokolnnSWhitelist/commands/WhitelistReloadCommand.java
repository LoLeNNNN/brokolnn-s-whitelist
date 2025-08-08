package org.src.brokolnnSWhitelist.commands;

import org.bukkit.command.*;
import org.src.brokolnnSWhitelist.storage.WhitelistStorage;

public class WhitelistReloadCommand implements CommandExecutor {

    private final WhitelistStorage storage;

    public WhitelistReloadCommand(WhitelistStorage storage) {
        this.storage = storage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        storage.reload();
        sender.sendMessage("§eВайтлист перезагружен.");
        return true;
    }
}
