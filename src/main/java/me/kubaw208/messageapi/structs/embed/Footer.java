package me.kubaw208.messageapi.structs.embed;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * Represents the footer of a Discord embed, shown as a small line at the bottom of the embed.
 */
@Getter
@Setter
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Footer implements Cloneable {

    /** Footer text displayed at the bottom of the embed. */
    @JsonProperty("text") private String text;
    /** URL of the small icon shown next to the footer text. */
    @JsonProperty("iconUrl") private String iconUrl;

    public Footer replace(String toReplace, String replaced) {
        FooterBuilder cloned = Footer.builder();

        if(text != null) cloned.text = text.replace(toReplace, replaced);
        if(iconUrl != null) cloned.iconUrl = iconUrl.replace(toReplace, replaced);
        return cloned.build();
    }

    @Override
    public Footer clone() {
        try {
            return (Footer) super.clone();
        } catch(CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}