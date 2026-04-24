package me.kubaw208.messageapi.structs.messages;

import lombok.Getter;
import lombok.Setter;
import me.kubaw208.messageapi.structs.Message;
import me.kubaw208.messageapi.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChatMessage extends Message implements Cloneable {

    @Getter @Setter private String message;

    public ChatMessage(String message) {
        this.message = message;
    }

    @Override
    public void sendTo(@NotNull Player player) {
        applyMessageSound(player);
        applyCommands(player);
        sendMessage(player, Utils.hexComponent(message));
        sendActionBar(player, Utils.hexComponent(message));
        sendTitle(player, Utils.hexComponent(message), null, 500, 3000, 2000);
    }

    @Override
    public ChatMessage replace(@NotNull String toReplace, @NotNull String replaced) {
        ChatMessage cloned = this.clone();

        cloned.setMessage(cloned.getMessage().replace(toReplace, replaced));
        return cloned;
    }

    /**
     * Sends message to console.
     * @param colors if true, message will be sent with colors, otherwise it will be sent without colors.
     */
    public void sendToConsole(boolean colors) {
        Bukkit.getConsoleSender().sendMessage(colors ? Utils.hexComponent(message) : Component.text(message));
    }

    /**
     * Sends message to console with colored text.
     */
    public void sendToConsole() {
        sendToConsole(true);
    }

    @Override
    public ChatMessage clone() {
        try {
            return (ChatMessage) super.clone();
        } catch(CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}