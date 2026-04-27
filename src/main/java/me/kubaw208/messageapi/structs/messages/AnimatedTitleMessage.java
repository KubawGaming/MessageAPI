package me.kubaw208.messageapi.structs.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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

    @JsonProperty("animatedTitle") private final List<TitleAnimationData> frames;
    @Setter private Integer defaultTime = null;
    @Setter private Integer defaultFadeIn = null;
    @Setter private Integer defaultStay = null;
    @Setter private Integer defaultFadeOut = null;

    @JsonCreator
    public AnimatedTitleMessage(@NotNull @JsonProperty("animatedTitle") List<TitleAnimationData> frames) {
        this.frames = frames;
    }

    @Override
    public void sendTo(@NotNull Player player) {
        applySound(player);
        applyCommands(player);

        if(frames.isEmpty()) return;

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
    public AnimatedTitleMessage clone() {
        try {
            AnimatedTitleMessage cloned = (AnimatedTitleMessage) super.clone();
            List<TitleAnimationData> clonedFrames = new ArrayList<>();

            for(TitleAnimationData frame : this.frames) {
                clonedFrames.add(frame.clone());
            }

            cloned.getFrames().clear();
            cloned.getFrames().addAll(clonedFrames);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
