package dev.emir.nametag.storage;

import dev.emir.nametag.api.data.PlayerData;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * This is responsible for abstracting
 * a database/flat file storage
 */
public interface AbstractConfig {

    void load();

    void reload();

    void shutdown();

    void load(Player player, boolean loggedIn);

    void save(PlayerData... playerData);

    void savePriority(boolean playerTag, String key, int priority);

    void clear(UUID uuid, String targetName);

}