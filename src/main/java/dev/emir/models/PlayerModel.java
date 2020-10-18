package dev.emir.models;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mongodb.client.model.Filters;
import dev.emir.Main;
import dev.emir.utils.ColorText;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.*;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Stream;

public class PlayerModel {

    private ArrayList<ConditionalModel> friends = new ArrayList<>();
    private ArrayList<String> trophy = new ArrayList<>();
    private PartyModel party = new PartyModel();
    private ClanModel clan = new ClanModel();
    private String last_server = "lobby";
    private double last_connection = 0;
    private String language = "es_mx";
    private int real_money = 0;
    private String username = "";
    private int level = 0;
    private String uuid = "";
    private double xp = 0;

    @BsonIgnore
    private transient Player player;

    public PlayerModel set(String last_server, String username, String uuid) {
        this.last_server = last_server;
        this.username = username;
        this.uuid = uuid;
        this.player = Bukkit.getPlayer(uuid);
        return this;
    }

    public PlayerModel set(String last_server, Player player) {
        this.last_server = last_server;
        this.username = player.getName();
        this.uuid = player.getUniqueId().toString();
        this.player = player;
        return this;
    }


    public ArrayList<ConditionalModel> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<ConditionalModel> friends) {
        this.friends = friends;
    }

    public ArrayList<String> getTrophy() {
        return trophy;
    }

    public void setTrophy(ArrayList<String> trophy) {
        this.trophy = trophy;
    }

    public PartyModel getParty() {
        return party;
    }

    public void setParty(PartyModel party) {
        this.party = party;
    }

    public ClanModel getClan() {
        return clan;
    }

    public void setClan(ClanModel clan) {
        this.clan = clan;
    }

    public String getLast_server() {
        return last_server;
    }

    public void setLast_server(String last_server) {
        this.last_server = last_server;
    }

    public double getLast_connection() {
        return last_connection;
    }

    public void setLast_connection(double last_connection) {
        this.last_connection = last_connection;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getReal_money() {
        return real_money;
    }

    public void setReal_money(int real_money) {
        this.real_money = real_money;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getUuid() {
        return uuid;
    }

    public PlayerModel setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public double getXp() {
        return xp;
    }

    public void setXp(double xp) {
        this.xp = xp;
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerModel setPlayer(Player player) {
        this.player = player;
        this.username = player.getName();
        return this;
    }

    public static void changeNameForTest(Player player) {
        String prefix = PlaceholderAPI.setPlaceholders(player, "%luckperms_prefix%");
        String colr = ColorText.translate(prefix.replaceAll(" ", "") + " &k1&r");
        GameProfile gp = ((CraftPlayer) player).getProfile();
        System.out.println(colr.length());
        System.out.println(player.getDisplayName().length());
        System.out.println(player.getName().length());
        try {
            Field nameField = GameProfile.class.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(gp, colr);
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            throw new IllegalStateException(ex);
        }
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(player.getEntityId()));
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) player).getHandle()));
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) player).getHandle()));
        Bukkit.getOnlinePlayers().forEach(pl -> {
            if (pl != player)
                ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(((CraftPlayer) player).getHandle()));
        });
    }

    public void reload() {
        PlayerModel model = Main.gson.fromJson(Main.getInstance().getMongodb().getCollection("production-users").find(Filters.eq("uuid", this.uuid)).first().toJson(), this.getClass());
        Stream.of(model.getClass().getDeclaredFields()).forEach(field -> {
            field.setAccessible(true);
            try {
                System.out.println(field.get(model));
                this.getClass().getDeclaredField(field.getName()).set(this, field.get(model));
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        });
    }

    public void debug() {
        Stream.of(this.getClass().getDeclaredFields()).forEach(field -> {
            field.setAccessible(true);
            try {
                System.out.println(field.get(this));
                System.out.println(field.getName());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    public void save() {
        Main.getInstance().getMongodb().replace("production-users", "uuid", this.uuid, Document.parse(Main.gson.toJson(this)));
    }

}
