package dev.emir.nametag.storage.flatfile;

import dev.emir.Main;
import dev.emir.nametag.NametagHandler;
import dev.emir.nametag.api.data.PlayerData;
import dev.emir.nametag.storage.AbstractConfig;
import dev.emir.nametag.utils.UUIDFetcher;
import dev.emir.nametag.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FlatFileConfig implements AbstractConfig {

    private File playersFile;

    private YamlConfiguration players;

    private Main plugin;
    private NametagHandler handler;

    public FlatFileConfig(Main plugin, NametagHandler handler) {
        this.plugin = plugin;
        this.handler = handler;
    }

    @Override
    public void load() {
        playersFile = new File(plugin.getDataFolder(), "players.yml");
        players = Utils.getConfig(playersFile, "players.yml", plugin);
        loadPlayers();

        new BukkitRunnable() {
            @Override
            public void run() {
                handler.applyTags();
            }
        }.runTask(plugin);
    }

    @Override
    public void reload() {
        handler.clearMemoryData();

        new BukkitRunnable() {
            @Override
            public void run() {
                load();
            }
        }.runTaskAsynchronously(plugin);
    }

    @Override
    public void shutdown() {
        // NOTE: Nothing to do
    }

    @Override
    public void load(Player player, boolean loggedIn) {
        loadPlayerTag(player);
        Main.getInstance().getNameTag().getHandler().applyTagToPlayer(player, loggedIn);
    }

    @Override
    public void save(PlayerData... data) {
        for (PlayerData playerData : data) {
            UUID uuid = playerData.getUuid();
            String name = playerData.getName();
            players.set("Players." + uuid + ".Name", name);
            players.set("Players." + uuid + ".Prefix", Utils.deformat(playerData.getPrefix()));
            players.set("Players." + uuid + ".Suffix", Utils.deformat(playerData.getSuffix()));
            players.set("Players." + uuid + ".SortPriority", playerData.getSortPriority());
        }

        save(players, playersFile);
    }

    @Override
    public void savePriority(boolean playerTag, String key, final int priority) {
        if (playerTag) {
            final Player target = Bukkit.getPlayerExact(key);
            if (target != null) {
                if (players.contains("Players." + target.getUniqueId().toString())) {
                    players.set("Players." + target.getUniqueId().toString(), priority);
                    save(players, playersFile);
                }
                return;
            }

            UUIDFetcher.lookupUUID(key, plugin, new UUIDFetcher.UUIDLookup() {
                @Override
                public void response(UUID uuid) {
                    if (players.contains("Players." + uuid.toString())) {
                        players.set("Players." + uuid.toString(), priority);
                        save(players, playersFile);
                    }
                }
            });
        }
    }


    @Override
    public void clear(UUID uuid, String targetName) {
        handler.removePlayerData(uuid);
        players.set("Players." + uuid.toString(), null);
        save(players, playersFile);
    }


    private void save(YamlConfiguration config, File file) {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPlayerTag(Player player) {
        PlayerData data = PlayerData.fromFile(player.getUniqueId().toString(), players);
        if (data != null) {
            data.setName(player.getName());
            handler.storePlayerData(player.getUniqueId(), data);
        }
    }

    private void loadPlayers() {
        for (Player player : Utils.getOnline()) {
            loadPlayerTag(player);
        }
    }

}