package dev.emir.commands;

import dev.emir.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpallCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("tpall")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.isOp() || player.hasPermission("gh.cmd.tp")) {
                    Main.getInstance().getServer().getOnlinePlayers().stream().forEach(players -> {
                        players.teleport(player);
                    });
                }
            }
        }
        return true;
    }
}
