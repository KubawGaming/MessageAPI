package me.kubaw208.messageapi.structs.embed;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Discord embed - the rich, boxed content block attached to a webhook message.
 */
@Getter
@Setter
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Embed implements Cloneable {

    /** Embed title shown at the top of the embed. */
    @JsonProperty("title") private String title;
    /** Main body text of the embed. */
    @JsonProperty("description") private String description;
    /** URL the title links to when clicked. */
    @JsonProperty("url") private String url;
    /** Color of the embed's left border, as an RGB integer (e.g. {@code 0xFF0000} for red). */
    @JsonProperty("color") private Integer color;
    /** Timestamp shown in the footer, in ISO-8601 format (e.g. {@code 2026-07-12T10:15:30+00:00}). */
    @JsonProperty("timestamp") private String timestamp;
    /** Footer displayed at the bottom of the embed. */
    @JsonProperty("footer") private Footer footer;
    /** URL of the small image shown in the top-right corner. */
    @JsonProperty("thumbnailUrl") private String thumbnailUrl;
    /** URL of the large image shown at the bottom of the embed. */
    @JsonProperty("imageUrl") private String imageUrl;
    /** Author line shown above the title. */
    @JsonProperty("author") private Author author;
    /** List of name/value fields displayed within the embed. */
    @JsonProperty("fields") private List<Field> fields;

    public Embed replace(String toReplace, String replaced) {
        EmbedBuilder cloned = Embed.builder();

        if(title != null) cloned.title = title.replace(toReplace, replaced);
        if(description != null) cloned.description = description.replace(toReplace, replaced);
        if(url != null) cloned.url = url.replace(toReplace, replaced);
        if(timestamp != null) cloned.timestamp = timestamp.replace(toReplace, replaced);
        if(color != null) cloned.color = color;
        if(thumbnailUrl != null) cloned.thumbnailUrl = thumbnailUrl.replace(toReplace, replaced);
        if(imageUrl != null) cloned.imageUrl = imageUrl.replace(toReplace, replaced);
        if(footer != null) cloned.footer = footer.replace(toReplace, replaced);
        if(author != null) cloned.author = author.replace(toReplace, replaced);

        if(fields != null) {
            List<Field> clonedFields = new ArrayList<>();

            for(Field field : fields) {
                clonedFields.add(field.replace(toReplace, replaced));
            }
            cloned.fields = clonedFields;
        }

        return cloned.build();
    }

    @Override
    public Embed clone() {
        try {
            return (Embed) super.clone();
        } catch(CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}