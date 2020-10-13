package dev.emir.commands;

import dev.emir.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if (cmd.getName().equalsIgnoreCase("setlobby")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Seteando ubicación en: 192.168.100.1");
            }
            final Player p = (Player) sender;
            if (p.hasPermission("gh.spawn") || p.isOp()) {
                final double x = p.getLocation().getX();
                final double y = p.getLocation().getY();
                final double z = p.getLocation().getZ();
                Main.getInstance().getConfig().set("spawn.world", (Object) p.getLocation().getWorld().getName());
                Main.getInstance().getConfig().set("spawn.x", (Object) p.getLocation().getX());
                Main.getInstance().getConfig().set("spawn.y", (Object) p.getLocation().getY());
                Main.getInstance().getConfig().set("spawn.z", (Object) p.getLocation().getZ());
                Main.getInstance().getConfig().set("spawn.yaw", (Object) p.getLocation().getYaw());
                Main.getInstance().getConfig().set("spawn.pitch", (Object) p.getLocation().getPitch());
                Main.getInstance().saveConfig();
                p.sendMessage(ChatColor.GREEN + "Listo el spawn estará en tu ubicación actual!");
                p.sendMessage(ChatColor.GREEN + "Coordenadas:"+ChatColor.RESET + x + y + z);
            }
        }
        return true;
    }
}
