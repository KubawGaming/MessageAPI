package me.kubaw208.messageapi.structs.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import me.kubaw208.messageapi.structs.SoundableMessage;
import me.kubaw208.messageapi.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChatListMessage extends SoundableMessage {

    @JsonProperty("list") @Getter private final List<String> messages;

    @JsonCreator
    public ChatListMessage(@NotNull @JsonProperty("list") List<String> messages) {
        this.messages = messages;
    }

    @Override
    public void sendTo(@NotNull Player player) {
        applySound(player);
        applyCommands(player);

        for(String message : messages) {
            sendMessage(player, Utils.hexComponent(message));
        }
    }

    @Override
    public ChatListMessage replace(@NotNull String toReplace, @NotNull String replaced) {
        ChatListMessage cloned = this.clone();
        ArrayList<String> replacedMessages = new ArrayList<>();

        for(String message : cloned.getMessages()) {
            replacedMessages.add(message.replace(toReplace, replaced));
        }

        cloned.getMessages().clear();
        cloned.getMessages().addAll(replacedMessages);
        return cloned;
    }

    /**
     * Sends messages to console.
     * @param colors if true, messages will be sent with colors, otherwise they will be sent without colors.
     */
    public void sendToConsole(boolean colors) {
        for(String message : messages) {
            Bukkit.getConsoleSender().sendMessage(colors ? Utils.hexComponent(message) : Component.text(message));
        }
    }

    /**
     * Sends messages to console with colored text.
     */
    public void sendToConsole() {
        sendToConsole(true);
    }

    @Override
    public ChatListMessage clone() {
        try {
            ChatListMessage cloned = (ChatListMessage) super.clone();

            cloned.getMessages().clear();
            cloned.getMessages().addAll(new ArrayList<>(this.messages));
            return cloned;
        } catch(CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
