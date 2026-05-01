package me.kubaw208.messageapi.structs.messages;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.kubaw208.messageapi.structs.SoundableMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("EMPTY")
public class EmptyMessage extends SoundableMessage {

    @Override
    public EmptyMessage sendToInternal(@NotNull Player player) {
        applySound(player);
        applyCommands(player);
        return this;
    }

    @Override
    public EmptyMessage replace(@NotNull String toReplace, @NotNull String replaced) {
        return this.clone();
    }

    @Override
    public EmptyMessage clone() {
        return (EmptyMessage) super.clone();
    }

}
