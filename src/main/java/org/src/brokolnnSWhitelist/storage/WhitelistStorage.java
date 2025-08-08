package org.src.brokolnnSWhitelist.storage;

import java.util.List;
import java.util.Set;

public interface WhitelistStorage {
    boolean isWhitelisted(String name);
    void addPlayer(String name);
    void removePlayer(String name);
    List<String> getWhitelistedPlayers();
    default void reload() {} // ← добавляем
}
