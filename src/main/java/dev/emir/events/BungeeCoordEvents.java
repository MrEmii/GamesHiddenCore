package dev.emir.events;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import dev.emir.Main;
import dev.emir.models.GameInformation;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class BungeeCoordEvents implements PluginMessageListener {

    private HashMap<String, GameInformation> gamesInformation;

    public BungeeCoordEvents() {
        this.gamesInformation = new HashMap<>();
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (channel.equals("BungeeCord")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String subchannel = in.readUTF();
            if (subchannel.equals("PlayerCount")) {
                String server = in.readUTF();
                int playercount = in.readInt();
                System.out.println(server + playercount);
            }
        } else if (channel.equalsIgnoreCase("HiddenKiller")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String subchannel = in.readUTF();
            if (subchannel.equalsIgnoreCase("gameinfo")) {
                String info = in.readUTF();
                GameInformation model = Main.gson.fromJson(info, GameInformation.class);
                this.gamesInformation.put(model.getName(), model);
            }
            if (subchannel.equalsIgnoreCase("gameupdate")) {
                String info = in.readUTF();
                GameInformation model = Main.gson.fromJson(info, GameInformation.class);
                Main.getInstance().getSignsManager().getSigns().forEach((s, signsModel) -> {
                    signsModel.getLocation().forEach(location -> {
                        Sign signblock = (Sign) location.getBlock().getState();
                        for (int i = 0; i < signsModel.getLines().size(); i++) {
                            String line = signsModel.getLines().get(i).replace("&", "§")
                                    .replace("{state}", model.getState())
                                    .replace("{spawns}", (model.getCurrentPlayers() + "/" + model.getMaxPlayers()));
                            signblock.setLine(i, line);
                        }
                    });
                });
                this.gamesInformation.put(model.getName(), model);
            }
        } else
            return;


    }

    public void connect(String server, Player player) {

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        player.sendPluginMessage(Main.getInstance(), "BungeeCord", b.toByteArray());
    }

    public void playerCount(String server) {

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("PlayerCount");
            out.writeUTF(server);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Main.getInstance().getServer().sendPluginMessage(Main.getInstance(), "BungeeCord", b.toByteArray());
    }

    public void gameInformation(String gameName) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("gameinfo");
            out.writeUTF(gameName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Main.getInstance().getServer().sendPluginMessage(Main.getInstance(), "HiddenKiller", b.toByteArray());
    }

    public HashMap<String, GameInformation> getGamesInformation() {
        return gamesInformation;
    }
}
