package dev.emir.managers;

import com.mongodb.client.MongoCollection;
import dev.emir.Main;
import dev.emir.models.SignsModel;
import org.bson.Document;

import java.util.HashMap;

import static com.mongodb.client.model.Filters.eq;

public class SignsManager {

    private MongoCollection<Document> signsCollection;
    private HashMap<String, SignsModel> signs;

    public SignsManager(MongoCollection<Document> signsCollection) {
        this.signsCollection = signsCollection;
        this.signs = new HashMap<>();
    }

    public SignsModel getSign(String id) {
        if (signsCollection.find(eq("name", id)).first() == null && !signs.containsKey(id)) {
            return null;
        } else {
            SignsModel model = Main.gson.fromJson(((Document) signsCollection.find(eq("name", id)).first()).toJson(), SignsModel.class);
            signs.putIfAbsent(id, model);
            return model;
        }

    }

    public MongoCollection<Document> getSignsCollection() {
        return signsCollection;
    }

    public HashMap<String, SignsModel> getSigns() {
        return signs;
    }
}
