package me.kubaw208.messageapi.structs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TitleAnimationData {

    private String title;
    private String subtitle;
    private Long fadeIn;
    private Long stay;
    private Long fadeOut;
    private Long time;

    @JsonCreator
    public TitleAnimationData(
            @JsonProperty("title") @Nullable String title,
            @JsonProperty("subtitle") @Nullable String subtitle,
            @JsonProperty("fadeIn") @Nullable Long fadeIn,
            @JsonProperty("stay") @Nullable Long stay,
            @JsonProperty("fadeOut") @Nullable Long fadeOut,
            @JsonProperty("time") @Nullable Long time) {
        this.title = title;
        this.subtitle = subtitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
        this.time = time;
    }

    public TitleAnimationData(@Nullable String title, @Nullable String subtitle, @Nullable Integer fadeIn, @Nullable Integer stay, @Nullable Integer fadeOut, @Nullable Integer time) {
        this.title = title;
        this.subtitle = subtitle;
        this.fadeIn = fadeIn == null ? null : fadeIn.longValue();
        this.stay = stay == null ? null : stay.longValue();
        this.fadeOut = fadeOut == null ? null : fadeOut.longValue();
        this.time = time == null ? null : time.longValue();
    }

    public TitleAnimationData clone() {
        return new TitleAnimationData(title, subtitle, fadeIn, stay, fadeOut, time);
    }

}
