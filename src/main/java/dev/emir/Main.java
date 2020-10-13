package dev.emir;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.emir.commands.ItemCommand;
import dev.emir.commands.SpawnCommand;
import dev.emir.commands.TpallCommands;
import dev.emir.db.Mongod;
import dev.emir.events.BungeeCoordEvents;
import dev.emir.events.PlayerEvent;
import dev.emir.events.SignsEvent;
import dev.emir.managers.PlayerManager;
import dev.emir.managers.SignsManager;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class Main extends JavaPlugin {

    public static Main instance;
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Mongod mongodb;
    private PlayerManager playerManager;
    private LuckPerms luckPerms;
    private BungeeCoordEvents bungeeCordListener;
    private SignsManager signsManager;

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            instance = this;

            this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", bungeeCordListener = new BungeeCoordEvents());
            this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            this.getServer().getMessenger().registerIncomingPluginChannel(this, "HiddenKiller", bungeeCordListener = new BungeeCoordEvents());
            this.getServer().getMessenger().registerOutgoingPluginChannel(this, "HiddenKiller");

            this.getConfig().options().copyDefaults(true);
            this.saveDefaultConfig();

            this.mongodb = new Mongod();

            RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
            if (provider != null) {
                luckPerms = provider.getProvider();
            }

            this.signsManager = new SignsManager(this.mongodb.getCollection("signs"));
            this.playerManager = new PlayerManager(this.mongodb.getCollection("globalusers"));

            registerEvents(new PlayerEvent(), new SignsEvent());

            this.registerCommands();

            getLogger().fine("HubCore funcionando");
            getLogger().fine(this.mongodb == null ? "No se pudo conectar con mongoDB" : "MongoDB Conectado");
        } else {
            getLogger().warning("No se encontró PlaceholderAPI");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public void registerCommands() {
        this.getCommand("setlobby").setExecutor(new SpawnCommand());
        this.getCommand("item").setExecutor(new ItemCommand());
        this.getCommand("tpall").setExecutor(new TpallCommands());
    }

    public void registerEvents(Listener... events) {
        Arrays.stream(events).forEach(event -> getServer().getPluginManager().registerEvents(event, this));
    }

    @Override
    public void onDisable() {
        mongodb.disconnect();
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

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public SignsManager getSignsManager() {
        return signsManager;
    }

    public BungeeCoordEvents getBungeeCordListener() {
        return bungeeCordListener;
    }
}
