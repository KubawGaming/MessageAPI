package me.kubaw208.messageapi.configs;

import de.exlll.configlib.Configuration;
import me.kubaw208.messageapi.structs.Message;
import me.kubaw208.messageapi.structs.messages.ChatMessage;

import java.util.List;

@Configuration
public class GlobalConfig {

    public Message testMessage = new ChatMessage("Test message")
            .setSoundPaths(List.of("minecraft:entity.experience_orb.pickup"))
            .setSoundDelay(20)
            .setSoundVolume(1)
            .setSoundPitch(1)
            .setCommands(List.of("say Hello", "say world!"));

}