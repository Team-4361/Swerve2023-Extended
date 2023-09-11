package frc.robot;

import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.SerialPort;
import frc.robot.swerve.encoders.PWMDutyCycleEncoderSwerve;
import frc.robot.swerve.imu.NavXSwerve;
import frc.robot.swerve.math.Matter;
import frc.robot.swerve.motors.SparkMaxSwerve;
import frc.robot.swerve.parser.*;
import frc.robot.util.math.Distance;
import frc.robot.util.math.GearRatio;
import frc.robot.util.math.PeakMotorDistance;
import frc.robot.util.math.Velocity;
import frc.robot.util.pid.PresetMap;
import frc.robot.util.pid.PresetMapGroup;
import frc.robot.util.pid.TunablePIDController;

import static frc.robot.util.math.DistanceUnit.INCHES;
import static java.util.Map.entry;
import static java.util.Map.ofEntries;

public class Constants {
    public static class Global {
        public static boolean TEST_MODE = true;
    }

    public static class Control {
        public static final int LEFT_STICK_ID = 0;
        public static final int RIGHT_STICK_ID = 1;
        public static final int XBOX_CONTROLLER_ID = 2;
    }

    public static class Chassis {
        public static final PIDFConfig DRIVE_PID = new PIDFConfig(0.01, 0, 0);
        public static final PIDFConfig TURN_PID = new PIDFConfig(0.01, 0, 0);
        public static final PIDFConfig JOYSTICK_PID = new PIDFConfig(0.4, 0, 0.1);
        public static final Velocity MAX_SPEED = Velocity.fromMPS(4.4);

        public static final PIDController AUTO_BALANCE_PID = new PIDController(0.05, 0, 0);

        public static final double ROBOT_MASS_KG = 36;

        public static final Matter MATTER = new Matter(new Translation3d(0, 0, Units.inchesToMeters(8)), ROBOT_MASS_KG);

        public static final SwerveModulePhysicalCharacteristics SWERVE_MODULE = new SwerveModulePhysicalCharacteristics(
                6.86,
                12.8,
                0.1,
                1,
                1,
                1,
                4096
        );

        public static final SwerveModuleConfiguration FL_MODULE = new SwerveModuleConfiguration(
                new SparkMaxSwerve(2, true),
                new SparkMaxSwerve(1, false),
                new PWMDutyCycleEncoderSwerve(0),
                181.45,
                0.33,
                0.33,
                DRIVE_PID,
                TURN_PID,
                MAX_SPEED.toMPS(),
                SWERVE_MODULE,
                "FL"
        );

        public static final SwerveModuleConfiguration FR_MODULE = new SwerveModuleConfiguration(
                new SparkMaxSwerve(4, true),
                new SparkMaxSwerve(3, false),
                new PWMDutyCycleEncoderSwerve(1),
                -226.32,
                0.33,
                0.33,
                DRIVE_PID,
                TURN_PID,
                MAX_SPEED.toMPS(),
                SWERVE_MODULE,
                "FR"
        );

        public static final SwerveModuleConfiguration BL_MODULE = new SwerveModuleConfiguration(
                new SparkMaxSwerve(6, true),
                new SparkMaxSwerve(5, false),
                new PWMDutyCycleEncoderSwerve(2),
                12.71,
                0.33,
                0.33,
                DRIVE_PID,
                TURN_PID,
                MAX_SPEED.toMPS(),
                SWERVE_MODULE,
                "BL"
        );

        public static final SwerveModuleConfiguration BR_MODULE = new SwerveModuleConfiguration(
                new SparkMaxSwerve(8, true),
                new SparkMaxSwerve(7, false),
                new PWMDutyCycleEncoderSwerve(3),
                169.38,
                0.33,
                0.33,
                DRIVE_PID,
                TURN_PID,
                MAX_SPEED.toMPS(),
                SWERVE_MODULE,
                "BR"
        );

        public static final SwerveDriveConfiguration DRIVE_CONFIG = new SwerveDriveConfiguration(
                new SwerveModuleConfiguration[]{
                        FL_MODULE, FR_MODULE, BL_MODULE, BR_MODULE
                },
                new NavXSwerve(SerialPort.Port.kMXP),
                MAX_SPEED.toMPS(),
                true // navx is inverted
        );

        public static final SwerveControllerConfiguration CONTROLLER_CONFIG = new SwerveControllerConfiguration(
                DRIVE_CONFIG,
                JOYSTICK_PID
        );
    }

    public static class VacuumValues {
        public static final int[] VACUUM_MOTOR_IDS = new int[]{20, 16, 13, 11};
        public static final double VACUUM_PUMP_SPEED = 0.45;
        public static final CANSparkMaxLowLevel.MotorType VACUUM_MOTOR_TYPE = CANSparkMaxLowLevel.MotorType.kBrushed;

        public static final int[][] VACUUM_SOLENOIDS = new int[][]{
                new int[]{1, 7}, // PDH 0
                new int[]{3, 4} // PDH 1
        };

        public static final int[] VACUUM_SENSORS = new int[]{0, 1, 2, 3};

        public static final double VACUUM_THRESHOLD = 1;

    }

    public static class ClimberWristValues {
        public static final int WRIST_GEAR_RATIO = 100;
        public static final int WRIST_MOTOR_ID = 22;
    }

    public static class ClimberArmValues {
        public static final int ROTATION_MOTOR_ID = 10;
        public static final int EXTENSION_MOTOR_ID = 21;

        public static final GearRatio ROTATION_GEAR_RATIO = GearRatio.fromRatio(735);

        public static final PeakMotorDistance EXTENSION_LIMIT = new PeakMotorDistance(
                Distance.fromValue(54.0, INCHES),
                94.09
        );
        // old was 88 rot and 50.5 inches

        public static final double WRIST_ROLLOVER_VALUE = 13180;
    }

    public static class ClimberPresets {
        public static final String ROTATION_NAME = "CLI ROT";
        public static final String EXTENSION_NAME = "CLI EXT";
        public static final String WRIST_NAME = "CLIM WST";

        public static final String ZERO_POSITION_NAME = "ZERO_POSITION_INDEX";
        public static final String HUMAN_STATION_NAME = "HUMAN_STATION_INDEX";
        public static final String FLOOR_CONE_NAME = "FLOOR_CONE_INDEX";
        public static final String MID_CONE_NAME = "MID_CONE_INDEX";
        public static final String HIGH_CONE_NAME = "HIGH_CONE_INDEX";
        public static final String FLOOR_CUBE_NAME = "FLOOR_CUBE_INDEX";
        public static final String MANUAL_STATION_NAME = "MANUAL_STATION_INDEX";
        public static final String GRAB_FLOOR_CUBE_NAME = "GRAB_FLOOR_CUBE_INDEX";

        public static final PresetMap<Double> ROTATION_PRESETS = new PresetMap<Double>(
                ofEntries(
                        entry(ZERO_POSITION_NAME, 0.0),
                        entry(HUMAN_STATION_NAME, -45.0),
                        entry(FLOOR_CONE_NAME, -143.0),
                        entry(MID_CONE_NAME, -57.0),
                        entry(HIGH_CONE_NAME, -59.0),
                        entry(FLOOR_CUBE_NAME, -102.0),
                        entry(MANUAL_STATION_NAME, -66.0),
                        entry(GRAB_FLOOR_CUBE_NAME, -143.0)
                )
        );

        public static final PresetMap<Double> EXTENSION_PRESETS = new PresetMap<Double>(
                ofEntries(
                        entry(ZERO_POSITION_NAME, 0.0),
                        entry(HUMAN_STATION_NAME, 17.365),
                        entry(FLOOR_CONE_NAME, 6.0),
                        entry(MID_CONE_NAME, 9.755),
                        entry(HIGH_CONE_NAME, 49.753),
                        entry(FLOOR_CUBE_NAME, 12.0),
                        entry(MANUAL_STATION_NAME, 0.0),
                        entry(GRAB_FLOOR_CUBE_NAME, 12.0)
                )
        );

        public static final PresetMap<Double> WRIST_PRESETS = new PresetMap<Double>(
                ofEntries(
                        entry(ZERO_POSITION_NAME, 0.0),
                        entry(HUMAN_STATION_NAME, -38.0),
                        entry(FLOOR_CONE_NAME, 56.0),
                        entry(MID_CONE_NAME, 10.0),
                        entry(HIGH_CONE_NAME, 0.685),
                        entry(FLOOR_CUBE_NAME, -75.0),
                        entry(MANUAL_STATION_NAME, 21.0),
                        entry(GRAB_FLOOR_CUBE_NAME, -75.0)
                )
        );

        public static final PresetMapGroup<Double> CLIMBER_PRESET_GROUP = new PresetMapGroup<Double>()
                .addPreset(ROTATION_NAME, ROTATION_PRESETS)
                .addPreset(EXTENSION_NAME, EXTENSION_PRESETS)
                .addPreset(WRIST_NAME, WRIST_PRESETS);
    }
}
