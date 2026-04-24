package me.kubaw208.messageapi.registry;

import me.kubaw208.messageapi.structs.Message;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;

public record RegisteredMessage<T extends Message>(
        @NotNull String type,
        @NotNull Class<T> clazz,
        @NotNull Function<T, Map<String, Object>> serializer,
        @NotNull Function<Map<String, Object>, T> deserializer
) {

    public RegisteredMessage {
        if(type.isBlank())
            throw new IllegalArgumentException("type cannot be blank");
    }

}