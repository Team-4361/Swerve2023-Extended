package frc.robot.util.preset;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * This {@link PresetMap} is designed to hold various fixed positions of a mechanism. Functionality
 * is almost identical to a HashMap, except for (a) elements are stored in a consistent order -- and (b)
 * the index is saved and noted with various supporting methods.
 *
 * @param <T> The type of values to hold.
 * @since 0.0.1
 * @version 0.0.1
 */
public class PresetMap<T> extends LinkedHashMap<String, T> implements IPresetContainer {
    private final String name;
    private final ArrayList<IPresetListener<T>> listeners;

    private Supplier<Boolean> completeSupplier;
    private long completeDelayMs;
    private long startMs = 0;
    private int index;

    /**
     * Constructs a new {@link PresetMap} with the specified Type and Name.
     *
     * @param name The name of the {@link PresetMap}.
     */
    public PresetMap(String name) {
        this.name = name;
        this.index = 0;
        this.listeners = new ArrayList<>();
        this.completeSupplier = null;
        this.completeDelayMs = 0;
    }

    /**
     * Sets the {@link Supplier} used to determine if {@link #isFinished()} is true.
     * @param supplier The Boolean {@link Supplier} to use.
     * @return The current {@link PresetMap} with the modification.
     */
    public PresetMap<T> setFinishedSupplier(Supplier<Boolean> supplier) {
        this.completeSupplier = supplier;
        return this;
    }

    /**
     * Sets the Delay used to determine if {@link #isFinished()} is true;
     * @param millis The Delay in milliseconds.
     * @return The current {@link PresetMap} with the modification.
     */
    public PresetMap<T> setFinishedDelay(long millis) {
        this.completeDelayMs = millis;
        return this;
    }

    /** @return The <b>optional</b> {@link Supplier} used to determine if {@link #isFinished()} is true. */
    public Optional<Supplier<Boolean>> getFinishedSupplier() {
        return Optional.ofNullable(completeSupplier);
    }

    /**
     * @return The delay used to determine if {@link #isFinished()} is true;
     * may be combined with {@link #getFinishedSupplier()}.
     */
    public long getFinishedDelay() { return this.completeDelayMs; }

    /**
     * Adds a {@link IPresetListener} to the {@link PresetMap}.
     * @param listener The {@link IPresetListener} to add.
     * @return The current {@link PresetMap} with the modification.
     */
    @SuppressWarnings("UnusedReturnValue")
    public PresetMap<T> addListener(IPresetListener<T> listener) {
        this.listeners.add(listener);
        return this;
    }

    /**
     * @param idx The index to check for.
     * @return The assigned {@link String} name for the Preset or empty {@link String} if invalid.
     */
    public String getPresetName(int idx) {
        int i = 0;
        if (idx < size()) {
            for (String tName : keySet()) {
                if (i == idx) {
                    return tName;
                }
                i++;
            }
        }
        return "";
    }

    private void fireListeners() {
        String name = getPresetName(); // slight optimization; call the loop only once to prevent O(N^2)
        if (name.isEmpty())
            return;
        for (IPresetListener<T> listener : listeners) {
            listener.onPresetAdjust(name, get(name));
        }
    }

    /** @return The name of the {@link IPresetContainer} */
    @Override public String getName() { return name; }

    /** The currently selected Preset Index. */
    @Override public int getIndex() { return index; }

    /** The maximum Preset Index which can be chosen. */
    @Override public int getMaxIndex() { return size()-1; }

    /**
     * Attempts to set the Preset to the specific Index.
     *
     * @param idx The Index to change the Preset to.
     * @return True if the operation was successful; false otherwise.
     */
    @Override
    public boolean setPreset(int idx) {
        if (idx >= size())
            return false;
        this.index = idx;
        this.startMs = System.currentTimeMillis();
        fireListeners();
        return true;
    }

    /**
     * Attempts to set the Preset to the specific Name.
     *
     * @param name The Name to change the Preset to.
     * @return True if the operation was successful; false otherwise.
     */
    public boolean setPreset(String name) {
        int idx = 0;
        for (String tName : keySet()) {
            if (name.equalsIgnoreCase(tName)) {
                index = idx;
                fireListeners();
                return true;
            }
            idx++;
        }
        return false;
    }

    /** @return The name of the currently selected Preset */
    public String getPresetName() {
        return getPresetName(index);
    }

    /**
     * Advances the Preset Container to the next option.
     *
     * @return True if the operation was successful; false otherwise.
     */
    @Override
    public boolean nextPreset() {
        if (index + 1 <= size() - 1) {
            index++;
            setPreset(index);
            return true;
        }
        return false;
    }

    /**
     * Declines the Preset Container to the previous option.
     *
     * @return True if the operation was successful; false otherwise.
     */
    @Override
    public boolean backPreset() {
        if (index - 1 >= 0) {
            index--;
            setPreset(index);
            return true;
        }
        return false;
    }

    /**
     * This method calculates the result based on the Supplier and/or Delay. Here are a few examples:
     * <ul>
     *     <li>
     *         Supplier AND 5000ms --> Supplier MUST be TRUE for AT LEAST 5000ms.
     *     </li>
     *     <li>
     *         1000ms with NO Supplier --> Preset must be set for AT LEAST 1000ms.
     *     </li>
     *     <li>
     *         Supplier AND NO DELAY --> Supplier MUST be TRUE.
     *     </li>
     *     <li>
     *         No Supplier OR Delay --> Returns true if preset has been set
     *         with {@link #setPreset(int)} or similar; false otherwise.
     *     </li>
     * </ul>
     * @return If the {@link IPresetContainer} has finished moving position; used for sequential effects.
     */
    @Override
    public boolean isFinished() {
        if (startMs == 0)
            return false; // no preset has been set yet.
        long elapsedTime = System.currentTimeMillis() - startMs;

        if (completeDelayMs > 0 && completeSupplier != null) { // Both delay and supplier are applicable
            return elapsedTime >= completeDelayMs && completeSupplier.get(); // Require supplier to be enabled for delayMs
        } else if (completeDelayMs > 0) { // Only delay is applicable
            return elapsedTime >= completeDelayMs;
        } else if (completeSupplier != null) { // Only supplier is applicable
            return completeSupplier.get();
        } else {
            // Neither condition is applicable, consider it finished
            return true;
        }
    }
}
