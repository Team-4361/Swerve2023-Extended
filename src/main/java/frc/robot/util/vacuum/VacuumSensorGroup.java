package frc.robot.util.vacuum;

import edu.wpi.first.wpilibj.AnalogInput;

import java.util.ArrayList;
import java.util.Arrays;

import static frc.robot.Constants.VacuumValues.VACUUM_THRESHOLD;

public class VacuumSensorGroup extends ArrayList<AnalogInput> {
    public VacuumSensorGroup(int... ids) {
        for (int id : ids) {
            add(new AnalogInput(id));
        }
    }

    public VacuumSensorGroup(AnalogInput... inputs) { addAll(Arrays.asList(inputs)); }

    public double getVoltage(int id) { return get(id).getVoltage(); }
    public boolean hasVacuum(int id) { return hasVacuum(get(id)); }

    public static boolean hasVacuum(AnalogInput input) { return input.getVoltage() <= VACUUM_THRESHOLD; }

    public boolean isAnyBound() {
        for (AnalogInput input : this) {
            if (hasVacuum(input))
                return true;
        }
        return false;
    }

    public boolean[] getBoundResults() {
        boolean[] results = new boolean[this.size()];
        for (int i=0; i<size(); i++) {
            results[i] = hasVacuum(i);
        }
        return results;
    }

    public double[] getVoltageResults() {
        double[] results = new double[this.size()];
        for (int i=0; i<size(); i++) {
            results[i] = getVoltage(i);
        }
        return results;
    }
}
