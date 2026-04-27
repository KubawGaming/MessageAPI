package me.kubaw208.messageapi.structs;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.kubaw208.betterrunnableapi.BetterDelayedRunnable;
import me.kubaw208.messageapi.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class SoundableMessage extends LocalizedMessage {

    @Accessors(chain = true) private List<String> soundPaths = new ArrayList<>();
    @Setter @Accessors(chain = true) private Integer soundDelay = null;
    @Setter @Accessors(chain = true) private Float soundVolume = null;
    @Setter @Accessors(chain = true) private Float soundPitch = null;

    // Override for getter/setter chaining
    @Setter @Accessors(chain = true) private List<String> commands = new ArrayList<>();

    /**
     * Sets location where sound will be played.
     * @param location location where sound will be played. If null, sound will be played in recipient location.
     * @return this message instance for chaining.
     */
    public SoundableMessage inLocation(Location location) {
        setLocation(location);
        return this;
    }

    /**
     * Plays sound to the player. Should be called when sending message.
     * @param player player who received the message.
     */
    protected void applySound(@NotNull Player player) {
        if(soundPaths.isEmpty()) return;

        new BetterDelayedRunnable(plugin, task -> {
            if(!player.isOnline()) return;

            Location location = getLocation() != null ? getLocation() : player.getLocation();

            var sound = soundPaths.get(
                    soundPaths.size() == 1 ? 0 : Utils.getRandom(0, soundPaths.size() - 1)
            );

            player.playSound(location, sound, soundVolume, soundPitch);
        }, soundDelay);
    }

    public SoundableMessage setSoundPaths(List<String> soundPaths) {
        this.soundPaths = soundPaths;

        if(soundPaths == null || soundPaths.isEmpty()) return this;

        if(soundDelay == null) soundDelay = 0;
        if(soundVolume == null) soundVolume = 1f;
        if(soundPitch == null) soundPitch = 1f;
        return this;
    }

}