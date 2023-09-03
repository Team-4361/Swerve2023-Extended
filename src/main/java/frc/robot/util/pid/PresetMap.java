package frc.robot.util.pid;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import frc.robot.util.math.TimeoutCommand;

import java.util.*;
import java.util.function.Supplier;

public class PresetMap<T> extends LinkedHashMap<String, T> {
    private String index;

    private ArrayList<PresetEventListener<T>> listeners;
    private Supplier<Boolean> targetSupplier;

    public PresetMap() {
        super();
        this.listeners = new ArrayList<>();
        this.targetSupplier = () -> true;
    }

    public PresetMap(Map<String, T> entries) {
        super();
        this.listeners = new ArrayList<>();
        this.targetSupplier = () -> true;
        this.clear();
        this.putAll(entries);
    }

    public boolean reachedTarget() { return targetSupplier.get(); }

    public T getPreset(String name) {
        if (name.equals("")) {
            Optional<Map.Entry<String, T>> o = entrySet().stream().findFirst();
            return o.map(Map.Entry::getValue).orElse(null);
        } else {
            return get(name);
        }
    }

    public T getCurrentPreset() { return getPreset(index); }

    public Command setPresetCommand(String name) {
        return Commands.runOnce(() -> setPreset(name));
    }

    public Command awaitPresetCommand(String name) {
        return new TimeoutCommand(new FunctionalCommand(
                () -> setPreset(name),
                () -> {},
                (i) -> {},
                this::reachedTarget
        ), 1.5);
    }

    public void setPreset(String name) {
        this.index = name;
        updateListener();
    }

    private void updateListener() {
        listeners.forEach(((listener) -> listener.onPresetAdjust(getPreset(index))));
    }

    public void addListener(PresetEventListener<T> listener) {
        listeners.add(listener);
    }

}
