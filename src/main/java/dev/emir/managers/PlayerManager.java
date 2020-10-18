package dev.emir.managers;

import com.mongodb.client.MongoCollection;
import dev.emir.Main;
import dev.emir.models.PlayerModel;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.stream.Stream;

import static com.mongodb.client.model.Filters.*;

public class PlayerManager {

    MongoCollection<Document> users;
    HashMap<String, PlayerModel> models;

    public PlayerManager(MongoCollection<Document> users) {
        this.users = users;
        this.models = new HashMap<>();
    }

    public boolean add(PlayerModel model) {
        return false;
    }

    public PlayerModel get(String identifier) {
        if (!models.containsKey(identifier)) {
            Document doc = users.find(eq("uuid", identifier)).first();

            PlayerModel model = null;

            if (doc != null) {
                model = Main.gson.fromJson(((Document) doc).toJson(), PlayerModel.class);
            } else {
                model = new PlayerModel().setUuid(identifier);
            }
            this.models.put(identifier, model);
            return model;
        } else {
            return this.models.get(identifier);
        }
    }

    public void reload() {
        this.models.values().forEach(PlayerModel::reload);
    }
}
