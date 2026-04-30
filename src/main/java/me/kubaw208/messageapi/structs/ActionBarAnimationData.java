package me.kubaw208.messageapi.structs;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActionBarAnimationData implements Cloneable {

    private String message;
    private Integer time;

    @Override
    public ActionBarAnimationData clone() {
        try {
            return (ActionBarAnimationData) super.clone();
        } catch(CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}