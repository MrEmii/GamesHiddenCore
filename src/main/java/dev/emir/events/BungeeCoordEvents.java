package dev.emir.events;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.emir.Main;
import dev.emir.models.GameInformation;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class BungeeCoordEvents implements PluginMessageListener {

    private final HashMap<String, GameInformation> gamesInformation;
    private final HashMap<String, Integer> servers;
    private final int globalCount = -1;

    public BungeeCoordEvents() {
        this.gamesInformation = new HashMap<>();
        servers = new HashMap<>();
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (channel.equals("BungeeCord")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String subchannel = in.readUTF();
            if (subchannel.equals("PlayerCount")) {
                String server = in.readUTF();
                int playercount = in.readInt();
                Main.getInstance().getBungeeCordListener().servers.put(server, playercount);
            }
            if (subchannel.equals("GetServers")) {
                String[] serverList = in.readUTF().split(", ");
                Arrays.stream(serverList).forEach(Main.getInstance().getBungeeCordListener()::addDefaultServer);
                Main.getInstance().getBungeeCordListener().allPlayers();
            }
        } else if (channel.equalsIgnoreCase("HiddenKiller")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String subchannel = in.readUTF();
            if (subchannel.equalsIgnoreCase("gameinfo")) {
                String info = in.readUTF();
                System.out.println(info);
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
        }


    }

    public int getPlayers() {
        int summer = -1;
        if (!Main.getInstance().getBungeeCordListener().servers.isEmpty()) {
            summer = Main.getInstance().getBungeeCordListener().servers.values().stream().mapToInt(integer -> integer >= 0 ? integer : 0).sum();
        } else {
            this.allPlayers();
        }

        return summer;
    }

    public void addDefaultServer(String server) {
        this.servers.putIfAbsent(server, -1);
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

    public void allPlayers() {

        if (this.servers.isEmpty()) {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);

            try {
                out.writeUTF("GetServers");
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            Main.getInstance().getServer().sendPluginMessage(Main.getInstance(), "BungeeCord", b.toByteArray());
        } else {
            this.servers.keySet().forEach(Main.getInstance().getBungeeCordListener()::playerCount);
        }

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

    public HashMap<String, Integer> getServers() {
        return servers;
    }

    @Override
    public String toString() {
        return "BungeeCoordEvents{" +
                "gamesInformation=" + gamesInformation +
                ", servers=" + servers +
                '}';
    }

    public void sendPacket() {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("gameadd");
            out.writeUTF("game");
            out.writeUTF("prueba");
            out.writeUTF("SI; ES UNA PRUEBA");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Main.getInstance().getServer().sendPluginMessage(Main.getInstance(), "HiddenKiller", b.toByteArray());

    }
}
