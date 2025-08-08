package org.src.brokolnnSWhitelist.storage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteStorage implements WhitelistStorage {

    private final String dbPath;
    private Connection connection;

    public SQLiteStorage(String pluginFolderPath) {
        this.dbPath = pluginFolderPath + "/whitelist.db";
        connect();
        createTableIfNotExists();
    }

    private void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath + "?journal_mode=WAL");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS whitelist (" +
                "name TEXT PRIMARY KEY COLLATE NOCASE, " +
                "added_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isWhitelisted(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT 1 FROM whitelist WHERE name = ? COLLATE NOCASE LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void addPlayer(String name) {
        if (name == null || name.trim().isEmpty()) {
            return;
        }

        String sql = "INSERT OR IGNORE INTO whitelist (name) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name.trim());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removePlayer(String name) {
        if (name == null || name.trim().isEmpty()) {
            return;
        }

        String sql = "DELETE FROM whitelist WHERE name = ? COLLATE NOCASE";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name.trim());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getWhitelistedPlayers() {
        List<String> players = new ArrayList<>();
        String sql = "SELECT name FROM whitelist ORDER BY name";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                players.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }

    @Override
    public void reload() {
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}