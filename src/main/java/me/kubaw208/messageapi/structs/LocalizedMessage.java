package me.kubaw208.messageapi.structs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class LocalizedMessage extends Message {

    @JsonIgnore @Setter @Accessors(chain = true) private Location location = null;

    // Override for setter chaining to LocalizedMessage
    @Setter @Accessors(chain = true) private List<String> commands = new ArrayList<>();

}