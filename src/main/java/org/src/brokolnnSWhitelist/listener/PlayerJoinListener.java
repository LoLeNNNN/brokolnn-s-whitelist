package org.src.brokolnnSWhitelist.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.src.brokolnnSWhitelist.storage.WhitelistStorage;
import org.src.brokolnnSWhitelist.utils.Messages;

public class PlayerJoinListener implements Listener {

    private final WhitelistStorage storage;

    public PlayerJoinListener(WhitelistStorage storage) {
        this.storage = storage;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();

        if (!storage.isWhitelisted(playerName)) {
            event.setJoinMessage(null);

            String kickMessage = Messages.getLocalizedKickMessage(event.getPlayer(), "not-whitelisted-kick");
            kickMessage = kickMessage.replace("%player%", playerName);

            event.getPlayer().kickPlayer(kickMessage);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getName();

        if (!storage.isWhitelisted(playerName)) {
            event.setQuitMessage(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        String playerName = event.getPlayer().getName();
        Player player = event.getPlayer();

        if (!storage.isWhitelisted(playerName)) {
            String kickMessage = Messages.getLocalizedKickMessage(player, "not-whitelisted-login");
            kickMessage = kickMessage.replace("%player%", playerName);

            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, kickMessage);
        }
    }
}