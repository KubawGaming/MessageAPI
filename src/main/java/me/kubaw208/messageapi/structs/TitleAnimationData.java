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
public class TitleAnimationData {

    private String title;
    private String subtitle;
    private Long fadeIn;
    private Long stay;
    private Long fadeOut;
    private Long time;

    public TitleAnimationData clone() {
        return new TitleAnimationData(title, subtitle, fadeIn, stay, fadeOut, time);
    }

}
