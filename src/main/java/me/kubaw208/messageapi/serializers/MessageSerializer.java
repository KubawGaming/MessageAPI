package me.kubaw208.messageapi.serializers;

import de.exlll.configlib.Serializer;
import me.kubaw208.messageapi.structs.Message;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;

public class MessageSerializer implements Serializer<Message, LinkedHashMap<String, Object>> {

    private final ObjectMapper mapper = Message.getObjectMapper();

    @Override
    public LinkedHashMap<String, Object> serialize(Message message) {
        return mapper.convertValue(message, new TypeReference<>() {});
    }

    @Override
    public Message deserialize(LinkedHashMap<String, Object> map) {
        return mapper.convertValue(map, Message.class);
    }

}