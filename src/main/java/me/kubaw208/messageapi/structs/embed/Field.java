package me.kubaw208.messageapi.structs.embed;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * Represents a single field of a Discord embed, rendered as a name/value pair.
 */
@Getter
@Setter
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Field implements Cloneable {

    /** Field header (bold title of the field). */
    @JsonProperty("name") private String name;
    /** Field body text shown below the name. */
    @JsonProperty("value") private String value;
    /** Whether the field is displayed on the same row as adjacent inline fields; {@code false} or {@code null} puts it on its own line. */
    @JsonProperty("inline") private Boolean inline;

    public Field replace(String toReplace, String replaced) {
        FieldBuilder cloned = Field.builder();

        if(name != null) cloned.name = name.replace(toReplace, replaced);
        if(value != null) cloned.value = value.replace(toReplace, replaced);
        if(inline != null) cloned.inline = inline;
        return cloned.build();
    }

    @Override
    public Field clone() {
        try {
            return (Field) super.clone();
        } catch(CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}