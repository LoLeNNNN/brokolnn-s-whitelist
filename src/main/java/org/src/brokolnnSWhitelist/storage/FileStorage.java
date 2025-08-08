package org.src.brokolnnSWhitelist.storage;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FileStorage implements WhitelistStorage {

    private final File file;
    private final YamlConfiguration config;
    private final Set<String> whitelistedNames = new HashSet<>();

    public FileStorage(File pluginFolder) {
        this.file = new File(pluginFolder, "whitelist.yml");

        if (!file.exists()) {
            try {
                if (!pluginFolder.exists()) {
                    pluginFolder.mkdirs();
                }
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private void load() {
        try {
            List<String> names = config.getStringList("whitelist");
            whitelistedNames.clear();

            // Нормализуем все имена к нижнему регистру для корректного сравнения
            for (String name : names) {
                if (name != null && !name.trim().isEmpty()) {
                    whitelistedNames.add(name.toLowerCase().trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void save() {
        try {
            // Сохраняем оригинальный регистр имён
            List<String> namesToSave = new ArrayList<>();
            for (String name : whitelistedNames) {
                namesToSave.add(name);
            }

            config.set("whitelist", namesToSave);
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isWhitelisted(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return whitelistedNames.contains(name.toLowerCase().trim());
    }

    @Override
    public void addPlayer(String name) {
        if (name == null || name.trim().isEmpty()) {
            return;
        }

        String normalizedName = name.toLowerCase().trim();
        if (whitelistedNames.add(normalizedName)) {
            save();
        }
    }

    @Override
    public void removePlayer(String name) {
        if (name == null || name.trim().isEmpty()) {
            return;
        }

        String normalizedName = name.toLowerCase().trim();
        if (whitelistedNames.remove(normalizedName)) {
            save();
        }
    }

    @Override
    public List<String> getWhitelistedPlayers() {
        return new ArrayList<>(whitelistedNames);
    }

    @Override
    public void reload() {
        load();
    }
}