package frc.robot.util.pid;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.PrintCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

import java.util.HashMap;

public class PresetMapGroup<T> extends HashMap<String, PresetMap<T>> {
    private String index = "";
    private String[] sequence;

    public PresetMapGroup<T> addPreset(String name, PresetMap<T> presetMap) {
        this.put(name, presetMap);
        return this;
    }

    public PresetMapGroup<T> setSequenceOrder(String... order) {
        this.sequence = order;
        return this;
    }

    public Command setPresetCommand(String name) {
        return Commands.runOnce(() -> setPreset(name));
    }

    public T getCurrentPreset(String name) {
        return get(name).getCurrentPreset();
    }

    public void setPreset(String name) {
        this.index = name;
        this.forEach((n, preset) -> preset.setPreset(name));
    }

    public Command setPresetSyncCommand(String name) {
        return setPresetSyncCommand(name, sequence);
    }

    public Command setPresetSyncCommand(String name, String[] order) {
        if (order.length == 0) return setPresetCommand(index);

        SequentialCommandGroup group = new SequentialCommandGroup();

        for (String n : order) {
            group.addCommands(get(n).awaitPresetCommand(name));
        }
        return group;
    }
}
