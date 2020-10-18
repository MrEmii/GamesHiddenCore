package dev.emir.nametag.api.events;

import dev.emir.nametag.api.data.INametag;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This class represents an Event that is fired when a
 * player joins the server and receives their nametag.
 */
public class NametagFirstLoadedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private Player player;
    private INametag nametag;

    public NametagFirstLoadedEvent(Player player, INametag nametag) {
        this.player = player;
        this.nametag = nametag;
    }

    public NametagFirstLoadedEvent(boolean isAsync, Player player, INametag nametag) {
        super(isAsync);
        this.player = player;
        this.nametag = nametag;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}