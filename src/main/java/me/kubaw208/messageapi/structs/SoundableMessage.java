package me.kubaw208.messageapi.structs;

import com.fasterxml.jackson.annotation.JsonProperty;
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

public abstract class SoundableMessage extends LocalizedMessage {

    @JsonProperty("sound") private SoundSettings soundSettings = null;

    // Override for setter chaining to SoundableMessage
    @Getter @Setter @Accessors(chain = true) private List<String> commands = new ArrayList<>();

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
        if(soundSettings.getPaths().isEmpty()) return;

        new BetterDelayedRunnable(plugin, task -> {
            if(!player.isOnline()) return;

            Location location = getLocation() != null ? getLocation() : player.getLocation();

            var sound = soundSettings.getPaths().get(
                    soundSettings.getPaths().size() == 1 ? 0 : Utils.getRandom(0, soundSettings.getPaths().size() - 1)
            );

            player.playSound(location, sound, soundSettings.getVolume(), soundSettings.getPitch());
        }, soundSettings.getDelay());
    }

    public SoundableMessage setSoundPaths(List<String> soundPaths) {
        if(soundSettings == null)
            soundSettings = new SoundSettings();

        soundSettings.setPaths(soundPaths);

        if(soundPaths == null || soundPaths.isEmpty()) return this;

        if(soundSettings.getDelay() == null) soundSettings.setDelay(0);
        if(soundSettings.getVolume() == null) soundSettings.setVolume(1f);
        if(soundSettings.getPitch() == null) soundSettings.setPitch(1f);
        return this;
    }

    public Integer getSoundDelay() {
        return soundSettings == null ? null : soundSettings.getDelay();
    }

    public SoundableMessage setSoundDelay(int delay) {
        if(soundSettings == null)
            soundSettings = new SoundSettings();

        soundSettings.setDelay(delay);
        return this;
    }

    public Float getSoundVolume() {
        return soundSettings == null ? null : soundSettings.getVolume();
    }

    public SoundableMessage setSoundVolume(float volume) {
        if(soundSettings == null)
            soundSettings = new SoundSettings();

        soundSettings.setVolume(volume);
        return this;
    }

    public Float getSoundPitch() {
        return soundSettings == null ? null : soundSettings.getPitch();
    }

    public SoundableMessage setSoundPitch(float pitch) {
        if(soundSettings == null)
            soundSettings = new SoundSettings();

        soundSettings.setPitch(pitch);
        return this;
    }

}