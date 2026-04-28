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
public class SoundSettings {

    private List<String> paths = new ArrayList<>();
    private Integer delay;
    private Float volume;
    private Float pitch;

}