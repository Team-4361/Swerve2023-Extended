package frc.robot.util.pid;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.PrintCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

import java.util.ArrayList;
import java.util.HashMap;

import static frc.robot.Constants.TEST_MODE;

public class PresetMapGroup extends HashMap<String, PresetMap> {
    private String index = "";
    private String[] defaultSyncOrder;
    public PresetMapGroup addPreset(String name, PresetMap presetMap) {
        this.put(name, presetMap);
        return this;
    }

    public PresetMapGroup setDefaultSyncOrder(String... order) {
        this.defaultSyncOrder = order;
        return this;
    }
    public Command setPresetCommand(String name) {
        return Commands.runOnce(() -> setPreset(name));
    }

    public Double getCurrentPreset(String name) {
        return get(name).getCurrentPreset();
    }

    public PresetMapGroup setPreset(String name) {
        this.index = name;
        this.forEach((n, preset) -> preset.setPreset(name));
        new PrintCommand("SETTING PRESET TO " + index).schedule();
        return this;
    }

    public Command setPresetSyncCommand(String name) {
        return setPresetSyncCommand(name, defaultSyncOrder);
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
