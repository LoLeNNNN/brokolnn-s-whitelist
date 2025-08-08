package org.src.brokolnnSWhitelist.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PermissionManager {

    private final JavaPlugin plugin;

    public PermissionManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    public boolean hasPermission(CommandSender sender, String permission) {
        // Проверяем настройку require-permission
        boolean requirePermission = plugin.getConfig().getBoolean("permissions.require-permission", true);

        if (!requirePermission) {
            return true; // Если не требуются права, разрешаем всем
        }

        // Операторы всегда имеют доступ
        if (sender.isOp()) {
            return true;
        }

        // Проверяем конкретное право
        return sender.hasPermission(permission);
    }

    /**
     * Проверяет права для конкретного действия
     */
    public boolean hasWhitelistPermission(CommandSender sender, String action) {
        return hasPermission(sender, "whitelist.manage." + action) ||
                hasPermission(sender, "whitelist.manage") ||
                hasPermission(sender, "whitelist.*");
    }

    /**
     * Проверяет, является ли отправитель консолью
     */
    public boolean isConsole(CommandSender sender) {
        return !(sender instanceof Player);
    }

    /**
     * Проверяет права с более детальной логикой
     */
    public PermissionResult checkPermission(CommandSender sender, String action) {
        if (isConsole(sender)) {
            return PermissionResult.ALLOWED_CONSOLE;
        }

        if (!plugin.getConfig().getBoolean("permissions.require-permission", true)) {
            return PermissionResult.ALLOWED_NO_REQUIREMENTS;
        }

        if (sender.isOp()) {
            return PermissionResult.ALLOWED_OP;
        }

        if (hasWhitelistPermission(sender, action)) {
            return PermissionResult.ALLOWED_PERMISSION;
        }

        return PermissionResult.DENIED;
    }

    public enum PermissionResult {
        ALLOWED_CONSOLE("Console access"),
        ALLOWED_NO_REQUIREMENTS("No permission requirements"),
        ALLOWED_OP("Operator access"),
        ALLOWED_PERMISSION("Has required permission"),
        DENIED("Access denied");

        private final String description;

        PermissionResult(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public boolean isAllowed() {
            return this != DENIED;
        }
    }
}