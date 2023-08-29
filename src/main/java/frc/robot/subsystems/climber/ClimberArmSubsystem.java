package frc.robot.subsystems.climber;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Robot;
import frc.robot.util.pid.SparkMaxAngledPIDSubsystem;
import frc.robot.util.pid.SparkMaxDistancePIDSubsystem;
import frc.robot.util.pid.SparkMaxPIDSubsystem;

import static frc.robot.Constants.ClimberArmValues.*;
import static frc.robot.Constants.ClimberPresets.*;

public class ClimberArmSubsystem extends SubsystemBase {
    private final SparkMaxPIDSubsystem extension;
    private final SparkMaxAngledPIDSubsystem rotation;

    public ClimberArmSubsystem() {
        this.extension = new SparkMaxDistancePIDSubsystem(EXTENSION_NAME, EXTENSION_LIMIT, EXTENSION_MOTOR_ID);
        this.rotation = new SparkMaxAngledPIDSubsystem(ROTATION_NAME, ROTATION_GEAR_RATIO, ROTATION_MOTOR_ID);
    
        extension.setPresetMap(CLIMBER_PRESET_GROUP.get(EXTENSION_NAME), () -> CLIMBER_PRESET_GROUP.getCurrentPreset(EXTENSION_NAME));
        rotation.setPresetMap(CLIMBER_PRESET_GROUP.get(EXTENSION_NAME), () -> CLIMBER_PRESET_GROUP.getCurrentPreset(ROTATION_NAME));

        extension.setPIDControlSupplier(() -> Robot.pidControlEnabled);
        rotation.setPIDControlSupplier(() -> Robot.pidControlEnabled);

        extension.setLimitBypassSupplier(() -> Robot.limitSwitchBypass);

        extension.setPID(0.05, 0, 0.01);
        rotation.setPID(0.04, 0, 0.01); // Working in competition 3/11/23

        extension.setTolerance(0.25);
    }

    public SparkMaxPIDSubsystem getExtension() { return extension; }
    public SparkMaxAngledPIDSubsystem getRotation() { return rotation; }
}
