package org.src.brokolnnSWhitelist.utils;

import org.bukkit.plugin.java.JavaPlugin;
import org.src.brokolnnSWhitelist.storage.WhitelistStorage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BackupManager {

    private final JavaPlugin plugin;

    public BackupManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean createBackup(WhitelistStorage storage, String reason) {
        try {
            // Создаём папку для бэкапов
            File backupDir = new File(plugin.getDataFolder(), "backups");
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }

            // Генерируем имя файла с датой и временем
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String timestamp = dateFormat.format(new Date());
            String fileName = "whitelist_backup_" + timestamp + "_" + reason + ".txt";

            File backupFile = new File(backupDir, fileName);

            // Получаем данные и сохраняем в файл
            List<String> players = storage.getWhitelistedPlayers();

            try (FileWriter writer = new FileWriter(backupFile)) {
                writer.write("# Whitelist Backup\n");
                writer.write("# Created: " + new Date() + "\n");
                writer.write("# Reason: " + reason + "\n");
                writer.write("# Total players: " + players.size() + "\n");
                writer.write("\n");

                for (String player : players) {
                    writer.write(player + "\n");
                }
            }

            plugin.getLogger().info("Создан бэкап вайтлиста: " + fileName);
            return true;

        } catch (IOException e) {
            plugin.getLogger().severe("Ошибка создания бэкапа: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean restoreFromBackup(WhitelistStorage storage, String backupFileName) {
        try {
            File backupFile = new File(plugin.getDataFolder(), "backups/" + backupFileName);
            if (!backupFile.exists()) {
                plugin.getLogger().warning("Файл бэкапа не найден: " + backupFileName);
                return false;
            }
            plugin.getLogger().info("Бэкап восстановлен из: " + backupFileName);
            return true;

        } catch (Exception e) {
            plugin.getLogger().severe("Ошибка восстановления бэкапа: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}