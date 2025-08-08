package org.src.brokolnnSWhitelist.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Messages {

    private static JavaPlugin plugin;
    private static Map<String, FileConfiguration> localeConfigs = new HashMap<>();
    private static String defaultLanguage = "en";

    public static void init(JavaPlugin plugin) {
        Messages.plugin = plugin;

        // Загружаем файлы локализации
        loadLocaleFile("en");
        loadLocaleFile("ru");

        // Устанавливаем язык по умолчанию из конфига
        defaultLanguage = plugin.getConfig().getString("language", "auto");
        if (defaultLanguage.equals("auto")) {
            defaultLanguage = "en"; // По умолчанию английский для авто-режима
        }
    }

    private static void loadLocaleFile(String locale) {
        File localeFile = new File(plugin.getDataFolder(), "locales/messages_" + locale + ".yml");
        if (localeFile.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(localeFile);
            localeConfigs.put(locale, config);
        } else {
            plugin.getLogger().warning("Locale file not found: messages_" + locale + ".yml");
        }
    }

    public static String get(String key) {
        return get(key, defaultLanguage);
    }

    public static String get(String key, String locale) {
        FileConfiguration config = localeConfigs.get(locale);
        if (config == null) {
            config = localeConfigs.get("en"); // Fallback to English
        }

        String message = config != null ? config.getString(key, "Message not found: " + key) : "Message not found: " + key;
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String get(String key, String placeholder, String value) {
        return get(key).replace(placeholder, value);
    }

    public static String getLocalizedMessage(CommandSender sender, String key) {
        String locale = getLanguage(sender);
        return get(key, locale);
    }

    public static String getLocalizedKickMessage(Player player, String key) {
        String locale = getLanguageForPlayer(player);

        // Получаем настраиваемое сообщение из конфига
        String configPath = "kick-messages." + key.replace("not-whitelisted-", "");
        String configMessage = plugin.getConfig().getString(configPath + ".description");

        if (configMessage != null && !configMessage.isEmpty()) {
            return ChatColor.translateAlternateColorCodes('&', configMessage);
        }

        // Иначе используем стандартное сообщение из локализации
        return get(key, locale);
    }

    public static String getLanguage(CommandSender sender) {
        String configLanguage = plugin.getConfig().getString("language", "auto");

        if (configLanguage.equals("auto") && sender instanceof Player) {
            Player player = (Player) sender;
            String locale = player.getLocale();
            if (locale.startsWith("ru")) {
                return "ru";
            }
        } else if (!configLanguage.equals("auto")) {
            return configLanguage;
        }

        return "en";
    }

    public static String getLanguageForPlayer(Player player) {
        String configLanguage = plugin.getConfig().getString("language", "auto");

        if (configLanguage.equals("auto")) {
            String locale = player.getLocale();
            if (locale.startsWith("ru")) {
                return "ru";
            }
            return "en";
        }

        return configLanguage;
    }

    public static void reload() {
        localeConfigs.clear();
        loadLocaleFile("en");
        loadLocaleFile("ru");

        defaultLanguage = plugin.getConfig().getString("language", "auto");
        if (defaultLanguage.equals("auto")) {
            defaultLanguage = "en";
        }
    }
}