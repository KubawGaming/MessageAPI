package me.kubaw208.messageapi.structs;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@JsonInclude(value= JsonInclude.Include.NON_EMPTY, content= JsonInclude.Include.NON_NULL)
public class SoundSettings implements Cloneable {

    private List<String> paths = new ArrayList<>();
    private Integer delay;
    private Float volume;
    private Float pitch;

    @Override
    public SoundSettings clone() {
        try {
            SoundSettings cloned = (SoundSettings) super.clone();

            if(paths != null)
                cloned.paths = new ArrayList<>(paths);

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

}