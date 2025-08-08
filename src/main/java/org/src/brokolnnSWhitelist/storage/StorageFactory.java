package org.src.brokolnnSWhitelist.storage;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class StorageFactory {

    public static WhitelistStorage create(JavaPlugin plugin) {
        FileConfiguration config = plugin.getConfig();
        String type = config.getString("storage.type", "file").toLowerCase();

        switch (type) {
            case "mysql":
                String host = config.getString("mysql.host");
                int port = config.getInt("mysql.port");
                String database = config.getString("mysql.database");
                String username = config.getString("mysql.username");
                String password = config.getString("mysql.password");
                return new MySQLStorage(host, port, database, username, password);

            case "sqlite":
                String sqliteFile = config.getString("sqlite.file", "whitelist.db");
                return new SQLiteStorage(plugin.getDataFolder().getPath());

            case "file":
            default:
                return new FileStorage(plugin.getDataFolder());
        }
    }
}
