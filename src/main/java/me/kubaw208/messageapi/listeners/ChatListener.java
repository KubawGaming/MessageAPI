package me.kubaw208.messageapi.listeners;

import me.kubaw208.messageapi.MessageAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

public class ChatListener implements Listener {

    private final MessageAPI plugin;

    public ChatListener(MessageAPI plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();

        plugin.getConfigLoader().getGlobalConfig().testMessage.sendTo(player);
    }

}
