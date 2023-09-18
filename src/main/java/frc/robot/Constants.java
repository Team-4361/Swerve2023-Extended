package frc.robot;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import frc.robot.util.math.*;
import frc.robot.util.pid.PresetMap;
import frc.robot.util.pid.TunablePIDController;
import frc.robot.util.swerve.SwerveChassis;
import frc.robot.util.swerve.SwerveModule;
import frc.robot.util.swerve.SwerveModuleConfig;

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
        public static final GearRatio DRIVE_RATIO = GearRatio.fromRatio(6.86);
        public static final GearRatio TURN_RATIO = GearRatio.fromRatio(12.8);
        public static final Distance WHEEL_SIZE = Distance.fromInches(4);
        public static final Distance SIDE_LENGTH = Distance.fromInches(26);
        public static final Velocity MAX_VELOCITY = Velocity.fromMPS(4.4);

        public static final PIDController DRIVE_CONTROLLER = new PIDController(0.01, 0, 0);
        public static final PIDController TURN_CONTROLLER = new PIDController(0.02, 0, 0);

        public static final SwerveModuleConfig MODULE_CONFIG = new SwerveModuleConfig(
                MAX_VELOCITY,
                DRIVE_RATIO,
                TURN_RATIO,
                WHEEL_SIZE
        );

        public static final SwerveModule FL_MODULE = new SwerveModule(
                "FL",
                2,
                1,
                0,
                MODULE_CONFIG,
                Rotation2d.fromDegrees(181.45),
                1
        );

        public static final SwerveModule FR_MODULE = new SwerveModule(
                "FR",
                4,
                3,
                1,
                MODULE_CONFIG,
                Rotation2d.fromDegrees(-226.32),
                1
        );

        public static final SwerveModule BL_MODULE = new SwerveModule(
                "BL",
                6,
                5,
                2,
                MODULE_CONFIG,
                Rotation2d.fromDegrees(12.71),
                1
        );

        public static final SwerveModule BR_MODULE = new SwerveModule(
                "BR",
                8,
                7,
                3,
                MODULE_CONFIG,
                Rotation2d.fromDegrees(169.38),
                1
        );
    }

    public static class VacuumValues {
        public static final int[] VACUUM_MOTOR_IDS = new int[]{20, 16, 13, 11};
        public static final double VACUUM_PUMP_SPEED = 0.45;
        public static final MotorType VACUUM_MOTOR_TYPE = MotorType.kBrushed;

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

        public static final PresetMap<Double> ROTATION_PRESETS = new PresetMap<>(
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

        public static final PresetMap<Double> EXTENSION_PRESETS = new PresetMap<>(
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

        public static final PresetMap<Double> WRIST_PRESETS = new PresetMap<>(
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

        public static final PresetMap<String> a = new PresetMap<>();
    }

    public static class AutoValues {
        // fancy calculus type stuff, not sure what to do with it but play with the numbers ;)
        public static final PIDController X_CONTROLLER = new PIDController(0.1, 0, 0);
        public static final PIDController Y_CONTROLLER = new PIDController(0.1, 0, 0);
        public static final ProfiledPIDController HEADING_CONTROLLER = new ProfiledPIDController(0.01, 0, 0,
                new TrapezoidProfile.Constraints(0.5, 0.5));

        public static final TunablePIDController PITCH_CONTROLLER = new TunablePIDController("Charge Pitch",
                0.0081, 0.0, 0.0);
    }
}
