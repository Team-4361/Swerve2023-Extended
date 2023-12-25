package frc.robot.util.preset;

import frc.robot.util.io.IOManager;
import frc.robot.util.io.Looper;

import java.time.Duration;
import java.util.ArrayList;


/**
 * This {@link PresetGroup} is designed to handle multiple {@link IPresetContainer} interfaces.
 *
 * @author Eric Gold
 * @since 0.0.1
 * @version 0.0.1
 */
public class PresetGroup extends ArrayList<IPresetContainer> implements IPresetContainer {
    private final String name;
    private final PresetMode mode;
    private final Looper seqLooper;

    private int index;
    private int seqIndex;
    private boolean finished;

    /**
     * Constructs a new {@link PresetGroup} with the specified Type and Name.
     *
     * @param name The name of the {@link PresetGroup}.
     */
    public PresetGroup(String name, PresetMode mode) {
        this.name = name;
        this.mode = mode;
        this.index = 0;
        this.seqIndex = 0;
        this.finished = false;

        if (mode == PresetMode.SEQUENTIAL) {
            this.seqLooper = IOManager.getLoop(name + "-SEQ")
                    .setInterval(Duration.ofMillis(100))
                    .setEndDelay(Duration.ofMillis(250))
                    .addInit(() -> seqIndex = 0);

            seqLooper.addPeriodic(() -> {
                if (seqIndex < 0 || seqIndex > size()-1) {
                    finished = true;
                    seqLooper.stop(); // End the Looper if an Exception will be thrown.
                }
                IPresetContainer inst = get(seqIndex);
                if (inst.getIndex() != index) {
                    // Set the preset if not currently set; then wait until finished.
                    inst.setPreset(index);
                }
                // Check right now to eliminate the need for another Periodic cycle if true
                // will instantly be thrown.
                if (inst.isFinished()) {
                    if (seqIndex >= size()-1) {
                        // We reached the last element; end the Loop.
                        finished = true;
                        seqLooper.stop();
                        return;
                    }
                    // Otherwise, advance to the next position.
                    seqIndex++;
                }
            });
            return;
        }
        this.seqLooper = null;
    }

    /** @return The name of the {@link PresetGroup} */
    @Override public String getName() { return name; }

    /** The currently selected Preset Index. */
    @Override public int getIndex() { return index; }

    /** The maximum Preset Index which can be chosen. */
    @Override public int getMaxIndex() {
        return stream()
                .mapToInt(IPresetContainer::getMaxIndex)
                .max()
                .orElse(0);
    }

    /** @return The {@link PresetMode} in which this {@link PresetGroup} is operating in. */
    public PresetMode getMode() { return this.mode; }

    /**
     * Attempts to set the Preset to the specific Index.
     *
     * @param idx The Index to change the Preset to.
     * @return True if the operation was successful; false otherwise.
     */
    @Override
    public boolean setPreset(int idx) {
        if (idx < 0 || idx > getMaxIndex())
            return false;

        this.index = idx;
        this.finished = false;

        // If the mode is SEQUENTIAL, we need to run a Looper to check for "isFinished" events.
        if (mode == PresetMode.SEQUENTIAL) {
            if (seqLooper == null)
                return false;
            seqLooper.run();
            return true;
        }
        for (IPresetContainer c : this) {
            if (!c.setPreset(idx))
                return false;
        }

        this.finished = true;
        return true;
    }

    /**
     * Advances the Preset Container to the next option.
     *
     * @return True if the operation was successful; false otherwise.
     */
    @Override
    public boolean nextPreset() {
        if (index + 1 > getMaxIndex())
            return false;
        index++;
        return setPreset(index);
    }

    /**
     * Declines the Preset Container to the previous option.
     *
     * @return True if the operation was successful; false otherwise.
     */
    @Override
    public boolean backPreset() {
        if (index - 1 < 0)
            return false;
        index--;
        return setPreset(index);
    }

    /**
     * @return If the {@link IPresetContainer} has finished moving position; used for sequential effects.
     */
    @Override public boolean isFinished() { return finished; }
}
