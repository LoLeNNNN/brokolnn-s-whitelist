package org.src.brokolnnSWhitelist;

import org.bukkit.plugin.java.JavaPlugin;
import org.src.brokolnnSWhitelist.commands.WhitelistCommand;
import org.src.brokolnnSWhitelist.listener.PlayerJoinListener;
import org.src.brokolnnSWhitelist.storage.StorageFactory;
import org.src.brokolnnSWhitelist.storage.WhitelistStorage;
import org.src.brokolnnSWhitelist.utils.Messages;
import org.src.brokolnnSWhitelist.utils.MigrationManager;

public class BrokolnnSWhitelist extends JavaPlugin {

    private WhitelistStorage storage;
    private MigrationManager migrationManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        saveResource("locales/messages_en.yml", false);
        saveResource("locales/messages_ru.yml", false);

        Messages.init(this);

        storage = StorageFactory.create(this);

        migrationManager = new MigrationManager(this, storage);

        migrationManager.checkAndMigrate();

        WhitelistCommand whitelistCommand = new WhitelistCommand(storage, this);
        getCommand("whitelist").setExecutor(whitelistCommand);
        getCommand("whitelist").setTabCompleter(whitelistCommand);

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(storage), this);

        getLogger().info("BrokolInnSWhitelist включён!");
    }

    @Override
    public void onDisable() {
        if (storage instanceof org.src.brokolnnSWhitelist.storage.MySQLStorage) {
            ((org.src.brokolnnSWhitelist.storage.MySQLStorage) storage).close();
        } else if (storage instanceof org.src.brokolnnSWhitelist.storage.SQLiteStorage) {
            ((org.src.brokolnnSWhitelist.storage.SQLiteStorage) storage).close();
        }
        getLogger().info("BrokolInnSWhitelist выключен.");
    }

    public WhitelistStorage getStorage() {
        return storage;
    }

    public MigrationManager getMigrationManager() {
        return migrationManager;
    }
}