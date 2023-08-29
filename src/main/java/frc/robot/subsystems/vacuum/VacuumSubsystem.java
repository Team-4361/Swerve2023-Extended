package frc.robot.subsystems.vacuum;

import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.util.vacuum.SolenoidGroup;
import frc.robot.util.vacuum.VacuumSensorGroup;

import static frc.robot.Constants.Global.TEST_MODE;
import static frc.robot.Constants.VacuumValues.*;

public class VacuumSubsystem extends SubsystemBase {
    public static final PneumaticsModuleType MODULE_TYPE = PneumaticsModuleType.CTREPCM;

    private final MotorControllerGroup motor;

    public VacuumSensorGroup sensors;
    public SolenoidGroup solenoids;

    private boolean vacEnabled = false;

    public void toggleVacuum() {
        if (motor.get() == 0) {
            activate();
        } else if (motor.get() != 0) {
            deactivate();
        }
    }

    public VacuumSubsystem() {
        this.motor = new MotorControllerGroup(
            new CANSparkMax(VACUUM_MOTOR_IDS[0], VACUUM_MOTOR_TYPE),
            new CANSparkMax(VACUUM_MOTOR_IDS[1], VACUUM_MOTOR_TYPE),
            new CANSparkMax(VACUUM_MOTOR_IDS[2], VACUUM_MOTOR_TYPE),
            new CANSparkMax(VACUUM_MOTOR_IDS[3], VACUUM_MOTOR_TYPE)
        );

        solenoids = new SolenoidGroup(
            new Solenoid(0, MODULE_TYPE, VACUUM_SOLENOIDS[0][0]),
            new Solenoid(0, MODULE_TYPE, VACUUM_SOLENOIDS[0][1]),
            new Solenoid(1, MODULE_TYPE, VACUUM_SOLENOIDS[1][0]),
            new Solenoid(1, MODULE_TYPE, VACUUM_SOLENOIDS[1][1])
        );

        sensors = new VacuumSensorGroup(VACUUM_SENSORS);
    }

    public Command openVacuumCommand() {
        return new ParallelRaceGroup(
                Commands.run(() -> {
                    // disable the vacuum until the solenoid shuts off.
                    motor.set(0);
                    solenoids.set(true);
                }),
                new WaitCommand(2)
        ).andThen(Commands.runOnce(() -> {
            solenoids.set(false);
            if (vacEnabled) {
                motor.set(VACUUM_PUMP_SPEED);
            }
        }));
    }

    public void activate() {
        motor.set(VACUUM_PUMP_SPEED);
        vacEnabled = true;
    }

    public void deactivate() {
        motor.set(0);
        vacEnabled = false;
    }

    public Command activateCommand() { return runOnce(this::activate); }
    public Command deactivateCommand() { return runOnce(this::deactivate); }

    /**
     * This method is called periodically by the {@link CommandScheduler}. Useful for updating
     * subsystem-specific state that you don't want to offload to a {@link Command}. Teams should try
     * to be consistent within their own codebases about which responsibilities will be handled by
     * Commands, and which will be handled here.
     */
    @Override
    public void periodic() {
        SmartDashboard.putBoolean("Vacuum: Running", motor.get() != 0);

        if (TEST_MODE) {
            SmartDashboard.putNumber("Vacuum: Power", motor.get());
        }

        SmartDashboard.putNumber("Vacuum: Sensor 1", sensors.get(0).getVoltage());
        SmartDashboard.putNumber("Vacuum: Sensor 2", sensors.get(1).getVoltage());
        SmartDashboard.putNumber("Vacuum: Sensor 3", sensors.get(2).getVoltage());
        SmartDashboard.putNumber("Vacuum: Sensor 4", sensors.get(3).getVoltage());

        SmartDashboard.putBoolean("Vacuum: Bound", sensors.isAnyBound());
        SmartDashboard.putBoolean("Vacuum: Solenoid", solenoids.get());
    }
}


