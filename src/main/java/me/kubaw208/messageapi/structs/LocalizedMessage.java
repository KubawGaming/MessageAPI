package me.kubaw208.messageapi.structs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Location;

import java.util.List;

/**
 * Represents a message that is associated with a specific in-game location.
 * <p>
 * This class extends {@link Message} by adding optional spatial context,
 * which can be used by implementations to modify how or where the message
 * is delivered (e.g., proximity-based messages, location-aware effects).
 */
@Getter
public abstract class LocalizedMessage extends Message {

    @JsonIgnore @Setter @Accessors(chain = true) private Location location = null;

    // Override setter for chaining to LocalizedMessage
    public LocalizedMessage setCommands(List<String> commands) {
        this.commands = commands;
        return this;
    }

    @Override
    public LocalizedMessage clone() {
        LocalizedMessage cloned = (LocalizedMessage) super.clone();

        cloned.location = location != null ? location.clone() : null;
        return cloned;
    }

}