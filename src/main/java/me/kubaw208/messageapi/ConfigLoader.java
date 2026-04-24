package me.kubaw208.messageapi;

import de.exlll.configlib.ConfigLib;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurationStore;
import lombok.Getter;
import me.kubaw208.messageapi.configs.GlobalConfig;
import me.kubaw208.messageapi.serializers.MessageSerializer;
import me.kubaw208.messageapi.structs.Message;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class ConfigLoader {

    public static final YamlConfigurationProperties PROPERTIES = ConfigLib.BUKKIT_DEFAULT_PROPERTIES
            .toBuilder()
            .charset(StandardCharsets.UTF_8)
            .addSerializer(Message.class, new MessageSerializer())
            .build();

    private final MessageAPI plugin;

    public ConfigLoader(MessageAPI plugin) {
        this.plugin = plugin;
    }

    YamlConfigurationStore<GlobalConfig> globalConfigStore;
    @Getter private GlobalConfig globalConfig;

    public void loadConfigs() {
        globalConfigStore = new YamlConfigurationStore<>(GlobalConfig.class, PROPERTIES);
        this.globalConfig = globalConfigStore.update(new File(plugin.getDataFolder(), "config.yml").toPath());
    }

}