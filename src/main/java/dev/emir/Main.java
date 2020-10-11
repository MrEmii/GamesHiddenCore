package dev.emir;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class Main extends JavaPlugin {

    public static Main instance;

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {

        } else {
            getLogger().warning("No se encontró PlaceholderAPI");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public void registerEvents(Listener... events) {
        Arrays.stream(events).forEach(event -> getServer().getPluginManager().registerEvents(event, this));
    }

    @Override
    public void onDisable() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            mongodb.disconnect();
        }
    }

    public static Main getInstance() {
        return instance;
    }

    public Mongod getMongodb() {
        return mongodb;
    }
}
