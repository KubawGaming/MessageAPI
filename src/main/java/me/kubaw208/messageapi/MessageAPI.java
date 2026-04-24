package me.kubaw208.messageapi;

import lombok.Getter;
import me.kubaw208.messageapi.listeners.ChatListener;
import me.kubaw208.messageapi.registry.MessageRegistry;
import me.kubaw208.messageapi.structs.Message;
import me.kubaw208.messageapi.structs.messages.ChatMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;

public final class MessageAPI extends JavaPlugin {

    @Getter private ConfigLoader configLoader;

    @Override
    public void onEnable() {
        Message.init(this);

        MessageRegistry.getInstance().register(
                "CHAT",
                ChatMessage.class,
                message -> {
                    LinkedHashMap<String, Object> map = new LinkedHashMap<>();

                    map.put("message", message.getMessage());
                    return map;
                },
                map -> new ChatMessage(String.valueOf(map.get("message")))
        );

        this.configLoader = new ConfigLoader(this);
        configLoader.loadConfigs();

        Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
    }

    @Override
    public void onDisable() {

    }

}