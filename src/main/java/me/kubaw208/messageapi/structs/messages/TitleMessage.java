package me.kubaw208.messageapi.structs.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;
import me.kubaw208.messageapi.structs.SoundableMessage;
import me.kubaw208.messageapi.utils.Utils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("TITLE")
@Getter
@Setter
public class TitleMessage extends SoundableMessage {

    @JsonProperty("title") private String title;
    @JsonProperty("subtitle") private String subtitle;
    @JsonProperty("fadeIn") private Integer fadeIn;
    @JsonProperty("stay") private Integer stay;
    @JsonProperty("fadeOut") private Integer fadeOut;

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
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    @Override
    public TitleMessage sendToInternal(@NotNull Player player) {
        applySound(player);
        applyCommands(player);
        sendTitle(player,
                Utils.hexComponent(title),
                Utils.hexComponent(subtitle),
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
