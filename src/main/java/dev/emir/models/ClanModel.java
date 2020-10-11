package dev.emir.models;

import java.util.ArrayList;

public class ClanModel {

    private String name = "";
    private double level = 0;
    private double id = -1;
    private ArrayList<String> players = new ArrayList<>();
    private boolean own = false;
    private double createdAt = 0;


    public ClanModel() {

    }

    public ClanModel(String name, double level, double id, ArrayList<String> players, boolean own, double createdAt) {
        this.name = name;
        this.level = level;
        this.id = id;
        this.players = players;
        this.own = own;
        this.createdAt = createdAt;
    }

    //TODO: Clan logic
}
