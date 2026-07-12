package me.kubaw208.messageapi.structs;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.kubaw208.betterrunnableapi.BetterDelayedRunnable;
import me.kubaw208.messageapi.structs.messages.*;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Base abstract representation of a message that can be sent to message receivers
 * using different delivery methods (chat, title, action bar, etc.).
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "messageType"
)
@JsonInclude(value= JsonInclude.Include.NON_EMPTY, content= JsonInclude.Include.NON_NULL)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE
)
@Getter
public abstract class Message implements Cloneable {

    protected static JavaPlugin plugin;
    protected static BukkitAudiences audience;

    @Setter @Getter private static ObjectMapper objectMapper;
    private static final Set<Class<?>> registeredMessageTypes = ConcurrentHashMap.newKeySet();

    @JsonProperty("messageDelay") @Setter @Accessors(chain = true) private Integer messageDelay = null;
    @JsonProperty("parsePlaceholders") @Setter @Accessors(chain = true) protected Boolean parsePlaceholders = null;
    @JsonProperty("commands") @Setter @Accessors(chain = true) protected List<String> commands = new ArrayList<>();

    /**
     * Initializes the message system with the given plugin instance and registers default message types.
     * Also initializes the BukkitAudiences API for sending messages.
     * @param plugin the plugin instance used for scheduling and Adventure API.
     * @return true if the BukkitAudiences API was successfully initialized, false otherwise.
     */
    public static boolean init(JavaPlugin plugin) {
        Message.plugin = plugin;

        registerMessageType(EmptyMessage.class);
        registerMessageType(ChatMessage.class);
        registerMessageType(ChatListMessage.class);
        registerMessageType(ActionBarMessage.class);
        registerMessageType(AnimatedActionBarMessage.class);
        registerMessageType(TitleMessage.class);
        registerMessageType(AnimatedTitleMessage.class);
        registerMessageType(MultiMessage.class);
        registerMessageType(WebhookMessage.class);

        try {
            Message.audience = BukkitAudiences.create(plugin);
        } catch(Exception ignored) {
            return false;
        }
        return true;
    }

    /**
     * Initializes the BukkitAudiences API for sending messages.
     * @param audience the BukkitAudiences instance to use for sending messages by API.
     */
    public static void initAudience(BukkitAudiences audience) {
        Message.audience = audience;
    }

    /**
     * Registers a new message type for JSON serialization/deserialization.
     * @param message the message class to register.
     */
    public synchronized static void registerMessageType(Class<? extends Message> message) {
        if(registeredMessageTypes.add(message)) {
            var builder = JsonMapper.builder()
                    .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, false);

            builder.registerSubtypes(registeredMessageTypes);
            objectMapper = builder.build();
        }
    }

    /**
     * Unregisters a message type from JSON serialization/deserialization.
     * @param message the message class to unregister.
     */
    public synchronized static void unregisterMessageType(Class<? extends Message> message) {
        if(registeredMessageTypes.remove(message)) {
            var builder = JsonMapper.builder();

            builder.registerSubtypes(registeredMessageTypes);
            objectMapper = builder.build();
        }
    }

    /**
     * Sends this message to the specified recipient.
     * @param recipient the recipient who will receive the message.
     * @return this Message instance for chaining.
     */
    protected abstract Message sendToInternal(@NotNull CommandSender recipient);

    /**
     * Replaces occurrences of the given placeholder in this message.
     * @param toReplace the placeholder to replace.
     * @param replaced the value to replace the placeholder with.
     * @return a new Message instance with the applied replacements.
     */
    public abstract Message replace(@NotNull String toReplace, @NotNull String replaced);


    /**
     * Sends this message to the specified recipient after a delay (in ticks).
     * @param recipient the player who will receive the message.
     * @param delayInTicks the delay before sending the message (in server ticks).
     * @return this Message instance for chaining.
     */
    private Message sendToInternal(@NotNull CommandSender recipient, int delayInTicks) {
        new BetterDelayedRunnable(plugin, task -> sendToInternal(recipient), delayInTicks);
        return this;
    }

    /**
     * Sends this message to the specified recipient.
     * @param recipient the recipient who will receive the message.
     * @return this Message instance for chaining.
     */
    public Message sendTo(@NotNull CommandSender recipient) {
        return sendToInternal(recipient, getMessageDelay());
    }

    /**
     * Sends this message to the specified recipient after a delay (in ticks).
     * @param recipient the recipient who will receive the message.
     * @param delayInTicks the delay before sending the message (in server ticks).
     * @return this Message instance for chaining.
     */
    public Message sendTo(@NotNull CommandSender recipient, int delayInTicks) {
        return sendToInternal(recipient, delayInTicks);
    }

    /**
     * Sends this message to the specified recipients.
     * @param recipients the recipients who will receive the message.
     * @return this Message instance for chaining.
     */
    public Message sendTo(@NotNull Iterable<? extends CommandSender> recipients) {
        new BetterDelayedRunnable(plugin, task -> {
            for(CommandSender recipient : recipients) {
                sendToInternal(recipient);
            }
        }, getMessageDelay());
        return this;
    }

    /**
     * Sends this message to the specified recipients after a delay (in ticks).
     * @param recipients the recipients who will receive the message.
     * @param delayInTicks the delay before sending the message (in server ticks).
     * @return this Message instance for chaining.
     */
    public Message sendTo(@NotNull Iterable<? extends CommandSender> recipients, int delayInTicks) {
        new BetterDelayedRunnable(plugin, task -> {
            for(CommandSender recipient : recipients) {
                sendToInternal(recipient);
            }
        }, delayInTicks);
        return this;
    }

    /**
     * Sends this message to the specified recipients.
     * @param recipients the recipients who will receive the message.
     * @return this Message instance for chaining.
     */
    public Message sendTo(CommandSender... recipients) {
        new BetterDelayedRunnable(plugin, task -> {
            for(CommandSender recipient : recipients) {
                sendToInternal(recipient);
            }
        }, getMessageDelay());
        return this;
    }

    /**
     * Sends this message to the specified recipients after a delay (in ticks).
     * @param recipients the recipients who will receive the message.
     * @param delayInTicks the delay before sending the message (in server ticks).
     * @return this Message instance for chaining.
     */
    public Message sendTo(int delayInTicks, CommandSender... recipients) {
        new BetterDelayedRunnable(plugin, task -> {
            for(CommandSender recipient : recipients) {
                sendToInternal(recipient);
            }
        }, delayInTicks);
        return this;
    }

    /**
     * Broadcasts this message to all online players.
     * @return this Message instance for chaining.
     */
    public Message broadcast() {
        new BetterDelayedRunnable(plugin, task -> {
            for(Player player : Bukkit.getOnlinePlayers()) {
                sendToInternal(player);
            }
        }, getMessageDelay());
        return this;
    }

    /**
     * Broadcasts this message to all online players after a delay (in ticks).
     * @param delayInTicks the delay before sending the message (in server ticks).
     * @return this Message instance for chaining.
     */
    public Message broadcast(int delayInTicks) {
        new BetterDelayedRunnable(plugin, task -> {
            for(Player player : Bukkit.getOnlinePlayers()) {
                sendToInternal(player);
            }
        }, delayInTicks);
        return this;
    }

    /**
     * Sends this message to the specified recipient if that recipient has the given permission.
     * @param recipient the recipient who may receive the message.
     * @param permission the permission required for the player to receive the message.
     * @return this Message instance for chaining.
     */
    public Message sendTo(@NotNull CommandSender recipient, @NotNull String permission) {
        if(recipient.hasPermission(permission))
            sendToInternal(recipient, getMessageDelay());
        return this;
    }

    /**
     * Sends this message to the specified recipient if that recipient has the given permission, after a delay (in ticks).
     * @param recipient the recipient who may receive the message.
     * @param permission the permission required for the player to receive the message.
     * @param delayInTicks the delay before sending the message (in server ticks).
     * @return this Message instance for chaining.
     */
    public Message sendTo(@NotNull CommandSender recipient, @NotNull String permission, int delayInTicks) {
        if(recipient.hasPermission(permission))
            sendToInternal(recipient, delayInTicks);
        return this;
    }

    /**
     * Sends this message to the specified recipients who have the given permission.
     * @param recipients the recipients who may receive the message.
     * @param permission the permission required to receive the message.
     * @return this Message instance for chaining.
     */
    public Message sendTo(@NotNull Iterable<? extends CommandSender> recipients, @NotNull String permission) {
        new BetterDelayedRunnable(plugin, task -> {
            for(CommandSender recipient : recipients) {
                if(!recipient.hasPermission(permission)) continue;

                sendToInternal(recipient);
            }
        }, getMessageDelay());
        return this;
    }

    /**
     * Sends this message to the specified recipients who have the given permission, after a delay (in ticks).
     * @param recipients the recipients who may receive the message.
     * @param permission the permission required to receive the message.
     * @param delayInTicks the delay before sending the message (in server ticks).
     * @return this Message instance for chaining.
     */
    public Message sendTo(@NotNull Iterable<? extends CommandSender> recipients, @NotNull String permission, int delayInTicks) {
        new BetterDelayedRunnable(plugin, task -> {
            for(CommandSender receiver : recipients) {
                if(!receiver.hasPermission(permission)) continue;

                sendToInternal(receiver);
            }
        }, delayInTicks);
        return this;
    }


    /**
     * Sends this message to all players in the specified world.
     * @param world the world whose players will receive the message.
     * @return this Message instance for chaining.
     */
    public Message sendTo(@NotNull World world) {
        for(Player player : world.getPlayers()) {
            sendToInternal(player, getMessageDelay());
        }
        return this;
    }

    /**
     * Sends this message to all players in the specified world after a delay (in ticks).
     * @param world the world whose players will receive the message.
     * @param delayInTicks the delay before sending the message (in server ticks).
     * @return this Message instance for chaining.
     */
    public Message sendTo(@NotNull World world, int delayInTicks) {
        for(Player player : world.getPlayers()) {
            sendToInternal(player, delayInTicks);
        }
        return this;
    }

    /**
     * Sends this message to all players in the specified world who have the given permission.
     * @param world the world whose players may receive the message.
     * @param permission the permission required to receive the message.
     * @return this Message instance for chaining.
     */
    public Message sendTo(@NotNull World world, @NotNull String permission) {
        new BetterDelayedRunnable(plugin, task -> {
            for(Player player : world.getPlayers()) {
                if(!player.hasPermission(permission)) continue;

                sendToInternal(player);
            }
        }, getMessageDelay());
        return this;
    }

    /**
     * Sends this message to all players in the specified world who have the given permission,
     * after a delay (in ticks).
     * @param world the world whose players may receive the message.
     * @param permission the permission required to receive the message.
     * @param delayInTicks the delay before sending the message (in server ticks).
     * @return this Message instance for chaining.
     */
    public Message sendTo(@NotNull World world, @NotNull String permission, int delayInTicks) {
        new BetterDelayedRunnable(plugin, task -> {
            for(Player player : world.getPlayers()) {
                if(!player.hasPermission(permission)) continue;

                sendToInternal(player);
            }
        }, delayInTicks);
        return this;
    }

    /**
     * Sends this message to all online players having the given permission.
     * @param permission permission required to receive the message.
     * @return this Message instance for chaining.
     */
    public Message broadcast(@NotNull String permission) {
        new BetterDelayedRunnable(plugin, task -> {
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(!player.hasPermission(permission)) continue;

                sendToInternal(player);
            }
        }, getMessageDelay());
        return this;
    }

    /**
     * Sends this message to all online players having the given permission after a delay (in ticks).
     * @param permission permission required to receive the message.
     * @param delayInTicks delay before sending the message (in server ticks).
     * @return this Message instance for chaining.
     */
    public Message broadcast(@NotNull String permission, int delayInTicks) {
        new BetterDelayedRunnable(plugin, task -> {
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(!player.hasPermission(permission)) continue;

                sendToInternal(player);
            }
        }, delayInTicks);
        return this;
    }

    /**
     * Sends this message to all players within the specified radius from the given location.
     * @param location the center location used to determine nearby players.
     * @param radius the X/Y/Z bounding box radius (AABB). Players are searched in a cubic area, not a spherical radius.
     * @return this Message instance for chaining.
     */
    public Message sendTo(@NotNull Location location, double radius) {
        new BetterDelayedRunnable(plugin, task -> {
            for(Player player : location.getWorld().getNearbyPlayers(location, radius)) {
                sendToInternal(player);
            }
        }, getMessageDelay());
        return this;
    }

    /**
     * Sends this message to all players within the specified radius from the given location, after a delay (in ticks).
     * @param location the center location used to determine nearby players.
     * @param radius the X/Y/Z bounding box radius (AABB). Players are searched in a cubic area, not a spherical radius.
     * @param delayInTicks the delay before sending the message (in server ticks).
     * @return this Message instance for chaining.
     */
    public Message sendTo(@NotNull Location location, double radius, int delayInTicks) {
        new BetterDelayedRunnable(plugin, task -> {
            for(Player player : location.getWorld().getNearbyPlayers(location, radius)) {
                sendToInternal(player);
            }
        }, delayInTicks);
        return this;
    }

    /**
     * Sends this message to all players within the specified radius from the given location
     * who have the given permission.
     * @param location the center location used to determine nearby players.
     * @param radius the X/Y/Z bounding box radius (AABB). Players are searched in a cubic area, not a spherical radius.
     * @param permission the permission required to receive the message.
     * @return this Message instance for chaining.
     */
    public Message sendTo(@NotNull Location location, double radius, @NotNull String permission) {
        new BetterDelayedRunnable(plugin, task -> {
            for(Player player : location.getWorld().getNearbyPlayers(location, radius)) {
                if(!player.hasPermission(permission)) continue;

                sendToInternal(player);
            }
        }, getMessageDelay());
        return this;
    }

    /**
     * Sends this message to all players within the specified radius from the given location
     * who have the given permission, after a delay (in ticks).
     * @param location the center location used to determine nearby players.
     * @param radius the X/Y/Z bounding box radius (AABB). Players are searched in a cubic area, not a spherical radius.
     * @param permission the permission required to receive the message.
     * @param delayInTicks the delay before sending the message (in server ticks).
     * @return this Message instance for chaining.
     */
    public Message sendTo(@NotNull Location location, double radius, @NotNull String permission, int delayInTicks) {
        new BetterDelayedRunnable(plugin, task -> {
            for(Player player : location.getWorld().getNearbyPlayers(location, radius)) {
                if(!player.hasPermission(permission)) continue;

                sendToInternal(player);
            }
        }, delayInTicks);
        return this;
    }

    /**
     * Sends this message to a single recipient if they match the given predicate.
     * @param recipient the recipient to check and potentially send the message to.
     * @param filter predicate deciding whether the player should receive the message.
     * @return this Message instance for chaining.
     */
    public Message sendTo(@NotNull CommandSender recipient, @NotNull Predicate<CommandSender> filter) {
        if(!filter.test(recipient)) return this;

        return sendToInternal(recipient);
    }

    /**
     * Sends this message to a single recipient if they match the given predicate after a delay (in ticks).
     * @param recipient the recipient to check and potentially send the message to.
     * @param filter predicate deciding whether the player should receive the message.
     * @param delayInTicks delay before sending the message (in server ticks).
     * @return this Message instance for chaining.
     */
    public Message sendTo(@NotNull CommandSender recipient, @NotNull Predicate<CommandSender> filter, int delayInTicks) {
        if(!filter.test(recipient)) return this;

        new BetterDelayedRunnable(plugin, task -> sendToInternal(recipient), delayInTicks);
        return this;
    }

    /**
     * Sends this message to recipients matching the given predicate.
     * @param recipients all available recipients to filter.
     * @param filter predicate deciding who receives the message.
     * @return this Message instance for chaining.
     */
    public Message sendTo(@NotNull Iterable<? extends CommandSender> recipients, @NotNull Predicate<CommandSender> filter) {
        new BetterDelayedRunnable(plugin, task -> {
            for(CommandSender recipient : recipients) {
                if(!filter.test(recipient)) continue;

                sendToInternal(recipient);
            }
        }, getMessageDelay());
        return this;
    }

    /**
     * Sends this message to recipients matching the given predicate after a delay (in ticks).
     * @param recipients all available recipients to filter.
     * @param filter predicate deciding who receives the message.
     * @param delayInTicks delay before sending the message (in server ticks).
     * @return this Message instance for chaining.
     */
    public Message sendTo(@NotNull Iterable<? extends CommandSender> recipients, @NotNull Predicate<CommandSender> filter, int delayInTicks) {
        new BetterDelayedRunnable(plugin, task -> {
            for(CommandSender recipient : recipients) {
                if(!filter.test(recipient)) continue;

                sendToInternal(recipient);
            }
        }, delayInTicks);
        return this;
    }

    /**
     * Sends this message to all online players matching the given predicate.
     * @param filter predicate deciding who receives the message.
     * @return this Message instance for chaining.
     */
    public Message broadcast(@NotNull Predicate<Player> filter) {
        new BetterDelayedRunnable(plugin, task -> {
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(!filter.test(player)) continue;

                sendToInternal(player);
            }
        }, getMessageDelay());
        return this;
    }

    /**
     * Sends this message to all online players matching the given predicate after a delay (in ticks).
     * @param filter predicate deciding who receives the message.
     * @param delayInTicks delay before sending the message (in server ticks).
     * @return this Message instance for chaining.
     */
    public Message broadcast(@NotNull Predicate<Player> filter, int delayInTicks) {
        new BetterDelayedRunnable(plugin, task -> {
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(!filter.test(player)) continue;

                sendToInternal(player);
            }
        }, delayInTicks);
        return this;
    }

    /**
     * Replaces a placeholder with an integer value.
     * @param toReplace the placeholder to replace.
     * @param replaced the value to replace the placeholder with.
     * @return a new Message instance with the applied replacement.
     */
    public Message replace(@NotNull String toReplace, int replaced) {
        return replace(toReplace, String.valueOf(replaced));
    }

    /**
     * Replaces a placeholder with a double value.
     * @param toReplace the placeholder to replace.
     * @param replaced the value to replace the placeholder with.
     * @return a new Message instance with the applied replacement.
     */
    public Message replace(@NotNull String toReplace, double replaced) {
        return replace(toReplace, String.valueOf(replaced));
    }

    /**
     * Attempts to cast this message to the specified type.
     * @param type the target message type.
     * @return the casted message if possible, otherwise null.
     */
    public <T extends Message> @Nullable T asOrNull(@NotNull Class<T> type) {
        if(!type.isInstance(this)) return null;

        return type.cast(this);
    }

    public @Nullable EmptyMessage asEmpty() {
        return asOrNull(EmptyMessage.class);
    }

    public @Nullable ChatMessage asChat() {
        return asOrNull(ChatMessage.class);
    }

    public @Nullable ChatListMessage asChatList() {
        return asOrNull(ChatListMessage.class);
    }

    public @Nullable ActionBarMessage asActionBar() {
        return asOrNull(ActionBarMessage.class);
    }

    public @Nullable AnimatedActionBarMessage asAnimatedActionBar() {
        return asOrNull(AnimatedActionBarMessage.class);
    }

    public @Nullable TitleMessage asTitle() {
        return asOrNull(TitleMessage.class);
    }

    public @Nullable AnimatedTitleMessage asAnimatedTitle() {
        return asOrNull(AnimatedTitleMessage.class);
    }

    public @Nullable MultiMessage asMultiple() {
        return asOrNull(MultiMessage.class);
    }

    public @Nullable WebhookMessage asDiscord() {
        return asOrNull(WebhookMessage.class);
    }

    /**
     * Sends a chat message to the specified recipient using the Adventure API.
     * @param recipient the recipient who will receive the message.
     * @param message the message component to send.
     */
    protected void sendMessage(@NotNull CommandSender recipient, Component message) {
        audience.sender(recipient).sendMessage(message != null ? message : Component.empty());
    }

    /**
     * Sends an actionbar message to the specified recipient using the Adventure API.
     * @param recipient the recipient who will receive the message.
     * @param message the message component to send.
     */
    protected void sendActionBar(@NotNull CommandSender recipient, Component message) {
        audience.sender(recipient).sendActionBar(message != null ? message : Component.empty());
    }

    /**
     * Sends a title to the specified recipient using the Adventure API.
     * @param recipient the recipient who will receive the title.
     * @param title the title component.
     * @param subtitle the subtitle component.
     * @param fadeIn the fade-in time in milliseconds.
     * @param stay the display time in milliseconds.
     * @param fadeOut the fade-out time in milliseconds.
     */
    protected void sendTitle(@NotNull CommandSender recipient, Component title, Component subtitle, int fadeIn, int stay, int fadeOut) {
        var audience = Message.audience.sender(recipient);

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
     * Executes configured commands associated with this message.
     * @param recipient the recipient used for placeholder replacement ( {PLAYER} ).
     */
    protected void applyCommands(@NotNull CommandSender recipient) {
        for(String command : commands) {
            if(command == null || command.isBlank()) continue;

            Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    command.replace("{PLAYER}", recipient.getName())
            );
        }
    }

    /**
     * Returns the delay for this message in ticks.
     * Defaults to 0 if not specified.
     * @return the message delay in server ticks.
     */
    public Integer getMessageDelay() {
        return Objects.requireNonNullElse(messageDelay, 0);
    }

    /**
     * Returns if message should be parsed using PlaceholderAPI.
     * Defaults to false if not specified.
     * @return the result if message should be parsed using PlaceholderAPI.
     */
    public Boolean getParsePlaceholders() {
        return Objects.requireNonNullElse(parsePlaceholders, false);
    }

    @Override
    public Message clone() {
        try {
            Message cloned = (Message) super.clone();

            if(commands != null)
                cloned.commands = new ArrayList<>(commands);

            return cloned;
        } catch(CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}