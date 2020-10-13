package dev.emir.models;

import dev.emir.Main;
import dev.emir.utils.Encrypter;
import org.bson.Document;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class SignsModel {

    private String name = "";
    private List<String> lines = new ArrayList<>();
    private List<String> location = new ArrayList<>();

    public SignsModel(String name) {
        this.name = name;
    }

    public void save() {
        Main.getInstance().getMongodb().replace("signs", name, this.name, Document.parse(Main.gson.toJson(this)));
    }


    public String getName() {
        return name;
    }

    public List<String> getLines() {
        return lines;
    }


    public List<String> getStringLocations() {
        return this.location;
    }

    public List<Location> getLocation() {
        return Encrypter.StringToListLocation(this.location);
    }
}
