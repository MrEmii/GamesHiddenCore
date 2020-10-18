package dev.emir.commands;

import com.google.common.primitives.Ints;
import dev.emir.Main;
import dev.emir.utils.ChatCreator;
import dev.emir.utils.ColorText;
import dev.emir.utils.command.Command;
import dev.emir.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GHCommands {

    private Main plugin = Main.getInstance();

    private HashMap<String, Integer> trys = new HashMap<>();
    private HashMap<String, BukkitTask> tasks = new HashMap<>();

    @Command(name = "uwu", aliases = {"awa", "ewe", "twt"}, inGameOnly = true)
    public void uwu(CommandArgs args) {
        Player player = args.getPlayer();
        int trigger = trys.getOrDefault(player.getUniqueId().toString(), 0);
        if (tasks.containsKey(player.getUniqueId().toString())) {
            tasks.get(player.getUniqueId().toString()).cancel();
        }
        if (trigger == 0) {
            player.sendMessage(ColorText.translate("&a" + args.getLabel()));
        } else if (trigger == 1) {
            player.sendMessage(ColorText.translate("&2" + args.getLabel()));
        } else if (trigger == 2) {
            player.sendMessage(ColorText.translate("&2&l" + args.getLabel()));
        } else if (trigger == 3) {
            player.sendMessage(ColorText.translate("&2&l" + args.getLabel().toUpperCase()));
        } else if (trigger == 4) {
            player.sendMessage(ColorText.translate("&c" + args.getLabel().toUpperCase()));
        } else if (trigger == 5) {
            player.sendMessage(ColorText.translate("&c&l" + args.getLabel().toUpperCase()));
        } else if (trigger == 6) {
            player.sendMessage(ColorText.translate("&4" + args.getLabel().toUpperCase()));
        } else if (trigger == 7) {
            player.sendMessage(ColorText.translate("&4&l" + args.getLabel().toUpperCase()));
        } else if (trigger == 8) {
            player.sendMessage(ColorText.translate("&4&l" + String.join(" ", args.getLabel().split("(?!^)"))));
        } else if (trigger == 9) {
            player.sendMessage(ColorText.translate("&4&l" + String.join("   ", args.getLabel().split("(?!^)"))));
        } else if (trigger >= 20) {
            for (int i = 0; i < 100; i++) {
                player.sendMessage(ColorText.translate("&4&l" + String.join("            ", args.getLabel().split("(?!^)"))));
            }
            trys.put(player.getUniqueId().toString(), 0);
            return;
        }

        BukkitTask task = Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                trys.put(player.getUniqueId().toString(), 0);
            }
        }, 20);

        tasks.put(player.getUniqueId().toString(), task);


        trys.put(player.getUniqueId().toString(), trigger + 1);
    }

    @Command(name = "gh", aliases = {"gameshiddencore", "core", "hub"}, permission = "gh.op", inGameOnly = true)
    public void gamesHidden(CommandArgs args) {
        Player p = (Player) args.getSender();
        if (args.getArgs().length > 0) {
            switch (args.getArgs(0)) {
                case "setlobby":
                    double x = p.getLocation().getX();
                    double y = p.getLocation().getY();
                    double z = p.getLocation().getZ();
                    Main.getInstance().getConfig().set("spawn.world", p.getLocation().getWorld().getName());
                    Main.getInstance().getConfig().set("spawn.x", p.getLocation().getX());
                    Main.getInstance().getConfig().set("spawn.y", p.getLocation().getY());
                    Main.getInstance().getConfig().set("spawn.z", p.getLocation().getZ());
                    Main.getInstance().getConfig().set("spawn.yaw", p.getLocation().getYaw());
                    Main.getInstance().getConfig().set("spawn.pitch", p.getLocation().getPitch());
                    Main.getInstance().saveConfig();
                    p.sendMessage(ChatColor.GREEN + "Se situo el lobby en tu ubicacion");
                    p.sendMessage(ChatColor.GREEN + "Coordenadas:" + ChatColor.RESET + x + y + z);
                    break;
                case "reload":
                    Main.getInstance().reloadConfig();
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        Main.getInstance().getScoreboardDataHandler().reloadData(player);
                    });
                    p.sendMessage(ColorText.translate("&aSe ha actualizado la configuracion del plugin."));
                    break;
                case "pandi":
                    p.sendMessage(ColorText.translate("&aPuede ser que Pandi me guste... puede ser..."));
                    break;
                default:
                    p.sendMessage("");
                    break;
            }
        } else {
            p.sendMessage(ColorText.translate(getHelpMessage().toString()));
        }
    }

    public StringBuilder getHelpMessage() {

        StringBuilder msg = new StringBuilder("&5&lGames&d&lHidden &6Core\n");
        msg.append("&a&m---------------------------------------------------\n");
        msg.append("&a[] - Opcional <> - Obligatorio\n");
        msg.append("&6Comandos &aGenerales\n");
        msg.append("\n");
        msg.append("&b/gh setlobby - Comando para setear el spawn\n");
        msg.append("&b/gh reload - Comando para actualizar configuracion\n");
        msg.append("&6Otros &aComandos\n");
        msg.append("&b/broadcast <mensaje...> - &7Envia un mensaje a toda la comunidad en tu lobby actual\n");
        msg.append("&b/clearchat - &7Haz un trabajo extra y limpia el chat de todo el servidor\n");
        msg.append("&b/enchant <encantamiento> <nivel> - &7Encanta el item que tienes en tu mano al nivel que quieras.\n");
        msg.append("&7Encantamientos disponibles: ").append(Stream.of(Enchantment.values()).map(enchantment -> enchantment.getName().toLowerCase()).collect(Collectors.joining(", "))).append("\n");
        msg.append("&b/skull [usuario] - &7Obtén tu cabeza o la de alguien mas.\n");
        msg.append("&b/invsee [usuario] - &7Revisa el inventario del usuario que desees.\n");
        msg.append("&b/gm <modo de juego> - &7Cambiate la modalidad de juego a la que desees.\n");
        msg.append("&7Modalidades disponibles: ").append("survival, creative, adventure, spectator").append("&7puedes utilizar el numero de la posicion \n");
        msg.append("&6Permisos\n");
        msg.append("&7Todos los permisos son automticamente dados al usuario con OP\n");
        msg.append("&bgh.drop - &7Permitir dropear items\n");
        msg.append("&bgh.break - &7Permitir romper bloques\n");
        msg.append("&bgh.place - &7 Permite poner bloques\n");
        msg.append("&bgh.op - &7Permite comandos de /gh y colocar carteles para mapas\n");
        msg.append("&a&m---------------------------------------------------\n");

        return msg;
    }

    @Command(name = "teleporthere", aliases = {"tphere", "s", "tpall"}, permission = "gh.staff", inGameOnly = true)
    public void teleportHere(CommandArgs args) {
        Player player = args.getPlayer();
        String[] argz = args.getArgs();
        if (argz.length < 1) {
            player.sendMessage(ColorText.translate("&cUso: /" + args.getLabel() + " <playerName/all>"));
        } else {
            if (argz[0].equalsIgnoreCase("*") || argz[0].equalsIgnoreCase("all")) {
                int i = 0;
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (online.equals(player)) {
                        continue;
                    }
                    online.teleport(player);
                    i++;
                }
                if (i < 1) {
                    player.sendMessage(ColorText.translate("&cNo hay jugadores conectados."));
                } else {
                   player.sendMessage(ColorText.translate("&6Todos fueron teletransportado hacia ti."));
                }
                return;
            }
            Player target = Bukkit.getPlayer(argz[0]);
            if (target == null || !target.isOnline()) {
                player.sendMessage(String.format("&cEl jugador no existe!", argz[0]));
                return;
            }
            if (target.equals(player)) {
                return;
            }
            String message = "&cError al intentar tp...";
            if (target.teleport(player)) {
                message = "&6Teletransportado a " + target.getDisplayName() + " &6hacia tu ubicacion.";
            }
            player.sendMessage(ColorText.translate(message));

        }
    }

    @Command(name = "broadcast", aliases = {"bc"})
    public void broadcast(CommandArgs args) {
        CommandSender sender = args.getSender();
        if (args.getArgs().length < 1) {
            sender.sendMessage(ColorText.translate("&Uso: /" + args.getLabel() + " <texto...>"));
        } else {
            Bukkit.broadcastMessage(ColorText.translate(String.join(" ", args.getArgs())));
        }
    }

    @Command(name = "gamemode", aliases = {"gm"}, permission = "gh.staff", inGameOnly = true)
    public void gamemode(CommandArgs args) {
        if (args.getSender() instanceof Player) {
            Player p = args.getPlayer();
            if (args.getArgs().length >= 1) {
                GameMode mode = getGameMode(args.getArgs(0));
                if (mode != null) {
                    if (args.getArgs().length == 2) {
                        Player player = Bukkit.getPlayer(args.getArgs(1));
                        if (player != null) {
                            player.setGameMode(mode);
                            p.sendMessage(ColorText.translate("&aSe cambio el modo de juego de " + player.getDisplayName() + " &aa " + mode.name()));
                        } else {
                            p.sendMessage(ColorText.translate("&cEl jugador no está conectado"));
                        }

                    } else {
                        p.setGameMode(mode);
                        p.sendMessage(ColorText.translate("&aSe cambio el modo de juego a " + mode.name()));
                    }
                }
            } else {
                args.getSender().sendMessage(ColorText.translate("&cUso: /" + args.getLabel() + " <modo> [player]"));
            }

        }
    }

    public GameMode getGameMode(String id) {
        GameMode mode;
        try {
            int materialNumber = Integer.parseInt(id);
            mode = GameMode.getByValue(materialNumber);
        } catch (NumberFormatException e) {
            mode = GameMode.valueOf(id.toUpperCase());
        }

        return mode;
    }

    @Command(name = "clearchat", aliases = {"cc"}, permission = "gh.staff")
    public void clearChat(CommandArgs args) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.isOp()) {
                continue;
            }
            online.sendMessage(new String[120]);
        }
        args.getPlayer().sendMessage(ColorText.translate("&eEl chat ha sido limpiado"));
    }

    @Command(name = "test", aliases = {"tt"})
    public void conector(CommandArgs args) {
        Main.getInstance().getBungeeCordListener().sendPacket();
    }

    @Command(name = "skull", aliases = {"head"}, permission = "gh.staff", inGameOnly = true)
    public void skull(CommandArgs args) {
        String[] argz = args.getArgs();
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        if (argz.length < 1) {
            meta.setOwner(args.getPlayer().getName());
        } else {
            meta.setOwner(argz[0]);
        }
        item.setItemMeta(meta);

        args.getPlayer().getInventory().addItem(item);

        args.getPlayer().sendMessage(ColorText.translate(argz.length > 1 ? "&eRecibiste la cabeza de " + argz[0] + "." : "&eRecibiste tu cabeza."));
    }

    @Command(name = "enchant", inGameOnly = true, permission = "gh.staff")
    public void enchant(CommandArgs args) {
        Player player = args.getPlayer();

        if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
            player.sendMessage(ColorText.translate("&cNecestiar tener un item en tu mano."));
            return;
        }

        String[] argz = args.getArgs();
        if (argz.length < 2) {
            player.sendMessage(ColorText.translate("&cUso: /enchant <encantamiento> <nivel>"));
        } else {
            String enchantName = argz[0].toUpperCase().equalsIgnoreCase("sharpness") ? "DAMAGE_ALL" : argz[0].toUpperCase();
            Enchantment enchantment = Enchantment.getByName(enchantName);
            if (enchantment == null) {
                player.sendMessage(ColorText.translate("&cEl encantamiento no existe."));
                return;
            }
            Integer level = Ints.tryParse(argz[1]);
            if (level == null) {
                player.sendMessage(ColorText.translate("&cNivel invalido"));
            } else {
                player.getItemInHand().addUnsafeEnchantment(enchantment, level < 1 ? 1 : level);
                player.updateInventory();

                String name = (player.getItemInHand().getItemMeta().hasDisplayName() ? player.getItemInHand().getItemMeta().getDisplayName() : player.getItemInHand().getType().name());
                player.sendMessage(ColorText.translate("&6Encantamiento aplicado."));
            }
        }
    }

    @Command(name = "item", inGameOnly = true, permission = "gh.staff")
    public void item(CommandArgs argsz) {
        Player player = argsz.getPlayer();

        String[] args = argsz.getArgs();
        if (args.length > 0) {
            Material material = parseMaterial(args[0].contains(":") ? args[0].split(":")[0] : args[0]);
            int variant = args[0].contains(":") ? Integer.parseInt(args[0].split(":")[1]) : 0;

            if (material != Material.AIR) {
                ItemStack item = new ItemStack(material, 1, (byte) variant);
                ItemMeta itemMeta = item.getItemMeta();
                System.out.println(Arrays.toString(args));
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
            player.sendMessage(ChatColor.RED + "Uso: /item <material> [cantidad] [name] [lore]!");
        }
    }

    public Material parseMaterial(String id) {
        Material material;
        try {
            int materialNumber = Integer.parseInt(id);
            material = Material.getMaterial(materialNumber);
        } catch (NumberFormatException e) {
            material = Material.getMaterial(id.toUpperCase());
        }

        return material == null ? Material.AIR : material;
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

    @Command(name = "invsee", aliases = {"inventorysee", "invspect"}, permission = "gh.staff", inGameOnly = true)
    public void invSee(CommandArgs args) {
        Player player = args.getPlayer();
        String[] argz = args.getArgs();
        if (argz.length < 1) {
            args.getSender().sendMessage(ColorText.translate("&cUsage: /" + args.getLabel() + " <playerName>"));
        } else {
            Player target = Bukkit.getPlayer(argz[0]);
            if (target == null || !target.isOnline()) {
                args.getSender().sendMessage("El jugador no existe.");
                return;
            }
            player.openInventory(target.getInventory());
            player.sendMessage( ColorText.translate("&eAbriendo inventario de &b" + target.getName()));
        }
    }

}