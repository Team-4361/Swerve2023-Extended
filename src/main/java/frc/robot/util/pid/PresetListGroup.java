package frc.robot.util.pid;

import java.util.HashMap;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.PrintCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

import static frc.robot.Constants.TEST_MODE;

public class PresetListGroup extends HashMap<String, PresetList> {
    private int index = 0;
    private String[] defaultSyncOrder;

    public PresetListGroup addPreset(String name, PresetList extensionPresets) {
        this.put(name, extensionPresets);
        return this;
    }

    public PresetListGroup setDefaultSyncOrder(String... order) {
        this.defaultSyncOrder = order;
        return this;
    }

    public Command setPresetCommand(int index) {
        return Commands.runOnce(() -> setPreset(index));
    }

    public Double getCurrentPreset(String name) {
        return get(name).getCurrentPreset();
    }

    public PresetListGroup setPreset(int index) {
        this.index = index;
        this.forEach((name, preset) -> preset.setPreset(index));
        new PrintCommand("SETTING PRESET TO " + index).schedule();
        return this;
    }

    public Command setPresetSyncCommand(int index) {
        return setPresetSyncCommand(index, defaultSyncOrder);
    }

    public Command setPresetSyncCommand(int index, String[] order) {
        if (order.length == 0) return setPresetCommand(index);

        SequentialCommandGroup group = new SequentialCommandGroup();

        for (String name : order) {
            group.addCommands(get(name).awaitPresetCommand(index));
        }
        return group;
    }

    public void updateDashboard() {
        if (TEST_MODE) {
            forEach((name, presetList) -> presetList.updateDashboard(name));
        }
    }

    public PresetListGroup nextPreset() {
        if (index+1 <= size()) {
            setPreset(index+1);
        }
        return this;
    }

    public PresetListGroup prevPreset() {
        if (index-1 >= 0) {
            setPreset(index-1);
        }
        return this;
    }
}
