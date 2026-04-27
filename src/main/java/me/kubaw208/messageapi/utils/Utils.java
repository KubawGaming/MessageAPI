package me.kubaw208.messageapi.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class Utils {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    /**
     * Colors the component message with hex colors
     * @return Component with a hex colors message
     */
    public static Component hexComponent(String message) {
        return miniMessage.deserializeOrNull(message);
    }

    public static List<Component> hexComponentList(List<String> messages) {
        return messages.stream()
                .map(miniMessage::deserialize)
                .collect(Collectors.toList());
    }

    /**
     * Generate a random number between two values
     * @param lower Lowest number
     * @param upper Highest number
     * @return Random number between lower and upper variables
     */
    public static int getRandom(int lower, int upper) {
        return new Random().nextInt(upper - lower + 1) + lower;
    }

    /**
     * Extracts String from Component with colors
     * @param component Component sent to be changed
     * @return String from a component
     */
    public static String asText(Component component) {
        return miniMessage.serialize(component);
    }

    /**
     * @returns new Title with replaced text
     */
    public static Title replacedTitle(Title title, HashMap<String, String> replacements) {
        String titleText = asText(title.title());
        String subtitleText = asText(title.subtitle());

        for(Map.Entry<String, String> entry : replacements.entrySet()) {
            titleText = titleText.replace(entry.getKey(), entry.getValue());
            subtitleText = subtitleText.replace(entry.getKey(), entry.getValue());
        }

        return Title.title(hexComponent(titleText), hexComponent(subtitleText), title.times());
    }

}