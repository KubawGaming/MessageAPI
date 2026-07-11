package me.kubaw208.messageapi.structs.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.kubaw208.messageapi.structs.SoundableMessage;
import me.kubaw208.messageapi.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("CHAT")
@Getter
@Setter
public class ChatMessage extends SoundableMessage {

    @JsonProperty("message") private String message;

    @JsonCreator
    public ChatMessage(@NotNull @JsonProperty("message") String message) {
        this.message = message;
    }

    @Override
    public ChatMessage sendToInternal(@NotNull CommandSender recipient) {
        Player playerForPlaceholder = recipient instanceof Player player ? player : null;

        applySound(recipient);
        applyCommands(recipient);
        sendMessage(recipient, Utils.hexComponent(
                getParsePlaceholders()
                        ? PlaceholderAPI.setPlaceholders(playerForPlaceholder, message)
                        : message
        ));
        return this;
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
        sendMessage(
                Bukkit.getConsoleSender(),
                colors ? Utils.hexComponent(message) : Component.text(message)
        );
    }

    /**
     * Sends message to console with colored text.
     */
    public void sendToConsole() {
        sendToConsole(true);
    }

    @Override
    public ChatMessage clone() {
        return (ChatMessage) super.clone();
    }

}