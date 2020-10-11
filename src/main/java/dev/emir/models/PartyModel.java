package dev.emir.models;

import java.util.ArrayList;

public class PartyModel {

    private ArrayList<String> players = new ArrayList<>();
    private ArrayList<ConditionalModel> pending = new ArrayList<>();
    private String creator = "";

    public void addToParty(String uuid) {
        players.add(uuid);
    }

    public String requestParty(String uuid) {
        if (!players.contains(uuid)) {
            if (pending.stream().anyMatch(conditionalModel -> conditionalModel.getUuid().equalsIgnoreCase(uuid))) {
                return "¡Ya has invitado a este jugador!";
            } else {
                //TODO: Enviar solicitud & crear conditional model;
                return "";
            }
        } else {
            return "¡El jugador ya está en la party!";
        }
    }



}
