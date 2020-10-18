package dev.emir.nametag;

import java.util.ArrayList;

import dev.emir.Main;
import dev.emir.nametag.api.INametagApi;
import dev.emir.nametag.api.NametagAPI;
import dev.emir.nametag.packets.PacketWrapper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class NametagEdit{

    private static INametagApi api;

    private NametagHandler handler;
    private NametagManager manager;

    public static INametagApi getApi() {
        return api;
    }

    public void onEnable() {
        testCompat();

        manager = new NametagManager();
        handler = new NametagHandler(Main.getInstance(), manager);

        if (api == null) {
            api = new NametagAPI();
        }

    }

    public void onDisable() {
        manager.reset();
        handler.getAbstractConfig().shutdown();
    }

    void debug(String message) {
        if (handler != null && handler.debug()) {
            Main.getInstance().getLogger().info("[DEBUG] " + message);
        }
    }

    private boolean checkShouldRegister(String plugin) {
        if (Bukkit.getPluginManager().getPlugin(plugin) == null) return false;
        Main.getInstance().getLogger().info("Found " + plugin + "! Hooking in.");
        return true;
    }

    private void testCompat() {
        PacketWrapper wrapper = new PacketWrapper("TEST", "&f", "", 0, new ArrayList<>());
        wrapper.send();
        if (wrapper.error == null) return;
        Bukkit.getPluginManager().disablePlugin(Main.getInstance());
    }

    public NametagHandler getHandler() {
        return handler;
    }

    public NametagManager getManager() {
        return manager;
    }
}