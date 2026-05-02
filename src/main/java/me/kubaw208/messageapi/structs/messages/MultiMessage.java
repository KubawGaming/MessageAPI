package me.kubaw208.messageapi.structs.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import me.kubaw208.messageapi.structs.Message;
import me.kubaw208.messageapi.structs.SoundableMessage;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@JsonTypeName("MULTI")
@Getter
public class MultiMessage extends SoundableMessage {

    @JsonProperty("messages") private ArrayList<Message> messages;

    @JsonCreator
    public MultiMessage(@NotNull @JsonProperty("messages") List<Message> messages) {
        this.messages = new ArrayList<>(messages);
    }

    @Override
    public MultiMessage sendToInternal(@NotNull CommandSender recipient) {
        for(Message message : messages) {
            message.sendTo(recipient);
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
        return (MultiMessage) super.clone();
    }

}