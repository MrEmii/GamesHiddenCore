package dev.emir.events;

import dev.emir.Main;
import dev.emir.models.PlayerModel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkUnloadEvent;


public class PlayerEvent implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoinAsync(AsyncPlayerPreLoginEvent e) {
        try {
            PlayerModel mPlayer = Main.getInstance().getPlayerManager().get(e.getUniqueId().toString());
            mPlayer.setLast_connection(Long.valueOf(System.currentTimeMillis()));
        } catch (Exception es) {
            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            e.setKickMessage(ChatColor.RED + "Ops... Who are you?");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        PlayerModel mPlayer = Main.getInstance().getPlayerManager().get(e.getPlayer().getUniqueId().toString()).setPlayer(e.getPlayer());

        for (int i = 0; i < 100; i++) {
            player.sendMessage("");
        }

        if (!Main.getInstance().getConfig().getString("spawn.world").equalsIgnoreCase("undefined")) {
            Location lobby = new Location(Bukkit.getWorld(Main.getInstance().getConfig().getString("spawn.world")), Main.getInstance().getConfig().getDouble("spawn.x"), Main.getInstance().getConfig().getDouble("spawn.y"), Main.getInstance().getConfig().getDouble("spawn.z"));
            lobby.setYaw((float) Main.getInstance().getConfig().getDouble("spawn.yaw"));
            lobby.setPitch((float) Main.getInstance().getConfig().getDouble("spawn.pitch"));

            player.teleport(lobby);
        } else {
            if (player.isOp())
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aUtiliza el comando &l/gh setlobby&a para setear el lobby!"));
        }
        for (final String msg : Main.getInstance().getConfig().getStringList("join-message")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg).replace("<player>", player.getName()));
        }
        try {
            mPlayer.save();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        e.setJoinMessage(null);

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        PlayerModel mPlayer = Main.getInstance().getPlayerManager().get(e.getPlayer().getUniqueId().toString()).setPlayer(e.getPlayer());
        try {
            mPlayer.setLast_server(Main.getInstance().getServer().getServerName());
            mPlayer.setLast_connection(System.currentTimeMillis());
            mPlayer.save();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        e.setQuitMessage(null);
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        PlayerModel mPlayer = Main.getInstance().getPlayerManager().get(e.getPlayer().getUniqueId().toString()).setPlayer(e.getPlayer());
        try {
            mPlayer.setLast_connection(System.currentTimeMillis());
            mPlayer.save();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        e.setLeaveMessage(null);
    }


    @EventHandler
    public void onPlayerAchievement(PlayerAchievementAwardedEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerHurt(EntityDamageEvent event) {
        if ((event.getEntity() instanceof Player) && !event.getEntity().isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerHurt(EntityDamageByEntityEvent event) {
        if (((event.getEntity() instanceof Player)) || ((event.getDamager() instanceof Player)) && !event.getEntity().isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHungerLoss(FoodLevelChangeEvent event) {
        if ((event.getEntity() instanceof Player)) {
            Player player = (Player) event.getEntity();
            player.setFoodLevel(20);
            player.setSaturation(10.0F);
        }
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void playerDropItemEvent(PlayerDropItemEvent event) {
        if (!event.getPlayer().isOp() || !event.getPlayer().hasPermission("gh.drop"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            event.getDrops().clear();
            event.setDroppedExp(0);
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerSuffocation(EntityDamageEvent event) {
        if (((event.getEntity() instanceof Player)) && (event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Location from = e.getFrom();
        Location to = e.getTo();

        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        if (e.getTo().getY() <= -44) {
            if (!Main.getInstance().getConfig().getString("spawn.world").equalsIgnoreCase("undefined")) {
                Location lobby = new Location(Bukkit.getWorld(Main.getInstance().getConfig().getString("spawn.world")), Main.getInstance().getConfig().getDouble("spawn.x"), Main.getInstance().getConfig().getDouble("spawn.y"), Main.getInstance().getConfig().getDouble("spawn.z"));
                lobby.setYaw((float) Main.getInstance().getConfig().getDouble("spawn.yaw"));
                lobby.setPitch((float) Main.getInstance().getConfig().getDouble("spawn.pitch"));

                e.setTo(lobby);
            }

        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerFallIntoVoid(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (player.getLocation().getBlock().getY() < -44) {
                if (!Main.getInstance().getConfig().getString("spawn.world").equalsIgnoreCase("undefined")) {
                    Location lobby = new Location(Bukkit.getWorld(Main.getInstance().getConfig().getString("spawn.world")), Main.getInstance().getConfig().getDouble("spawn.x"), Main.getInstance().getConfig().getDouble("spawn.y"), Main.getInstance().getConfig().getDouble("spawn.z"));
                    lobby.setYaw((float) Main.getInstance().getConfig().getDouble("spawn.yaw"));
                    lobby.setPitch((float) Main.getInstance().getConfig().getDouble("spawn.pitch"));
                    player.teleport(lobby);
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        if (!event.getPlayer().isOp() || !event.getPlayer().hasPermission("gh.break") && event.getPlayer().getGameMode() != GameMode.CREATIVE)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        if (!event.getPlayer().isOp() || !event.getPlayer().hasPermission("gh.place") && event.getPlayer().getGameMode() != GameMode.CREATIVE)
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerFishEvent(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            event.setExpToDrop(0);
            event.setCancelled(true);
        }
    }

}
