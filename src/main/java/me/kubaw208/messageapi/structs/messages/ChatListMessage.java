package me.kubaw208.messageapi.structs.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.kubaw208.messageapi.structs.SoundableMessage;
import me.kubaw208.messageapi.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@JsonTypeName("CHAT_LIST")
@Getter
public class ChatListMessage extends SoundableMessage {

    @JsonProperty("messages") private ArrayList<String> messages;

    @JsonCreator
    public ChatListMessage(@NotNull @JsonProperty("messages") List<String> messages) {
        this.messages = new ArrayList<>(messages);
    }

    @Override
    public ChatListMessage sendToInternal(@NotNull CommandSender recipient) {
        Player playerForPlaceholder = recipient instanceof Player player ? player : null;

        applySound(recipient);
        applyCommands(recipient);

        for(String message : messages) {
            sendMessage(recipient, Utils.hexComponent(
                    getParsePlaceholders()
                            ? PlaceholderAPI.setPlaceholders(playerForPlaceholder, message)
                            : message
                    ));
        }
        return this;
    }

    @Override
    public ChatListMessage replace(@NotNull String toReplace, @NotNull String replaced) {
        ChatListMessage cloned = this.clone();
        List<String> replacedMessages = new ArrayList<>();

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
            sendMessage(
                    Bukkit.getConsoleSender(),
                    colors ? Utils.hexComponent(message) : Component.text(message)
            );
        }
    }

    /**
     * Sends messages to console with colored text.
     */
    public void sendToConsole() {
        sendToConsole(true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ChatListMessage clone() {
        ChatListMessage cloned = (ChatListMessage) super.clone();

        cloned.messages = (ArrayList<String>) messages.clone();
        return cloned;
    }

}
