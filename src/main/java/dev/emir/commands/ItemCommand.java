package dev.emir.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (command.getName().equalsIgnoreCase("item")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (player.isOp() || player.hasPermission("gh.cmd.item")) {
                    if (args.length > 0) {
                        Material material = null;
                        boolean variant = false;
                        try {
                            int materialNumber = Integer.parseInt(args[0]);
                            material = Material.getMaterial(materialNumber);
                        } catch (NumberFormatException e) {
                            if (args[0].contains(":")) {
                                variant = true;
                                int materialNumber = Integer.parseInt(args[0].split(":")[0]);
                                material = Material.getMaterial(materialNumber);
                            } else {
                                if (Material.getMaterial(args[0].toUpperCase()) != null) {
                                    material = Material.getMaterial(args[0].toUpperCase());
                                } else {
                                    player.sendMessage(ChatColor.RED + "El material no existe");
                                    return true;
                                }
                            }
                        }
                        if (material != null) {
                            ItemStack item = new ItemStack(material, 1, variant ? (byte) Integer.parseInt(args[0].split(":")[1]) : 0);
                            ItemMeta itemMeta = item.getItemMeta();
                            if (args.length > 1 && args[1] != null) item.setAmount(Integer.parseInt(args[1]));
                            if (args.length > 2 && args[2] != null) itemMeta.setDisplayName(args[2]);
                            if (args.length >= 3) {
                                List<String> arguments = Arrays.asList(args.clone()).subList(3, args.length);
                                arguments = splitLore(String.join(" ", arguments));
                                itemMeta.setLore(arguments);
                            }
                            item.setItemMeta(itemMeta);

                            player.getInventory().addItem(item);
                        } else {
                            player.sendMessage(ChatColor.RED + "Material no encontrado");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Uso erroneo: /item <material> [cantidad] [name] [lore]!");
                    }
                }
            }
        }


        return true;
    }

    public List<String> splitLore(String text) {
        text = ChatColor.translateAlternateColorCodes('&', text);
        List<String> strings = new ArrayList<String>();
        int index = 0;
        while (index < text.length()) {
            strings.add(text.substring(index, Math.min(index + 16, text.length())));
            index += 16;
        }
        return strings;
    }
}
