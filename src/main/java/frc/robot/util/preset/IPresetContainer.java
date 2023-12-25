package frc.robot.util.preset;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

/**
 * This interface enables {@link PresetMap} consistency and allows chaining of
 * multiple synchronized {@link PresetGroup}s -- similar logic to {@link SequentialCommandGroup} implementing
 * the {@link Command} interface.
 *
 * @author Eric Gold
 * @since 0.0.1
 */
public interface IPresetContainer {
    /** @return The name of the {@link IPresetContainer} */
    String getName();

    /** The currently selected Preset Index. */
    int getIndex();

    /** The maximum Preset Index which can be chosen. */
    int getMaxIndex();

    /**
     * Attempts to set the Preset to the specific Index.
     * @param idx The Index to change the Preset to.
     * @return True if the operation was successful; false otherwise.
     */
    boolean setPreset(int idx);

    /**
     * Advances the Preset Container to the next option.
     * @return True if the operation was successful; false otherwise.
     */
    boolean nextPreset();

    /**
     * Declines the Preset Container to the previous option.
     * @return True if the operation was successful; false otherwise.
     */
    boolean backPreset();

    /**
     * @return If the {@link IPresetContainer} has finished moving position; used for sequential effects.
     */
    boolean isFinished();
}
