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

/**
 * A message that can play a sound effect when delivered to a player.
 * <p>
 * Extends {@link LocalizedMessage} by adding optional sound configuration
 * such as sound paths, volume, pitch, delay, and playback location.
 */
public abstract class SoundableMessage extends LocalizedMessage {

    @JsonProperty("sound") private SoundSettings soundSettings = null;

    // Override for setter chaining to SoundableMessage
    @Getter @Setter @Accessors(chain = true) private List<String> commands = new ArrayList<>();

    /**
     * Sets the location where the sound will be played.
     * <p>
     * If the location is null, the sound will be played at the recipient's current location.
     * @param location the location where the sound will be played, or null to use player location.
     * @return this message instance for chaining.
     */
    public SoundableMessage inLocation(Location location) {
        setLocation(location);
        return this;
    }

    /**
     * Plays the configured sound to the specified player.
     * <p>
     * This method should be called when sending the message.
     * If no sound paths are configured, no sound will be played.
     * @param player the player who received the message.
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

    /**
     * Sets the sound paths used by this message.
     * @param soundPaths list of sound identifiers to be used.
     * @return this message instance for chaining.
     */
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


    /**
     * Returns the configured sound delay.
     * @return delay in ticks or null if not set.
     */
    public Integer getSoundDelay() {
        return soundSettings == null ? null : soundSettings.getDelay();
    }

    /**
     * Sets the delay before the sound is played.
     * @param delay delay in ticks.
     * @return this message instance for chaining.
     */
    public SoundableMessage setSoundDelay(int delay) {
        if(soundSettings == null)
            soundSettings = new SoundSettings();

        soundSettings.setDelay(delay);
        return this;
    }

    /**
     * Returns the configured sound volume.
     * @return volume or null if not set.
     */
    public Float getSoundVolume() {
        return soundSettings == null ? null : soundSettings.getVolume();
    }

    /**
     * Sets the volume of the sound.
     * @param volume sound volume.
     * @return this message instance for chaining.
     */
    public SoundableMessage setSoundVolume(float volume) {
        if(soundSettings == null)
            soundSettings = new SoundSettings();

        soundSettings.setVolume(volume);
        return this;
    }

    /**
     * Returns the configured sound pitch.
     * @return pitch or null if not set.
     */
    public Float getSoundPitch() {
        return soundSettings == null ? null : soundSettings.getPitch();
    }

    /**
     * Sets the pitch of the sound.
     * @param pitch sound pitch.
     * @return this message instance for chaining.
     */
    public SoundableMessage setSoundPitch(float pitch) {
        if(soundSettings == null)
            soundSettings = new SoundSettings();

        soundSettings.setPitch(pitch);
        return this;
    }

}