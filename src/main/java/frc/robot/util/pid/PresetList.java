package frc.robot.util.pid;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.commands.auto.TimeoutCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

public class PresetList<T> extends ArrayList<T> {
    private int index = 0;
    private String name = "";
    private boolean dashAdded = false;

    private final ArrayList<PresetEventListener<T>> listeners = new ArrayList<>();
    private Supplier<Boolean> targetReachedSupplier = () -> true;

    @SafeVarargs
    public PresetList(T... elements) {
        this.addAll(Arrays.asList(elements));
    }

    public PresetList<T> setTargetReachedSupplier(Supplier<Boolean> supplier) {
        this.targetReachedSupplier = supplier;
        return this;
    }

    public boolean hasReachedTarget() {
        return targetReachedSupplier.get();
    }

    public T getCurrentPreset() {
        return getPreset(index);
    }

    public T getPreset(int idx) {
        return get(MathUtil.clamp(idx, 0, size()-1));
    }

    public Command setPresetCommand(int preset) {
        return Commands.runOnce(() -> setPreset(preset));
    }

    public Command awaitPresetCommand(int preset) {
        return new TimeoutCommand(new FunctionalCommand(
                () -> setPreset(preset),
                () -> {},
                (i) -> {},
                this::hasReachedTarget
        ), 1.5);
    }

    public void setPreset(int index) {
        this.index = index;
        updateListener();
    }

    private void updateListener() {
        listeners.forEach(((listener) -> listener.onPresetAdjust(getPreset(index))));
    }

    public PresetList<T> nextPreset() {
        if (index+1 <= size()-1) {
            index++;
        }
        updateListener();
        return this;
    }

    public PresetList<T> prevPreset() {
        if (index-1 >= 0) {
            index--;
        }
        updateListener();
        return this;
    }

    private String getDashboardName(int element) {
        return name + " | Preset " + element;
    }

    public void updateDashboard(String name) {
        /*
        if (TEST_MODE) {
            assert !Objects.equals(name, "");
            this.name = name;
            if (!dashAdded) {
                // Add the initial values to the SmartDashboard.
                for (int i = 0; i < size(); i++) {
                    SmartDashboard.putNumber(getDashboardName(i), getPreset(i));
                }
                dashAdded = true;
            } else {
                // Pull the values from the SmartDashboard.
                for (int i = 0; i < size(); i++) {
                    set(i, SmartDashboard.getNumber(getDashboardName(i), get(i)));
                }
            }
        }
         */
    }

    public void addListener(PresetEventListener<T> listener) {
        listeners.add(listener);
    }
}
