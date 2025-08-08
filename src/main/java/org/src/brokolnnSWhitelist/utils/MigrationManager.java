package org.src.brokolnnSWhitelist.utils;

import org.bukkit.plugin.java.JavaPlugin;
import org.src.brokolnnSWhitelist.storage.WhitelistStorage;
import org.src.brokolnnSWhitelist.storage.StorageFactory;
import org.src.brokolnnSWhitelist.storage.FileStorage;
import org.src.brokolnnSWhitelist.storage.SQLiteStorage;
import org.src.brokolnnSWhitelist.storage.MySQLStorage;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class MigrationManager {

    private final JavaPlugin plugin;
    private final WhitelistStorage currentStorage;
    private final Logger logger;
    private final BackupManager backupManager;

    public MigrationManager(JavaPlugin plugin, WhitelistStorage currentStorage) {
        this.plugin = plugin;
        this.currentStorage = currentStorage;
        this.logger = plugin.getLogger();
        this.backupManager = new BackupManager(plugin);
    }

    public void checkAndMigrate() {
        String currentType = plugin.getConfig().getString("storage.type", "file");
        String lastType = plugin.getConfig().getString("storage.last-type", "none");

        // Если тип хранилища изменился, запускаем миграцию
        if (!currentType.equals(lastType) && !lastType.equals("none")) {
            logger.info("Обнаружено изменение типа хранилища с " + lastType + " на " + currentType);
            logger.info("Начинаем миграцию данных...");

            try {
                migrateFromTo(lastType, currentType);

                // Обновляем конфиг с новым типом
                plugin.getConfig().set("storage.last-type", currentType);
                plugin.saveConfig();

                logger.info("Миграция завершена успешно!");
            } catch (Exception e) {
                logger.severe("Ошибка при миграции: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // Сохраняем текущий тип для будущих проверок
            plugin.getConfig().set("storage.last-type", currentType);
            plugin.saveConfig();
        }
    }

    private void migrateFromTo(String fromType, String toType) throws Exception {
        // Создаём бэкап перед миграцией если настройка включена
        boolean createBackup = plugin.getConfig().getBoolean("migration.backup-before-migration", true);
        if (createBackup) {
            logger.info("Создаём бэкап перед миграцией...");
            backupManager.createBackup(currentStorage, "before_migration_" + fromType + "_to_" + toType);
        }

        // Создаём старое хранилище для чтения данных
        WhitelistStorage oldStorage = createStorageForType(fromType);

        // Получаем все данные из старого хранилища
        List<String> whitelistedPlayers = oldStorage.getWhitelistedPlayers();

        logger.info("Найдено " + whitelistedPlayers.size() + " игроков для миграции");

        // Очищаем новое хранилище и добавляем данные
        if (whitelistedPlayers.size() > 0) {
            for (String playerName : whitelistedPlayers) {
                currentStorage.addPlayer(playerName);
                logger.info("Мигрирован игрок: " + playerName);
            }
        }

        // Закрываем старое хранилище если нужно
        if (oldStorage instanceof MySQLStorage) {
            ((MySQLStorage) oldStorage).close();
        } else if (oldStorage instanceof SQLiteStorage) {
            ((SQLiteStorage) oldStorage).close();
        }

        logger.info("Миграция " + whitelistedPlayers.size() + " игроков завершена");
    }

    private WhitelistStorage createStorageForType(String type) {
        switch (type.toLowerCase()) {
            case "mysql":
                String host = plugin.getConfig().getString("mysql.host", "localhost");
                int port = plugin.getConfig().getInt("mysql.port", 3306);
                String database = plugin.getConfig().getString("mysql.database", "whitelist");
                String username = plugin.getConfig().getString("mysql.username", "root");
                String password = plugin.getConfig().getString("mysql.password", "password");
                return new MySQLStorage(host, port, database, username, password);

            case "sqlite":
                String sqliteFile = plugin.getConfig().getString("sqlite.file", "whitelist.db");
                return new SQLiteStorage(plugin.getDataFolder().getPath() + "/" + sqliteFile);

            case "file":
            default:
                return new FileStorage(plugin.getDataFolder());
        }
    }

    public void forceMigration(String fromType, String toType) {
        try {
            logger.info("Принудительная миграция с " + fromType + " на " + toType);
            migrateFromTo(fromType, toType);

            plugin.getConfig().set("storage.last-type", toType);
            plugin.saveConfig();

            logger.info("Принудительная миграция завершена!");
        } catch (Exception e) {
            logger.severe("Ошибка при принудительной миграции: " + e.getMessage());
            e.printStackTrace();
        }
    }
}