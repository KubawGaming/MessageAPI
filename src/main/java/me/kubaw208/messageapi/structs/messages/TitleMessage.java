package me.kubaw208.messageapi.structs.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import me.kubaw208.messageapi.structs.SoundableMessage;
import me.kubaw208.messageapi.utils.Utils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TitleMessage extends SoundableMessage {

    @Getter @Setter private String title;
    @Getter @Setter private String subtitle;
    @Getter @Setter private int fadeIn;
    @Getter @Setter private int stay;
    @Getter @Setter private int fadeOut;

    @JsonCreator
    public TitleMessage(
            @JsonProperty("title") String title,
            @JsonProperty("subtitle") String subtitle,
            @JsonProperty("fadeIn") int fadeIn,
            @JsonProperty("stay") int stay,
            @JsonProperty("fadeOut") int fadeOut
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
                fadeIn,
                stay,
                fadeOut
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
        try {
            return (TitleMessage) super.clone();
        } catch(CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
