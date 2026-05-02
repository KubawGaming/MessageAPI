package me.kubaw208.messageapi.structs.messages;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.kubaw208.messageapi.structs.SoundableMessage;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("EMPTY")
public class EmptyMessage extends SoundableMessage {

    @Override
    public EmptyMessage sendToInternal(@NotNull CommandSender receiver) {
        applySound(receiver);
        applyCommands(receiver);
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
