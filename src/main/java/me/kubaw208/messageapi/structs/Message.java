package me.kubaw208.messageapi.structs;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.kubaw208.betterrunnableapi.BetterDelayedRunnable;
import me.kubaw208.messageapi.structs.messages.ChatMessage;
import me.kubaw208.messageapi.utils.Utils;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Message {

    protected static JavaPlugin plugin;
    private static BukkitAudiences adventure;

    @Setter @Accessors(chain = true) private List<String> soundPaths = new ArrayList<>();
    @Setter @Accessors(chain = true) private int soundDelay = 0;
    @Setter @Accessors(chain = true) private float soundVolume = 1;
    @Setter @Accessors(chain = true) private float soundPitch = 1;
    @Getter(AccessLevel.PRIVATE) private Location soundLocation = null;
    @Setter @Accessors(chain = true) private List<String> commands = new ArrayList<>();

    public static void init(JavaPlugin plugin) {
        Message.plugin = plugin;
        Message.adventure = BukkitAudiences.create(plugin);
    }

    /**
     * Sends message to given player.
     * @param player player to send message to.
     */
    public abstract void sendTo(@NotNull Player player);

    /**
     * Replaces text in the message.
     * @param toReplace placeholder to replace.
     * @param replaced new value of the placeholder.
     * @return new message with replaced placeholder.
     */
    public abstract Message replace(@NotNull String toReplace, @NotNull String replaced);

    /**
     * Sends message to given player. Uses adventure API that increases MiniMessage versions support.
     * @param player player to send message to.
     * @param message message to send.
     */
    protected void sendMessage(Player player, Component message) {
        adventure.player(player).sendMessage(message);
    }

    /**
     * Sends actionbar message to given player. Uses adventure API that increases MiniMessage versions support.
     * @param player player to send message to.
     * @param message message to send.
     */
    protected void sendActionBar(Player player, Component message) {
        adventure.player(player).sendActionBar(message);
    }

    /**
     * Sends title to given player. Uses adventure API that increases MiniMessage versions support.
     * @param player player to send message to.
     * @param title title message to send.
     * @param subtitle subtitle message to send.
     * @param fadeIn time in milliseconds for title to fade in.
     * @param stay time in milliseconds for title to stay.
     * @param fadeOut time in milliseconds for title to fade out.
     */
    protected void sendTitle(@NotNull Player player, Component title, Component subtitle, int fadeIn, int stay, int fadeOut) {
        var audience = adventure.player(player);

        audience.sendTitlePart(
                TitlePart.TIMES,
                Title.Times.times(
                        Duration.ofMillis(fadeIn),
                        Duration.ofMillis(stay),
                        Duration.ofMillis(fadeOut)
                )
        );
        audience.sendTitlePart(TitlePart.TITLE, title != null ? title : Component.empty());
        audience.sendTitlePart(TitlePart.SUBTITLE, subtitle != null ? subtitle : Component.empty());
    }

    /**
     * Sends message to given players.
     * @param players players to send message to.
     */
    public void sendTo(@NotNull Iterable<? extends Player> players) {
        for(Player player : players) {
            sendTo(player);
        }
    }

    /**
     * Sends message to given players.
     * @param players players to send message to.
     */
    public void sendTo(Player... players) {
        for(Player player : players) {
            sendTo(player);
        }
    }

    /**
     * Broadcasts message to all online players.
     */
    public void broadcast() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            sendTo(player);
        }
    }

    /**
     * @see #replace(String, String)
     */
    public Message replace(@NotNull String toReplace, int replaced) {
        return replace(toReplace, String.valueOf(replaced));
    }

    /**
     * @see #replace(String, String)
     */
    public Message replace(@NotNull String toReplace, double replaced) {
        return replace(toReplace, String.valueOf(replaced));
    }

    /**
     * Sets location where sound will be played.
     * @param location location where sound will be played. If null, sound will be played in recipient location.
     * @return this message instance for chaining.
     */
    public Message inLocation(Location location) {
        this.soundLocation = location;
        return this;
    }

    public <T extends Message> T as(@NotNull Class<T> type) {
        if(type.isInstance(this))
            return type.cast(this);

        throw new ClassCastException("Cannot cast " + this.getClass().getSimpleName() + " to " + type.getSimpleName());
    }

    public ChatMessage asChat() {
        return as(ChatMessage.class);
    }

    /**
     * Plays sound to the player. Should be called when sending message.
     * @param player player who received the message.
     */
    protected void applyMessageSound(@NotNull Player player) {
        if(soundPaths.isEmpty()) return;

        new BetterDelayedRunnable(plugin, task -> {
            if(!player.isOnline()) return;

            Location location = soundLocation != null ? soundLocation : player.getLocation();

            var sound = soundPaths.get(
                    soundPaths.size() == 1 ? 0 : Utils.getRandom(0, soundPaths.size() - 1)
            );

            player.playSound(location, sound, soundVolume, soundPitch);
        }, soundDelay);
    }

    /**
     * Applies commands to the server. Should be called when sending message.
     * @param player player who received the message. Replaced with {PLAYER} placeholder.
     */
    protected void applyCommands(@NotNull Player player) {
        for(String command : commands) {
            if(command == null || command.isBlank()) continue;

            Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    command.replace("{PLAYER}", player.getName())
            );
        }
    }

}