package dev.emir.nametag;

import dev.emir.Main;
import dev.emir.nametag.api.data.INametag;
import dev.emir.nametag.api.data.PlayerData;
import dev.emir.nametag.api.events.NametagEvent;
import dev.emir.nametag.api.events.NametagFirstLoadedEvent;
import dev.emir.nametag.storage.AbstractConfig;
import dev.emir.nametag.storage.flatfile.FlatFileConfig;
import dev.emir.nametag.utils.UUIDFetcher;
import dev.emir.nametag.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class NametagHandler implements Listener {

    // Multiple threads access resources. We need to make sure we avoid concurrency issues.
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public static boolean DISABLE_PUSH_ALL_TAGS = false;
    private boolean debug;
    private boolean tabListEnabled;
    private boolean longNametagsEnabled;
    private boolean refreshTagOnWorldChange;

    private BukkitTask clearEmptyTeamTask;
    private BukkitTask refreshNametagTask;
    private AbstractConfig abstractConfig;

    private Map<UUID, PlayerData> playerData = new HashMap<>();

    private Main plugin;
    private NametagManager nametagManager;

    public NametagHandler(Main plugin, NametagManager nametagManager) {
        this.plugin = plugin;
        this.nametagManager = nametagManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);

        // Apply config properties
        this.applyConfig();

        abstractConfig = new FlatFileConfig(plugin, this);

        new BukkitRunnable() {
            @Override
            public void run() {
                abstractConfig.load();
            }
        }.runTaskAsynchronously(plugin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        nametagManager.reset(event.getPlayer().getName());
    }

    /**
     * Applies tags to a player
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        nametagManager.sendTeams(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                abstractConfig.load(player, true);
            }
        }.runTaskLaterAsynchronously(plugin, 1);
    }

    /**
     * Some users may have different permissions per world.
     * If this is enabled, their tag will be reloaded on TP.
     */
    @EventHandler
    public void onTeleport(final PlayerChangedWorldEvent event) {
        if (!refreshTagOnWorldChange) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                applyTagToPlayer(event.getPlayer(), false);
            }
        }.runTaskLater(plugin, 3);
    }

    private void handleClear(UUID uuid, String player) {
        removePlayerData(uuid);
        nametagManager.reset(player);
        abstractConfig.clear(uuid, player);
    }

    public void clearMemoryData() {
        try {
            readWriteLock.writeLock().lock();
            playerData.clear();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void removePlayerData(UUID uuid) {
        try {
            readWriteLock.writeLock().lock();
            playerData.remove(uuid);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void storePlayerData(UUID uuid, PlayerData data) {
        try {
            readWriteLock.writeLock().lock();
            playerData.put(uuid, data);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    // ==========================================
    // Below are methods used by the API/Commands
    // ==========================================
    boolean debug() {
        return debug;
    }


    // =================================================
    // Below are methods that we have to be careful with
    // as they can be called from different threads
    // =================================================
    public PlayerData getPlayerData(Player player) {
        return player == null ? null : playerData.get(player.getUniqueId());
    }

    /**
     * Replaces placeholders when a player tag is created.
     * Maxim and Clip's plugins are searched for, and input
     * is replaced. We use direct imports to avoid any problems!
     * (So don't change that)
     */
    public String formatWithPlaceholders(Player player, String input, boolean limitChars) {
        if (input == null) return "";
        if (player == null) return input;

        // The string can become null again at this point. Add another check.
        if (input != null && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            Main.getInstance().getNameTag().debug("Trying to use PlaceholderAPI for placeholders");
            input = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, input);
        }

        return Utils.format(input, limitChars);
    }

    private BukkitTask createTask(int path, BukkitTask existing, Runnable runnable) {
        if (existing != null) {
            existing.cancel();
        }

        if (path <= 0) return null;
        return Bukkit.getScheduler().runTaskTimer(plugin, runnable, 0, 20 * path);
    }

    public void reload() {
        applyConfig();
        nametagManager.reset();
        abstractConfig.reload();
    }

    private void applyConfig() {
        this.debug = false;
        this.tabListEnabled = true;
        this.longNametagsEnabled = true;
        this.refreshTagOnWorldChange = true;
        DISABLE_PUSH_ALL_TAGS = false;

        clearEmptyTeamTask = createTask(-1, clearEmptyTeamTask, new Runnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "nte teams clear");
            }
        });

        refreshNametagTask = createTask(0, refreshNametagTask, new Runnable() {
            @Override
            public void run() {
                nametagManager.reset();
                applyTags();
            }
        });
    }

    public void applyTags() {
        if (!Bukkit.isPrimaryThread()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    applyTags();
                }
            }.runTask(plugin);
            return;
        }

        for (Player online : Utils.getOnline()) {
            if (online != null) {
                applyTagToPlayer(online, false);
            }
        }

        Main.getInstance().getNameTag().debug("Applied tags to all online players.");
    }

    public void applyTagToPlayer(final Player player, final boolean loggedIn) {
        // If on the primary thread, run async
        if (Bukkit.isPrimaryThread()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    applyTagToPlayer(player, loggedIn);
                }
            }.runTaskAsynchronously(plugin);
            return;
        }

        INametag tempNametag = getPlayerData(player);

        if (tempNametag == null) return;
        plugin.getNameTag().debug("Applying " + (tempNametag.isPlayerTag() ? "PlayerTag" : "GroupTag") + " to " + player.getName());

        final INametag nametag = tempNametag;
        new BukkitRunnable() {
            @Override
            public void run() {
                nametagManager.setNametag(player.getName(), formatWithPlaceholders(player, nametag.getPrefix(), true),
                        formatWithPlaceholders(player, nametag.getSuffix(), true), nametag.getSortPriority());
                // If the TabList is disabled...
                if (!tabListEnabled) {
                    // apply the default white username to the player.
                    player.setPlayerListName(Utils.format("&f" + player.getPlayerListName()));
                } else {
                    if (longNametagsEnabled) {
                        player.setPlayerListName(formatWithPlaceholders(player, nametag.getPrefix() + player.getName() + nametag.getSuffix(), false));
                    } else {
                        player.setPlayerListName(null);
                    }
                }

                if (loggedIn) {
                    Bukkit.getPluginManager().callEvent(new NametagFirstLoadedEvent(player, nametag));
                }
            }
        }.runTask(plugin);
    }

    void clear(final CommandSender sender, final String player) {
        Player target = Bukkit.getPlayerExact(player);
        if (target != null) {
            handleClear(target.getUniqueId(), player);
            return;
        }

        UUIDFetcher.lookupUUID(player, plugin, new UUIDFetcher.UUIDLookup() {
            @Override
            public void response(UUID uuid) {
                if (uuid == null) {
                    System.out.println(" UUID no existe.");
                } else {
                    handleClear(uuid, player);
                }
            }
        });
    }

    void save(CommandSender sender, boolean playerTag, String key, int priority) {
        if (playerTag) {
            Player player = Bukkit.getPlayerExact(key);

            PlayerData data = getPlayerData(player);
            if (data == null) {
                abstractConfig.savePriority(true, key, priority);
                return;
            }

            data.setSortPriority(priority);
            abstractConfig.save(data);
        }
    }

    void save(final CommandSender sender, String targetName, NametagEvent.ChangeType changeType, String value) {
        Player player = Bukkit.getPlayerExact(targetName);

        PlayerData data = getPlayerData(player);
        if (data == null) {
            data = new PlayerData(targetName, null, "", "", -1);
            if (player != null) {
                storePlayerData(player.getUniqueId(), data);
            }
        }

        if (changeType == NametagEvent.ChangeType.PREFIX) {
            data.setPrefix(value);
        } else {
            data.setSuffix(value);
        }

        if (player != null) {
            applyTagToPlayer(player, false);
            data.setUuid(player.getUniqueId());
            abstractConfig.save(data);
            return;
        }

        final PlayerData finalData = data;
        UUIDFetcher.lookupUUID(targetName, plugin, new UUIDFetcher.UUIDLookup() {
            @Override
            public void response(UUID uuid) {
                if (uuid == null) {
                    System.out.println("UUID DESCONOCIDO");
                } else {
                    storePlayerData(uuid, finalData);
                    finalData.setUuid(uuid);
                    abstractConfig.save(finalData);
                }
            }
        });
    }

    public ReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }

    public void setReadWriteLock(ReadWriteLock readWriteLock) {
        this.readWriteLock = readWriteLock;
    }

    public static boolean isDisablePushAllTags() {
        return DISABLE_PUSH_ALL_TAGS;
    }

    public static void setDisablePushAllTags(boolean disablePushAllTags) {
        DISABLE_PUSH_ALL_TAGS = disablePushAllTags;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isTabListEnabled() {
        return tabListEnabled;
    }

    public void setTabListEnabled(boolean tabListEnabled) {
        this.tabListEnabled = tabListEnabled;
    }

    public boolean isLongNametagsEnabled() {
        return longNametagsEnabled;
    }

    public void setLongNametagsEnabled(boolean longNametagsEnabled) {
        this.longNametagsEnabled = longNametagsEnabled;
    }

    public boolean isRefreshTagOnWorldChange() {
        return refreshTagOnWorldChange;
    }

    public void setRefreshTagOnWorldChange(boolean refreshTagOnWorldChange) {
        this.refreshTagOnWorldChange = refreshTagOnWorldChange;
    }

    public BukkitTask getClearEmptyTeamTask() {
        return clearEmptyTeamTask;
    }

    public void setClearEmptyTeamTask(BukkitTask clearEmptyTeamTask) {
        this.clearEmptyTeamTask = clearEmptyTeamTask;
    }

    public BukkitTask getRefreshNametagTask() {
        return refreshNametagTask;
    }

    public void setRefreshNametagTask(BukkitTask refreshNametagTask) {
        this.refreshNametagTask = refreshNametagTask;
    }

    public AbstractConfig getAbstractConfig() {
        return abstractConfig;
    }

    public void setAbstractConfig(AbstractConfig abstractConfig) {
        this.abstractConfig = abstractConfig;
    }

    public Map<UUID, PlayerData> getPlayerData() {
        return playerData;
    }

    public void setPlayerData(Map<UUID, PlayerData> playerData) {
        this.playerData = playerData;
    }

    public Main getPlugin() {
        return plugin;
    }

    public void setPlugin(Main plugin) {
        this.plugin = plugin;
    }

    public NametagManager getNametagManager() {
        return nametagManager;
    }

    public void setNametagManager(NametagManager nametagManager) {
        this.nametagManager = nametagManager;
    }
}