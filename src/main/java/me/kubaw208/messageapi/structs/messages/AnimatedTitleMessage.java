package me.kubaw208.messageapi.structs.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.kubaw208.betterrunnableapi.BetterRunnable;
import me.kubaw208.messageapi.structs.SoundableMessage;
import me.kubaw208.messageapi.structs.TitleAnimationData;
import me.kubaw208.messageapi.utils.Utils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@JsonTypeName("ANIMATED_TITLE")
@JsonPropertyOrder({
        "defaultTime",
        "defaultFadeIn",
        "defaultStay",
        "defaultFadeOut"
})
@Getter
@Accessors(chain = true)
public class AnimatedTitleMessage extends SoundableMessage {

    @Getter private static final HashMap<Player, BetterRunnable> animatedTitleTasks = new HashMap<>();

    @JsonProperty("frames") private ArrayList<TitleAnimationData> frames;
    @Setter @JsonProperty("defaultTime") private Integer defaultTime = null;
    @Setter @JsonProperty("defaultFadeIn") private Integer defaultFadeIn = null;
    @Setter @JsonProperty("defaultStay") private Integer defaultStay = null;
    @Setter @JsonProperty("defaultFadeOut") private Integer defaultFadeOut = null;

    @JsonCreator
    public AnimatedTitleMessage(@NotNull @JsonProperty("frames") List<TitleAnimationData> frames) {
        this.frames = new ArrayList<>(frames);
    }

    @Override
    public AnimatedTitleMessage sendToInternal(@NotNull Player player) {
        applySound(player);
        applyCommands(player);

        if(frames.isEmpty()) return this;

        List<TitleAnimationData> frameList = new ArrayList<>(frames);
        AtomicInteger currentFrame = new AtomicInteger(0);
        AtomicInteger allFramesTime = new AtomicInteger(0);

        if(animatedTitleTasks.containsKey(player)) // prevent multiple animations for one player
            animatedTitleTasks.get(player).stop();

        animatedTitleTasks.put(player, new BetterRunnable(plugin, task -> {
            if(!player.isOnline()) {
                task.stop();
                animatedTitleTasks.remove(player);
                return;
            }

            int frameId = currentFrame.get();

            if(frameId >= frameList.size()) {
                task.stop();
                animatedTitleTasks.remove(player);
                return;
            }

            TitleAnimationData frame = frameList.get(frameId);
            int frameTime = frame.getTime() != null ? frame.getTime() : (defaultTime != null ? defaultTime : 1);
            int time = (allFramesTime.get() + frameTime);

            if(task.getExecutions() < time) return;

            sendTitle(
                    player,
                    Utils.hexComponent(frame.getTitle()),
                    Utils.hexComponent(frame.getSubtitle()),
                    frame.getFadeIn() != null ? frame.getFadeIn() : (defaultFadeIn != null ? defaultFadeIn : 0),
                    frame.getStay() != null ? frame.getStay() : (defaultStay != null ? defaultStay : 60),
                    frame.getFadeOut() != null ? frame.getFadeOut() : (defaultFadeOut != null ? defaultFadeOut : 0)
            );

            currentFrame.incrementAndGet();
            allFramesTime.addAndGet(frameTime);
        }, 0, 1));
        return this;
    }

    @Override
    public AnimatedTitleMessage replace(@NotNull String toReplace, @NotNull String replaced) {
        AnimatedTitleMessage cloned = this.clone();
        List<TitleAnimationData> replacedFrames = new ArrayList<>();

        for(TitleAnimationData frame : cloned.getFrames()) {
            TitleAnimationData newFrame = frame.clone();

            if(newFrame.getTitle() != null)
                newFrame.setTitle(newFrame.getTitle().replace(toReplace, replaced));

            if(newFrame.getSubtitle() != null)
                newFrame.setSubtitle(newFrame.getSubtitle().replace(toReplace, replaced));

            replacedFrames.add(newFrame);
        }

        cloned.getFrames().clear();
        cloned.getFrames().addAll(replacedFrames);
        return cloned;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AnimatedTitleMessage clone() {
        AnimatedTitleMessage cloned = (AnimatedTitleMessage) super.clone();

        cloned.frames = (ArrayList<TitleAnimationData>) frames.clone();
        return cloned;
    }

}