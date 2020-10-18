package dev.emir;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.emir.commands.GHCommands;
import dev.emir.db.Mongod;
import dev.emir.events.BungeeCoordEvents;
import dev.emir.events.PlayerEvent;
import dev.emir.events.SignsEvent;
import dev.emir.managers.PlayerManager;
import dev.emir.managers.SignsManager;
import dev.emir.models.PlayerModel;
import dev.emir.scoreboad.ScoreboardObject;
import dev.emir.scoreboad.ScoreboardObjectHandler;
import dev.emir.utils.ColorText;
import dev.emir.utils.command.CommandFramework;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;

import java.util.Arrays;

public class Main extends JavaPlugin {

    public static Main instance;
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Mongod mongodb;
    private PlayerManager playerManager;
    private BungeeCoordEvents bungeeCordListener;
    private SignsManager signsManager;
    private ScoreboardObjectHandler scoreboardDataHandler;

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            instance = this;

            this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", bungeeCordListener = new BungeeCoordEvents());
            this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            this.getServer().getMessenger().registerIncomingPluginChannel(this, "HiddenKiller", bungeeCordListener = new BungeeCoordEvents());
            this.getServer().getMessenger().registerOutgoingPluginChannel(this, "HiddenKiller");

            this.mongodb = new Mongod();
            this.scoreboardDataHandler = new ScoreboardObjectHandler();
            this.scoreboardDataHandler.enable();
            registerEvents(new PlayerEvent(), this.scoreboardDataHandler);
            this.signsManager = new SignsManager(this.mongodb.getCollection("signs"));
            this.playerManager = new PlayerManager(this.mongodb.getCollection("production-users"));

            this.setupScoreboard();
            this.setupCommands();

            this.getConfig().options().copyDefaults(true);
            this.saveDefaultConfig();

            getServer().getConsoleSender().sendMessage(ColorText.translate("&a&m------------------------------------"));
            getServer().getConsoleSender().sendMessage(ColorText.translate("&aHola HiddenGamer, bienvenido"));
            getServer().getConsoleSender().sendMessage(ColorText.translate(this.mongodb == null ? "6cNo se pudo conectar con mongoDB" : "&cMongoDB Conectado"));
            getServer().getConsoleSender().sendMessage(ColorText.translate("&a&m------------------------------------"));
        } else {
            getLogger().warning("No se encontró PlaceholderAPI");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        this.scoreboardDataHandler.reload();
        Bukkit.getOnlinePlayers().forEach(this.scoreboardDataHandler::reloadData);
    }

    public void setupScoreboard() {
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                getServer().getOnlinePlayers().forEach(player -> {
                    PlayerModel model = getPlayerManager().get(player.getUniqueId().toString());
                    model.debug();
                    ScoreboardObject scoreboard = scoreboardDataHandler.getScoreboardFor(player);
                    Main.getInstance().getBungeeCordListener().playerCount("ALL");
                    scoreboard.clear();
                    getConfig().getStringList("scoreboard.board").forEach(line -> {
                        line = line.replace("{username}", player.getDisplayName())
                                .replace("{balance}", String.valueOf(0))
                                .replace("{level}", String.valueOf(model.getLevel()))
                                .replace("{trophies}", String.valueOf(model.getTrophy().size()))
                                .replace("{lobby}", player.getServer().getServerName())
                                .replace("{players}", String.valueOf(Main.getInstance().getBungeeCordListener().getServers().get("ALL")));

                        line = PlaceholderAPI.setPlaceholders(player, line);
                        scoreboard.add(line);
                    });
                    scoreboard.update(player);
                });
            }
        }, 0L, 200L);
    }


    public void setupCommands() {
        CommandFramework framework = new CommandFramework(this);
        framework.registerCommands(new GHCommands());
        framework.registerHelp();
    }

    public void registerEvents(Listener... events) {
        Arrays.stream(events).forEach(event -> getServer().getPluginManager().registerEvents(event, this));
    }

    @Override
    public void onDisable() {
        mongodb.disconnect();
        Messenger messenger = Bukkit.getServer().getMessenger();
        messenger.unregisterIncomingPluginChannel(this, "BungeeCord", this.bungeeCordListener);
        messenger.unregisterOutgoingPluginChannel(this);
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public static Main getInstance() {
        return instance;
    }

    public Mongod getMongodb() {
        return mongodb;
    }

    public SignsManager getSignsManager() {
        return signsManager;
    }

    public BungeeCoordEvents getBungeeCordListener() {
        return bungeeCordListener;
    }

    public ScoreboardObjectHandler getScoreboardDataHandler() {
        return scoreboardDataHandler;
    }
}
