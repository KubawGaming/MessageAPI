package me.kubaw208.messageapi.serializers;

import de.exlll.configlib.Serializer;
import me.kubaw208.messageapi.registry.MessageRegistry;
import me.kubaw208.messageapi.registry.RegisteredMessage;
import me.kubaw208.messageapi.structs.Message;

import java.util.*;

public class MessageSerializer implements Serializer<Message, LinkedHashMap<String, Object>> {

    private final MessageRegistry registry = MessageRegistry.getInstance();

    // ConfigLib key names
    private static final String MESSAGE_TYPE_KEY = "messageType";
    private static final String SOUND_PATH_KEY = "soundPath";
    private static final String SOUND_PATHS_KEY = "soundPaths";
    private static final String SOUND_DELAY_KEY = "soundDelay";
    private static final String SOUND_VOLUME_KEY = "soundVolume";
    private static final String SOUND_PITCH_KEY = "soundPitch";
    private static final String COMMANDS_KEY = "commands";

    @Override
    public LinkedHashMap<String, Object> serialize(Message message) {
        RegisteredMessage<?> registration = registry.getByClass(message.getClass())
                .orElseThrow(() -> new IllegalStateException(
                        "No registration found for message class: " + message.getClass().getSimpleName()
                ));
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        Map<String, Object> serialized = Optional
                .ofNullable(applySerialize(registration, message))
                .orElse(Collections.emptyMap());

        result.put(MESSAGE_TYPE_KEY, registration.type().toUpperCase(Locale.ROOT));

        serialized.forEach((key, value) -> {
            if(result.containsKey(key))
                throw new IllegalStateException("Duplicate key during serialization: " + key);

            result.put(key, value);
        });

        if(!message.getSoundPaths().isEmpty()) {
            if(message.getSoundPaths().size() > 1) {
                result.put(SOUND_PATHS_KEY, new ArrayList<>(message.getSoundPaths()));
            } else {
                result.put(SOUND_PATH_KEY, message.getSoundPaths().get(0));
            }

            result.put(SOUND_DELAY_KEY, message.getSoundDelay());
            result.put(SOUND_VOLUME_KEY, message.getSoundVolume());
            result.put(SOUND_PITCH_KEY, message.getSoundPitch());
        }

        if(!message.getCommands().isEmpty())
            result.put(COMMANDS_KEY, message.getCommands());

        return result;
    }

    @Override
    public Message deserialize(LinkedHashMap<String, Object> map) {
        Object rawType = map.get(MESSAGE_TYPE_KEY);

        if(!(rawType instanceof String type) || type.isBlank())
            throw new IllegalArgumentException("Missing or invalid '" + MESSAGE_TYPE_KEY + "' field: " + map);

        RegisteredMessage<?> registration = registry.getByType(type)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown message type: " + type
                ));
        Message message = applyDeserialize(registration, map);
        Object soundPaths = map.get(SOUND_PATHS_KEY);
        Object soundPath = map.get(SOUND_PATH_KEY);
        Object soundDelay = map.get(SOUND_DELAY_KEY);
        Object soundVolume = map.get(SOUND_VOLUME_KEY);
        Object soundPitch = map.get(SOUND_PITCH_KEY);
        Object commands = map.get(COMMANDS_KEY);

        message.getSoundPaths().clear();
        message.getCommands().clear();

        if(soundPaths != null && soundPath != null)
            throw new IllegalArgumentException("Use either 'soundPath' or 'soundPaths', not both (conflicting config keys)");

        if(soundPaths != null) {
            if(!(soundPaths instanceof List<?> list))
                throw new IllegalArgumentException("soundPaths must be a list");

            for(Object o : list) {
                message.getSoundPaths().add(String.valueOf(o));
            }
        }

        if(soundPath != null)
            message.getSoundPaths().add(soundPath.toString());

        if(soundDelay != null) {
            if(!(soundDelay instanceof Number number))
                throw new IllegalArgumentException("soundDelay must be a number");

            message.setSoundDelay(number.intValue());
        }

        if(soundVolume != null) {
            if(!(soundVolume instanceof Number number))
                throw new IllegalArgumentException("soundVolume must be a number");

            message.setSoundVolume(number.floatValue());
        }

        if(soundPitch != null) {
            if(!(soundPitch instanceof Number number))
                throw new IllegalArgumentException("soundPitch must be a number");

            message.setSoundPitch(number.floatValue());
        }

        if(commands != null) {
            if(!(commands instanceof List<?> list))
                throw new IllegalArgumentException("commands must be a list");

            for(Object o : list) {
                String cmd = String.valueOf(o);

                if(cmd.isBlank()) continue;

                message.getCommands().add(cmd);
            }
        }

        return message;
    }

    @SuppressWarnings("unchecked")
    private <T extends Message> Map<String, Object> applySerialize(RegisteredMessage<T> registration, Message message) {
        return registration.serializer().apply((T) message);
    }

    private <T extends Message> T applyDeserialize(RegisteredMessage<T> registration, Map<String, Object> map) {
        return registration.deserializer().apply(map);
    }

}