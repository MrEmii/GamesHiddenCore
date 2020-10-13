package dev.emir.utils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class ChatCreator {

    private TextComponent component;

    public ChatCreator(String text) {
        this.component = new TextComponent(text);
    }

    public ChatCreator(TextComponent component) {
        this.component = component;
    }

    public TextComponent onHover(String hoverText) {
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hoverText)));
        return component;
    }

    public TextComponent onClick(String command) {
        component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
        return component;
    }

    public TextComponent onClick(String command, ClickEvent.Action action) {
        component.setClickEvent(new ClickEvent(action, command));
        return component;
    }


    private static void clearChat(Player player) {
        if (player != null && player.isOnline()) {
            for (int i = 0; i < 100; i++)
                player.sendMessage("");
        }
    }
}
