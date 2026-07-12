package me.kubaw208.messageapi.structs.embed;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * Represents the author of a Discord embed, shown as a small line above the embed title.
 */
@Getter
@Setter
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Author implements Cloneable {

    /** Author name displayed above the embed title. */
    @JsonProperty("name") private String name;
    /** URL the author name links to when clicked. */
    @JsonProperty("url") private String url;
    /** URL of the small icon shown next to the author name. */
    @JsonProperty("iconUrl") private String iconUrl;

    public Author replace(String toReplace, String replaced) {
        AuthorBuilder cloned = Author.builder();

        if(name != null) cloned.name = name.replace(toReplace, replaced);
        if(url != null) cloned.url = url.replace(toReplace, replaced);
        if(iconUrl != null) cloned.iconUrl = iconUrl.replace(toReplace, replaced);
        return cloned.build();
    }

    @Override
    public Author clone() {
        try {
            return (Author) super.clone();
        } catch(CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}