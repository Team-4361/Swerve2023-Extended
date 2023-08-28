package frc.robot.util.pid;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import frc.robot.commands.auto.TimeoutCommand;

import java.util.*;
import java.util.function.Supplier;

public class PresetMap extends LinkedHashMap<String, Double> {
    private String index = "";
    private String name = "";
    private boolean dashAdded = false;

    private ArrayList<PresetEventListener> listeners;
    private Supplier<Boolean> targetSupplier;

    public PresetMap() {
        super();
        this.listeners = new ArrayList<>();
        this.targetSupplier = () -> true;
    }

    public PresetMap(Map<String, Double> entries) {
        super();
        this.listeners = new ArrayList<>();
        this.targetSupplier = () -> true;
        this.clear();
        this.putAll(entries);
    }

    public boolean reachedTarget() { return targetSupplier.get(); }


    public double getPreset(String name) {
        if (name.equals("")) {
            Optional<Map.Entry<String, Double>> o = entrySet().stream().findFirst();
            return o.isPresent() ? o.get().getValue() : 0;
        } else {
            return get(name);
        }
    }

    public double getCurrentPreset() { return getPreset(index); }

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

    public PresetMap setPreset(String name) {
        this.index = name;
        updateListener();
        return this;
    }

    private void updateListener() {
        listeners.forEach(((listener) -> listener.onPresetAdjust(getPreset(index))));
    }

    public PresetMap addListener(PresetEventListener listener) {
        listeners.add(listener);
        return this;
    }
}
