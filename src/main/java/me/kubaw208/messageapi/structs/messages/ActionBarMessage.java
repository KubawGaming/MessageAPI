package me.kubaw208.messageapi.structs.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import me.kubaw208.messageapi.structs.SoundableMessage;
import me.kubaw208.messageapi.utils.Utils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ActionBarMessage extends SoundableMessage {

    @JsonProperty("actionbar") @Getter @Setter private String message;

    @JsonCreator
    public ActionBarMessage(@NotNull @JsonProperty("actionbar") String message) {
        this.message = message;
    }

    @Override
    public void sendTo(@NotNull Player player) {
        applySound(player);
        applyCommands(player);
        sendActionBar(player, Utils.hexComponent(message));
    }

    @Override
    public ActionBarMessage replace(@NotNull String toReplace, @NotNull String replaced) {
        ActionBarMessage cloned = this.clone();

        cloned.setMessage(cloned.getMessage().replace(toReplace, replaced));
        return cloned;
    }

    @Override
    public ActionBarMessage clone() {
        try {
            return (ActionBarMessage) super.clone();
        } catch(CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}