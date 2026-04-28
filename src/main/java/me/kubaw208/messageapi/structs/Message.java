package me.kubaw208.messageapi.structs;

import com.fasterxml.jackson.annotation.*;
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


@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonInclude(value= JsonInclude.Include.NON_EMPTY, content= JsonInclude.Include.NON_NULL)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE
)
@Getter
public abstract class Message {

    protected static JavaPlugin plugin;
    private static BukkitAudiences adventure;

    @Setter @Getter private static ObjectMapper objectMapper;
    private static final Set<Class<?>> registeredMessageTypes = ConcurrentHashMap.newKeySet();

    @JsonProperty("messageDelay") @Setter private Integer messageDelay = null;
    @Setter @Accessors(chain = true) private List<String> commands = new ArrayList<>();

    public static void init(JavaPlugin plugin) {
        me.kubaw208.messageapi.structs.Message.plugin = plugin;
        me.kubaw208.messageapi.structs.Message.adventure = BukkitAudiences.create(plugin);

        registerMessageType(EmptyMessage.class);
        registerMessageType(ChatMessage.class);
        registerMessageType(ChatListMessage.class);
        registerMessageType(ActionBarMessage.class);
        registerMessageType(AnimatedActionBarMessage.class);
        registerMessageType(TitleMessage.class);
        registerMessageType(AnimatedTitleMessage.class);
        registerMessageType(MultiMessage.class);
    }

    public synchronized static void registerMessageType(Class<? extends Message> message) {
        if(registeredMessageTypes.add(message)) {
            var builder = JsonMapper.builder()
                    .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, false);

            builder.registerSubtypes(registeredMessageTypes);
            objectMapper = builder.build();
        }
    }

    public synchronized static void unregisterMessageType(Class<? extends Message> message) {
        if(registeredMessageTypes.remove(message)) {
            var builder = JsonMapper.builder();

            builder.registerSubtypes(registeredMessageTypes);
            objectMapper = builder.build();
        }
    }

    /**
     * Sends message to given player.
     * @param player player to send message to.
     */
    protected abstract Message sendToInternal(@NotNull Player player);

    /**
     * Replaces text in the message.
     * @param toReplace placeholder to replace.
     * @param replaced new value of the placeholder.
     * @return new message with replaced placeholder.
     */
    public abstract Message replace(@NotNull String toReplace, @NotNull String replaced);

    /**
     * Sends message to given player.
     * @param player player to send message to.
     */
    private Message sendToInternal(Player player, int delayInTicks) {
        new BetterDelayedRunnable(plugin, task -> sendToInternal(player), delayInTicks);
        return this;
    }

    /**
     * Sends message to given player.
     * @param player player to send message to.
     */
    public Message sendTo(@NotNull Player player) {
        sendToInternal(player, getMessageDelay());
        return this;
    }
    /**
     * Sends message to given player with delay in ticks.
     * @param player player to send message to.
     */
    public Message sendTo(@NotNull Player player, int delayInTicks) {
        sendToInternal(player, delayInTicks);
        return this;
    }

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
    public Message sendTo(@NotNull Iterable<? extends Player> players) {
        new BetterDelayedRunnable(plugin, task -> {
            for(Player player : players) {
                sendToInternal(player);
            }
        }, getMessageDelay());
        return this;
    }

    /**
     * Sends message to given players with delay in ticks.
     * @param players players to send message to.
     * @param delayInTicks delay in ticks.
     */
    public Message sendTo(@NotNull Iterable<? extends Player> players, int delayInTicks) {
        new BetterDelayedRunnable(plugin, task -> {
            for(Player player : players) {
                sendToInternal(player);
            }
        }, delayInTicks);
        return this;
    }

    /**
     * Sends message to given players.
     * @param players players to send message to.
     */
    public Message sendTo(Player... players) {
        new BetterDelayedRunnable(plugin, task -> {
            for(Player player : players) {
                sendToInternal(player);
            }
        }, getMessageDelay());
        return this;
    }

    /**
     * Sends message to given players with delay in ticks.
     * @param players players to send message to.
     * @param delayInTicks delay in ticks.
     */
    public Message sendTo(int delayInTicks, Player... players) {
        new BetterDelayedRunnable(plugin, task -> {
            for(Player player : players) {
                sendToInternal(player);
            }
        }, delayInTicks);
        return this;
    }

    /**
     * Broadcasts message to all online players.
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
     * Broadcasts message to all online players with delay in ticks.
     * @param delayInTicks delay in ticks.
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

    public @Nullable TitleMessage asTitle() {
        return asOrNull(TitleMessage.class);
    }

    public @Nullable MultiMessage asMultiple() {
        return asOrNull(MultiMessage.class);
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

    /**
     * @return delay in ticks for the message. Default is 0 if not specified in config file.
     */
    public Integer getMessageDelay() {
        return Objects.requireNonNullElse(messageDelay, 0);
    }

}