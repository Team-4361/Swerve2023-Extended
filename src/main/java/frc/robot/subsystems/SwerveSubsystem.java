// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.auto.PIDConstants;
import com.pathplanner.lib.auto.SwerveAutoBuilder;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Robot;
import frc.robot.util.io.AlertType;
import frc.robot.util.io.IOManager;
import frc.robot.util.joystick.DriveHIDBase;
import swervelib.SwerveController;
import swervelib.SwerveDrive;
import swervelib.parser.SwerveDriveConfiguration;
import swervelib.parser.SwerveParser;
import swervelib.telemetry.SwerveDriveTelemetry;

import java.io.File;
import java.util.List;
import java.util.Map;

import static frc.robot.Constants.AlertConfig.STRING_SWERVE_CONFIG_FAIL;
import static frc.robot.Constants.DriveConfig.MAX_SPEED_MPS;
import static frc.robot.Constants.DriveConfig.SWERVE_TELEMETRY;

/**
 * This {@link SwerveSubsystem} is heavily based upon the YAGSL example and contains special methods designed
 * to enable easy-to-use driving of the {@link Robot}. Additionally, this class enables manipulation of the
 * Gyroscope -- as it is heavily attached to the driving system.
 *
 * @author Eric Gold
 * @since 0.0.2
 * @version 0.0.2
 */
public class SwerveSubsystem extends SubsystemBase {
    private SwerveDrive swerveDrive;
    private SwerveAutoBuilder autoBuilder = null;
    public boolean teleopFieldOriented = true;
    public boolean teleopClosedLoop = true;

    /** Stops the {@link SwerveDrive} robot from moving. */
    public void stop() {
        setChassisSpeeds(new ChassisSpeeds(0, 0, 0));
    }

    /** @return A {@link Command} which toggles <b>teleoperated</b> FOC */
    public Command toggleFieldOrientedCommand() {
        return Commands.run(() -> teleopFieldOriented = !teleopFieldOriented);
    }

    /** @return A {@link Command} which toggles <b>teleoperated</b> closed loop control. */
    public Command toggleClosedLoopCommand() {
        return Commands.run(() -> teleopClosedLoop = !teleopClosedLoop);
    }

    /**
     * Initialize {@link SwerveDrive} with the directory provided.
     *
     * @param directory Directory of swerve drive config files.
     */
    public SwerveSubsystem(File directory) {
        // Configure the Telemetry before creating the SwerveDrive to avoid unnecessary objects being created.
        SwerveDriveTelemetry.verbosity = SWERVE_TELEMETRY;

        try {
            swerveDrive = new SwerveParser(directory).createSwerveDrive(MAX_SPEED_MPS);

            // Heading correction should only be used while controlling the robot via angle.
            swerveDrive.setHeadingCorrection(false);
        } catch (Exception e) {
            IOManager
                    .getAlert(STRING_SWERVE_CONFIG_FAIL, AlertType.ERROR)
                    .setPersistence(true)
                    .setEnabled(true);
        }
    }

    /**
     * Drives the Robot using one Joystick.
     * @param stick The {@link DriveHIDBase} to use.
     */
    public void teleopDrive(DriveHIDBase stick) {
        // Calculate the maximum speed based on XY and Twist.
        double xS = stick.getRobotX() * MAX_SPEED_MPS;
        double yS = stick.getRobotY() * MAX_SPEED_MPS;
        double tS = stick.getRobotTwist() * swerveDrive.swerveController.config.maxAngularVelocity;

        ChassisSpeeds speeds = new ChassisSpeeds(xS, yS, tS);

        if (teleopFieldOriented)
            speeds = ChassisSpeeds.fromFieldRelativeSpeeds(speeds, swerveDrive.getYaw());

        swerveDrive.drive(speeds, teleopClosedLoop, new Translation2d());
    }

    /**
     * Drives the Robot using two Joysticks: one for XY and one for Twist.
     * @param xyStick The {@link DriveHIDBase} to use for directional control.
     * @param twistStick The {@link DriveHIDBase} to use for twisting.
     */
    public void teleopDrive(DriveHIDBase xyStick, DriveHIDBase twistStick) {
        // Calculate the maximum speed based on XY and Twist.
        double xS = xyStick.getRobotX() * MAX_SPEED_MPS;
        double yS = xyStick.getRobotY() * MAX_SPEED_MPS;
        double tS = twistStick.getRobotTwist() * swerveDrive.swerveController.config.maxAngularVelocity;

        ChassisSpeeds speeds = new ChassisSpeeds(xS, yS, tS);

        if (teleopFieldOriented)
            speeds = ChassisSpeeds.fromFieldRelativeSpeeds(speeds, swerveDrive.getYaw());

        swerveDrive.drive(speeds, teleopClosedLoop, new Translation2d());
    }

    /** @return The {@link SwerveDriveKinematics} of the {@link SwerveDrive}. */
    public SwerveDriveKinematics getKinematics() { return swerveDrive.kinematics; }

    /**
     * Resets the {@link SwerveDrive} to the given {@link Pose2d}. Gyroscope angle and module positions do
     * not need to be reset when calling this method.  However, this method <b>MUST</b> be called upon a regular
     * reset.
     *
     * @param initialPose The {@link Pose2d} to reset the Odometry to.
     */
    public void resetOdometry(Pose2d initialPose) { swerveDrive.resetOdometry(initialPose); }

    /**
     * Resets the {@link SwerveDrive} to a blank {@link Pose2d}. Gyroscope angle and module positions do
     * not need to be reset when calling this method.  However, this method <b>MUST</b> be called upon a regular
     * reset.
     */
    public void resetOdometry() { resetOdometry(new Pose2d()); }

    /** @return The current Robot {@link Pose2d} as reported by the Odometry. */
    public Pose2d getPose() { return swerveDrive.getPose(); }

    /**
     * Set the raw {@link ChassisSpeeds} with Closed-Loop Velocity Control.
     * <br></br>
     * NOTE: This method should <b>ONLY</b> be used for autonomous control. In regular driving, call the
     * {@link #teleopDrive(DriveHIDBase, DriveHIDBase)} method or equivalent.
     *
     * @param chassisSpeeds Chassis Speeds to set.
     */
    public void setChassisSpeeds(ChassisSpeeds chassisSpeeds) { swerveDrive.setChassisSpeeds(chassisSpeeds); }

    /*
    public void postTrajectory(Trajectory trajectory) { swerveDrive.postTrajectory(trajectory); }
    */

    /** @return A {@link Command} used to reset the {@link SwerveDrive} gyroscope to a heading of zero degrees. */
    public Command resetGyroCommand() { return Commands.runOnce(() -> swerveDrive.zeroGyro()); }

    /** @return The {@link Rotation2d} yaw/heading of the robot (CCW positive) */
    public Rotation2d getHeading() { return swerveDrive.getYaw(); }

    /** @return The currently used <b>field-oriented</b> {@link ChassisSpeeds} of the {@link SwerveDrive} system. */
    public ChassisSpeeds getFieldVelocity() { return swerveDrive.getFieldVelocity(); }

    /** @return The currently used <b>robot-oriented</b> {@link ChassisSpeeds} of the {@link SwerveDrive} system. */
    public ChassisSpeeds getRobotVelocity() { return swerveDrive.getRobotVelocity(); }

    /** @return The current {@link SwerveController} of the {@link SwerveDrive} system .*/
    public SwerveController getSwerveController() { return swerveDrive.swerveController; }

    /** @return The current {@link SwerveDriveConfiguration} of the {@link SwerveDrive} system. */
    public SwerveDriveConfiguration getSwerveDriveConfiguration() { return swerveDrive.swerveDriveConfiguration; }

    /** @return A {@link Command} used to angle all {@link SwerveDrive} wheels 90 degrees -- locking the wheels. */
    public Command lockWheelCommand() { return this.run(() -> swerveDrive.lockPose()); }

    /** @return The current {@link Rotation2d} pitch of the Robot. */
    public Rotation2d getPitch() { return swerveDrive.getPitch(); }

    /**
     * Factory to fetch the PathPlanner command to follow the defined path.
     *
     * @param path             Path planner path to specify.
     * @param constraints      {@link PathConstraints} for {@link com.pathplanner.lib.PathPlanner#loadPathGroup}
     *                         function limiting velocity and acceleration.
     * @param eventMap         {@link java.util.HashMap} of commands corresponding to path planner events given as
     *                         strings.
     * @param translation      The {@link PIDConstants} for the translation of the robot while following the path.
     * @param rotation         The {@link PIDConstants} for the rotation of the robot while following the path.
     * @param useAllianceColor Automatically transform the path based on alliance color.
     * @return PathPlanner command to follow the given path.
     */
    public Command createPathPlannerCommand(String path, PathConstraints constraints, Map<String, Command> eventMap,
                                           PIDConstants translation, PIDConstants rotation, boolean useAllianceColor) {
        List<PathPlannerTrajectory> pathGroup = PathPlanner.loadPathGroup(path, constraints);
        if (autoBuilder == null) {
            autoBuilder = new SwerveAutoBuilder(
                    swerveDrive::getPose,
                    swerveDrive::resetOdometry,
                    translation,
                    rotation,
                    swerveDrive::setChassisSpeeds,
                    eventMap,
                    useAllianceColor,
                    this
            );
        }

        return autoBuilder.fullAuto(pathGroup);
    }
}