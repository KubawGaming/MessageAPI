# MessageAPI

[![Version](https://jitpack.io/v/KubawGaming/MessageAPI.svg)](https://jitpack.io/#KubawGaming/MessageAPI)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Paper](https://img.shields.io/badge/Paper-1.17+-green.svg)](https://papermc.io/)

A lightweight message management library for Minecraft Paper servers. Simplifies sending chat messages, titles, action bars, and more with MiniMessage support, animations, and sound integration.

**[📖 Full Documentation](https://kubawgaming.github.io/MessageAPI/)**

## ✨ Features

- **🎮 1.17+ Support**: Works with Minecraft 1.17 and newer versions
- **📦 Built-in Message Types**: ChatMessage, ActionBarMessage, TitleMessage, AnimatedTitleMessage, AnimatedActionBarMessage, and more!
- **🎨 MiniMessage Integration**: Full hex color support using MiniMessage formatting
- **🔊 Sound System**: Configurable sounds with customizable pitch and volume (editable via code or config)
- **💾 ConfigLib Serialization**: Built-in serialization/deserialization for ConfigLib (easily adaptable to other libraries)
- **🎬 Animations**: Default message types for animated titles and actionbars
- **⚡ Command Execution**: You can optionally execute commands when messages are sent
- **🔗 Method Chaining**: Fluent API for clean, readable code
- **🔌 PlaceholderAPI Support**: Built-in integration with PlaceholderAPI for dynamic placeholders in messages (e.g. player names, statistics, and custom placeholders)

## 📚 Examples

### Initialize
```java
public class YourPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        Message.init(this);
    }
}
```

### Simple Chat Message Example
```java
Message message = new ChatMessage("<green>Hello, <player>!")
        .replace("{PLAYER}", player.getName())
        .sendTo(player);
```

### Animated Title with Sound Example
```java
SoundableMessage title = new AnimatedTitleMessage(List.of(
        new TitleAnimationData("<red>WARNING", "<gray>You", 10, 40, 10, 20),
        new TitleAnimationData("<red>WARNING", "<gray>are", 10, 40, 10, 20),
        new TitleAnimationData("<red>WARNING", "<gray>low HP!", 10, 40, 10, 20)
));

title.setSoundPaths(List.of("minecraft:entity.experience_orb.pickup"))
        .setSoundVolume(1f)
        .setSoundPitch(0.5f)
        // Only send to players with less than 10 health
        .sendTo(Bukkit.getOnlinePlayers(), player -> player.getHealth() < 10);
```

## 🚀 Installation

### Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.KubawGaming</groupId>
        <artifactId>MessageAPI</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

### Gradle
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.KubawGaming:MessageAPI:{VERSION}'
}
```
