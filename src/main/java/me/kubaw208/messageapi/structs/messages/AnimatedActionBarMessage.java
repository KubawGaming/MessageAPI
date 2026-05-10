package me.kubaw208.messageapi.structs.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.kubaw208.betterrunnableapi.BetterRunnable;
import me.kubaw208.messageapi.structs.ActionBarAnimationData;
import me.kubaw208.messageapi.structs.SoundableMessage;
import me.kubaw208.messageapi.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@JsonTypeName("ANIMATED_ACTION_BAR")
@JsonPropertyOrder({"defaultTime"})
@Getter
@Accessors(chain = true)
public class AnimatedActionBarMessage extends SoundableMessage {

    @Getter private static final HashMap<CommandSender, BetterRunnable> animatedActionBarTasks = new HashMap<>();

    @JsonProperty("frames") private ArrayList<ActionBarAnimationData> frames;
    @Setter @JsonProperty("defaultTime") private Integer defaultTime = null;

    @JsonCreator
    public AnimatedActionBarMessage(@NotNull @JsonProperty("frames") List<ActionBarAnimationData> frames) {
        this.frames = new ArrayList<>(frames);
    }

    @Override
    public AnimatedActionBarMessage sendToInternal(@NotNull CommandSender recipient) {
        applySound(recipient);
        applyCommands(recipient);

        if(frames.isEmpty()) return this;

        List<ActionBarAnimationData> frameList = frames;
        AtomicInteger previousFrame = new AtomicInteger(0);
        AtomicInteger currentFrame = new AtomicInteger(0);
        AtomicInteger allFramesTime = new AtomicInteger(0);
        AtomicBoolean startKeepMessageVisible = new AtomicBoolean(false);

        if(animatedActionBarTasks.containsKey(recipient)) // prevent multiple animations for one sender
            animatedActionBarTasks.get(recipient).stop();

        animatedActionBarTasks.put(recipient, new BetterRunnable(plugin, task -> {
            if(recipient instanceof Player player && !player.isOnline()) {
                task.stop();
                animatedActionBarTasks.remove(recipient);
                return;
            }

            int frameId = currentFrame.get();

            if(frameId >= frameList.size()) {
                task.stop();
                animatedActionBarTasks.remove(recipient);
                return;
            }

            ActionBarAnimationData frame = frameList.get(frameId);
            int frameTime = frame.getTime() != null ? frame.getTime() : (defaultTime != null ? defaultTime : 1);
            int time = (allFramesTime.get() + frameTime);

            if(startKeepMessageVisible.get() && task.getExecutions() % 20 == 0) { // refresh every 1.5 seconds actionbar to keep visible longer frames
                ActionBarAnimationData previousFrameToKeep = frameList.get(previousFrame.get());

                sendActionBar(recipient, Utils.hexComponent(previousFrameToKeep.getMessage()));
            }

            if(task.getExecutions() < time) return;

            startKeepMessageVisible.set(true);
            sendActionBar(recipient, Utils.hexComponent(frame.getMessage()));
            currentFrame.incrementAndGet();
            previousFrame.set(currentFrame.get() - 1);
            allFramesTime.addAndGet(frameTime);
        }, 0, 1));
        return this;
    }

    @Override
    public AnimatedActionBarMessage replace(@NotNull String toReplace, @NotNull String replaced) {
        AnimatedActionBarMessage cloned = this.clone();
        List<ActionBarAnimationData> replacedFrames = new ArrayList<>();

        for(ActionBarAnimationData frame : cloned.getFrames()) {
            ActionBarAnimationData newFrame = frame.clone();

            if(newFrame.getMessage() != null)
                newFrame.setMessage(newFrame.getMessage().replace(toReplace, replaced));

            replacedFrames.add(newFrame);
        }

        cloned.getFrames().clear();
        cloned.getFrames().addAll(replacedFrames);
        return cloned;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AnimatedActionBarMessage clone() {
        AnimatedActionBarMessage cloned = (AnimatedActionBarMessage) super.clone();

        cloned.frames = (ArrayList<ActionBarAnimationData>) frames.clone();
        return cloned;
    }

}
