package me.kubaw208.messageapi.structs.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.clip.placeholderapi.PlaceholderAPI;
import me.kubaw208.messageapi.structs.SoundableMessage;
import me.kubaw208.messageapi.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("TITLE")
@Getter
@Setter
@Accessors(chain = true)
public class TitleMessage extends SoundableMessage {

    @JsonProperty("title") private String title;
    @JsonProperty("subtitle") private String subtitle;
    @JsonProperty("fadeIn") private Long fadeIn;
    @JsonProperty("stay") private Long stay;
    @JsonProperty("fadeOut") private Long fadeOut;

    @JsonCreator
    public TitleMessage(
            @JsonProperty("title") String title,
            @JsonProperty("subtitle") String subtitle,
            @JsonProperty("fadeIn") Long fadeIn,
            @JsonProperty("stay") Long stay,
            @JsonProperty("fadeOut") Long fadeOut
    ) {
        this.title = title;
        this.subtitle = subtitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    @JsonCreator
    public TitleMessage(
            @JsonProperty("title") String title,
            @JsonProperty("subtitle") String subtitle,
            @JsonProperty("fadeIn") Integer fadeIn,
            @JsonProperty("stay") Integer stay,
            @JsonProperty("fadeOut") Integer fadeOut
    ) {
        this.title = title;
        this.subtitle = subtitle;
        this.fadeIn = fadeIn.longValue();
        this.stay = stay.longValue();
        this.fadeOut = fadeOut.longValue();
    }

    @Override
    public TitleMessage sendToInternal(@NotNull CommandSender recipient) {
        Player playerForPlaceholder = recipient instanceof Player player ? player : null;

        applySound(recipient);
        applyCommands(recipient);
        sendTitle(recipient,
                Utils.hexComponent(
                        getParsePlaceholders()
                                ? PlaceholderAPI.setPlaceholders(playerForPlaceholder, title)
                                : title
                ),
                Utils.hexComponent(
                        getParsePlaceholders()
                                ? PlaceholderAPI.setPlaceholders(playerForPlaceholder, subtitle)
                                : subtitle
                ),
                fadeIn != null ? fadeIn : 0,
                stay != null ? stay : 1000,
                fadeOut != null ? fadeOut : 0
        );
        return this;
    }

    @Override
    public TitleMessage replace(@NotNull String toReplace, @NotNull String replaced) {
        TitleMessage cloned = this.clone();

        if(cloned.getTitle() != null)
            cloned.setTitle(cloned.getTitle().replace(toReplace, replaced));

        if(cloned.getSubtitle() != null)
            cloned.setSubtitle(cloned.getSubtitle().replace(toReplace, replaced));

        return cloned;
    }

    @Override
    public TitleMessage clone() {
        return (TitleMessage) super.clone();
    }

}
