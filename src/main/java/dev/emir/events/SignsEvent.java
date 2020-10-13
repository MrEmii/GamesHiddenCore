package dev.emir.events;

import com.mongodb.client.MongoCollection;
import dev.emir.Main;
import dev.emir.models.GameInformation;
import dev.emir.models.PlayerModel;
import dev.emir.models.SignsModel;
import dev.emir.utils.Encrypter;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class SignsEvent implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        if (player.isOp() || player.hasPermission("gh.sign")) {
            if (event.getLine(0).equalsIgnoreCase("game")) {
                SignsModel model = Main.getInstance().getSignsManager().getSign(event.getLine(1));
                if (model != null) {
                    Main.getInstance().getBungeeCordListener().gameInformation(model.getName());
                    model.getStringLocations().add(Encrypter.LocationToString(event.getBlock().getState().getLocation()));
                    GameInformation information = Main.getInstance().getBungeeCordListener().getGamesInformation().get(model.getName());
                    //String time = Main.getInstance().messages.getString("formats.time").replace("{minutes}", game.getTimer().formatTime().split(":")[0]).replace("{seconds}", game.getTimer().formatTime().split(":")[1]);
                    if (information != null) {
                        for (int i = 0; i < model.getLines().size(); i++) {
                            String line = model.getLines().get(i).replace("&", "§")
                                    .replace("{state}", information.getState())
                                    .replace("{spawns}", (information.getCurrentPlayers() + "/" + information.getMaxPlayers()));
                            event.setLine(i, line);
                        }
                    }
                } else {
                    player.sendMessage("&cEl juego no está registrado.");
                }
            }
        }
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        PlayerModel model = Main.getInstance().getPlayerManager().get(player.getUniqueId().toString());

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getClickedBlock().getType() == Material.WALL_SIGN || e.getClickedBlock().getType() == Material.SIGN_POST) {
                Sign signblock = (Sign) e.getClickedBlock().getState();

            }
        }

    }

}
