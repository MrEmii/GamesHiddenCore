package dev.emir.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerEvent implements Listener {

    @EventHandler
    public void playerBreakBlock(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!p.isOp() || !p.hasPermission("essentials.break")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerJoinEvent(){

    }

}
