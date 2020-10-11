package dev.emir.models;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PlayerModel {

    private ArrayList<ConditionalModel> friends = new ArrayList<>();
    private ArrayList<String> trophy = new ArrayList<>();
    private PartyModel party = new PartyModel();
    private ClanModel clan = new ClanModel();
    private String last_server = "lobby";
    private double last_connection = 0;
    private String language = "es_mx";
    private String rank = "default";
    private double real_money = 0;
    private double loot_coins = 0;
    private String username = "";
    private double level = 0;
    private double money = 0;
    private String uuid = "";
    private double xp = 0;

    @BsonIgnore
    private transient Player player;

    public PlayerModel set(String last_server, String rank, String username, String uuid) {
        this.last_server = last_server;
        this.rank = rank;
        this.username = username;
        this.uuid = uuid;
        return this;
    }

    public PlayerModel set(String last_server, String rank, Player player) {
        this.last_server = last_server;
        this.rank = rank;
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

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public double getReal_money() {
        return real_money;
    }

    public void setReal_money(double real_money) {
        this.real_money = real_money;
    }

    public double getLoot_coins() {
        return loot_coins;
    }

    public void setLoot_coins(double loot_coins) {
        this.loot_coins = loot_coins;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getLevel() {
        return level;
    }

    public void setLevel(double level) {
        this.level = level;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public void setPlayer(Player player) {
        this.player = player;
    }
}
