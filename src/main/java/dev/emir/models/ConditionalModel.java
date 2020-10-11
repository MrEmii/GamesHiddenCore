package dev.emir.models;

public class ConditionalModel {

    private String uuid;
    private String username;
    private boolean accepted;
    private String requester;

    public ConditionalModel(String uuid, String username, boolean accepted, String requester) {
        this.uuid = uuid;
        this.username = username;
        this.accepted = accepted;
        this.requester = requester;
    }

    //TODO: Conditional logic

    public String getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public String getRequester() {
        return requester;
    }
}
