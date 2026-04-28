package me.kubaw208.messageapi.structs.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import me.kubaw208.messageapi.structs.Message;
import me.kubaw208.messageapi.structs.SoundableMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MultiMessage extends SoundableMessage {

    @Getter private final List<Message> messages;

    @JsonCreator
    public MultiMessage(@NotNull @JsonProperty("messages") List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public MultiMessage sendToInternal(@NotNull Player player) {
        for(Message message : messages) {
            message.sendTo(player);
        }
        return this;
    }

    @Override
    public MultiMessage replace(@NotNull String toReplace, @NotNull String replaced) {
        MultiMessage cloned = this.clone();
        List<Message> messageComponents = new ArrayList<>();

        for(Message message : this.messages) {
            messageComponents.add(message.replace(toReplace, replaced));
        }

        cloned.messages.clear();
        cloned.messages.addAll(messageComponents);
        return cloned;
    }

    @Override
    public MultiMessage clone() {
        try {
            return (MultiMessage) super.clone();
        } catch(CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}