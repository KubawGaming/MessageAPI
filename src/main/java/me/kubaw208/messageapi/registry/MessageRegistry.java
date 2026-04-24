package me.kubaw208.messageapi.registry;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.kubaw208.messageapi.structs.Message;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageRegistry {

    private static final MessageRegistry INSTANCE = new MessageRegistry();

    public static MessageRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<String, RegisteredMessage<?>> byType = new ConcurrentHashMap<>();
    private final Map<Class<?>, RegisteredMessage<?>> byClass = new ConcurrentHashMap<>();

    public <T extends Message> void register(
            @NotNull String messageType,
            @NotNull Class<T> clazz,
            @NotNull Function<T, Map<String, Object>> serializer,
            @NotNull Function<Map<String, Object>, T> deserializer
    ) {
        String normalized = normalize(messageType);

        if(byType.containsKey(normalized))
            throw new IllegalArgumentException("Message type already registered: " + normalized);

        if(byClass.containsKey(clazz))
            throw new IllegalArgumentException("Message class already registered: " + clazz.getSimpleName());

        RegisteredMessage<T> entry = new RegisteredMessage<>(
                normalized,
                clazz,
                serializer,
                deserializer
        );

        byType.put(normalized, entry);
        byClass.put(clazz, entry);
        Bukkit.getLogger().info("Registered message type: " + normalized + " (" + clazz.getSimpleName() + ")");
    }

    public Optional<RegisteredMessage<?>> getByType(@NotNull String messageType) {
        return Optional.ofNullable(byType.get(normalize(messageType)));
    }

    public Optional<RegisteredMessage<?>> getByClass(@NotNull Class<?> clazz) {
        return Optional.ofNullable(byClass.get(clazz));
    }

    public boolean isRegistered(@NotNull String messageType) {
        return byType.containsKey(normalize(messageType));
    }

    public void unregister(@NotNull String messageType) {
        String normalized = normalize(messageType);
        RegisteredMessage<?> removed = byType.remove(normalized);

        if(removed == null) return;

        byClass.remove(removed.clazz());
        Bukkit.getLogger().info("Unregistered message type: " + normalized);
    }

    private String normalize(@NotNull String type) {
        if(type.isBlank())
            throw new IllegalArgumentException("messageType cannot be blank");

        return type.toUpperCase(Locale.ROOT);
    }

}