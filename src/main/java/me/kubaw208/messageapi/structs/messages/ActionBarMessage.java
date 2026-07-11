package me.kubaw208.messageapi.structs.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.kubaw208.messageapi.structs.SoundableMessage;
import me.kubaw208.messageapi.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("ACTION_BAR")
@Getter
@Setter
public class ActionBarMessage extends SoundableMessage {

    @JsonProperty("message") private String message;

    @JsonCreator
    public ActionBarMessage(@NotNull @JsonProperty("message") String message) {
        this.message = message;
    }

    @Override
    public ActionBarMessage sendToInternal(@NotNull CommandSender recipient) {
        Player playerForPlaceholder = recipient instanceof Player player ? player : null;

        applySound(recipient);
        applyCommands(recipient);
        sendActionBar(recipient, Utils.hexComponent(
                getParsePlaceholders()
                        ? PlaceholderAPI.setPlaceholders(playerForPlaceholder, message)
                        : message
        ));
        return this;
    }

    @Override
    public ActionBarMessage replace(@NotNull String toReplace, @NotNull String replaced) {
        ActionBarMessage cloned = this.clone();

        cloned.setMessage(cloned.getMessage().replace(toReplace, replaced));
        return cloned;
    }

    @Override
    public ActionBarMessage clone() {
        return (ActionBarMessage) super.clone();
    }

}