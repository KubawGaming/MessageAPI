package me.kubaw208.messageapi.serializers;

import de.exlll.configlib.Serializer;
import me.kubaw208.messageapi.structs.Message;
import tools.jackson.core.type.TypeReference;

import java.util.LinkedHashMap;

public class MessageSerializer implements Serializer<Message, LinkedHashMap<String, Object>> {

    @Override
    public LinkedHashMap<String, Object> serialize(Message message) {
        return Message.getObjectMapper().convertValue(message, new TypeReference<>() {});
    }

    @Override
    public Message deserialize(LinkedHashMap<String, Object> map) {
        return Message.getObjectMapper().convertValue(map, Message.class);
    }

}