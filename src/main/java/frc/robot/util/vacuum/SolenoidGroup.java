package frc.robot.util.vacuum;

import edu.wpi.first.wpilibj.Solenoid;

public class SolenoidGroup {
    private final Solenoid[] group;

    public SolenoidGroup(Solenoid... solenoids) {
        this.group = solenoids;
    }

    public void set(boolean val) {
        for (Solenoid s : group) {
            s.set(val);
        }
    }

    public boolean get() {
        return group[0].get();
    }
}
