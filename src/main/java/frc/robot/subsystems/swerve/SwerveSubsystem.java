// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.swerve;

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
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Robot;
import frc.robot.swerve.SwerveController;
import frc.robot.swerve.SwerveDrive;
import frc.robot.swerve.parser.SwerveControllerConfiguration;
import frc.robot.swerve.parser.SwerveDriveConfiguration;
import frc.robot.util.joystick.DriveMode;
import frc.robot.util.joystick.IDriveHID;
import frc.robot.util.joystick.IDriveMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwerveSubsystem extends SubsystemBase {
    private final SwerveDrive swerveDrive;

    private SwerveAutoBuilder autoBuilder = null;
    private IDriveMode driveMode;

    private Field2d field;

    /**
     * Construct the swerve drive.
     *
     * @param driveCfg      SwerveDriveConfiguration for the swerve.
     * @param controllerCfg Swerve Controller.
     */
    public SwerveSubsystem(SwerveDriveConfiguration driveCfg, SwerveControllerConfiguration controllerCfg) {
        swerveDrive = new SwerveDrive(driveCfg, controllerCfg);
        driveMode = DriveMode.SMOOTH_MAP;
        field = new Field2d();
    }

    /**
     * This method is called periodically by the {@link CommandScheduler}. Useful for updating
     * subsystem-specific state that you don't want to offload to a {@link Command}. Teams should try
     * to be consistent within their own codebases about which responsibilities will be handled by
     * Commands, and which will be handled here.
     */
    @Override
    public void periodic() {
        field.setRobotPose(getPose());
        SmartDashboard.putData("Field", field);
    }

    public void setDriveMode(IDriveMode mode) {
        this.driveMode = mode;
    }

    public IDriveMode getDriveMode() {
        return this.driveMode;
    }

    /**
     * The primary method for controlling the drivebase.  Takes a {@link Translation2d} and a rotation rate, and
     * calculates and commands module states accordingly.  Can use either open-loop or closed-loop velocity control for
     * the wheel velocities.  Also has field- and robot-relative modes, which affect how the translation vector is used.
     *
     * @param translation   {@link Translation2d} that is the commanded linear velocity of the robot, in meters per
     *                      second. In robot-relative mode, positive x is torwards the bow (front) and positive y is
     *                      torwards port (left).  In field-relative mode, positive x is away from the alliance wall
     *                      (field North) and positive y is torwards the left wall when looking through the driver station
     *                      glass (field West).
     * @param rotation      Robot angular rate, in radians per second. CCW positive.  Unaffected by field/robot
     *                      relativity.
     * @param fieldRelative Drive mode.  True for field-relative, false for robot-relative.
     * @param isOpenLoop    Whether to use closed-loop velocity control.  Set to true to disable closed-loop.
     */
    public void drive(Translation2d translation, double rotation, boolean fieldRelative, boolean isOpenLoop) {
        swerveDrive.drive(translation, rotation, fieldRelative, isOpenLoop);
    }

    public void drive(Pose2d pose, boolean fieldRelative, boolean isOpenLoop) {
        swerveDrive.drive(pose.getTranslation(), pose.getRotation().getRadians(), fieldRelative, isOpenLoop);
    }

    /**
     * Get the swerve drive kinematics object.
     *
     * @return {@link SwerveDriveKinematics} of the swerve drive.
     */
    public SwerveDriveKinematics getKinematics() {
        return swerveDrive.kinematics;
    }

    /**
     * Resets odometry to the given pose. Gyro angle and module positions do not need to be reset when calling this
     * method.  However, if either gyro angle or module position is reset, this must be called in order for odometry to
     * keep working.
     *
     * @param pose The pose to set the odometry to
     */
    public void resetOdometry(Pose2d pose) {
        swerveDrive.resetOdometry(pose);
    }

    /**
     * Gets the current pose (position and rotation) of the robot, as reported by odometry.
     *
     * @return The robot's pose
     */
    public Pose2d getPose() {
        return swerveDrive.getPose();
    }

    /**
     * Set chassis speeds with closed-loop velocity control.
     *
     * @param chassisSpeeds Chassis Speeds to set.
     */
    public void setChassisSpeeds(ChassisSpeeds chassisSpeeds) {
        swerveDrive.setChassisSpeeds(chassisSpeeds);
    }

    /**
     * Post the trajectory to the field.
     *
     * @param trajectory The trajectory to post.
     */
    public void postTrajectory(Trajectory trajectory) {
        swerveDrive.postTrajectory(trajectory);
    }

    /**
     * Resets the gyro angle to zero and resets odometry to the same position, but facing toward 0.
     */
    public void zeroGyro() {
        swerveDrive.zeroGyro();
    }

    /**
     * Sets the drive motors to brake/coast mode.
     *
     * @param brake True to set motors to brake mode, false for coast.
     */
    public void setMotorBrake(boolean brake) { swerveDrive.setMotorIdleMode(brake); }

    /**
     * Gets the current yaw angle of the robot, as reported by the imu.  CCW positive, not wrapped.
     *
     * @return The yaw angle
     */
    public Rotation2d getHeading() { return swerveDrive.getYaw(); }

    /**
     * Get the chassis speeds based on controller input of 2 joysticks. One for speeds in which direction. The other for
     * the angle of the robot.
     *
     * @param xInput   X joystick input for the robot to move in the X direction.
     * @param yInput   Y joystick input for the robot to move in the Y direction.
     * @param headingX X joystick which controls the angle of the robot.
     * @param headingY Y joystick which controls the angle of the robot.
     * @return {@link ChassisSpeeds} which can be sent to th Swerve Drive.
     */
    public ChassisSpeeds getTargetSpeeds(double xInput, double yInput, double headingX, double headingY) {
        xInput = Math.pow(xInput, 3);
        yInput = Math.pow(yInput, 3);
        return swerveDrive.swerveController.getTargetSpeeds(xInput, yInput, headingX, headingY, getHeading().getRadians());
    }

    public Pose2d getTargetPose(IDriveHID xyStick, IDriveHID twistStick) {
        double xVel = xyStick.getRobotX();
        double yVel = xyStick.getRobotY();
        double twistVel = twistStick.getRobotTwist();

        if (driveMode != null) {
            xVel = driveMode.getX(xVel);
            yVel = driveMode.getY(yVel);
            twistVel = driveMode.getTwist(twistVel);
        }

        return new Pose2d(
                new Translation2d(
                        xVel * swerveDrive.swerveDriveConfiguration.maxSpeed,
                        yVel * swerveDrive.swerveDriveConfiguration.maxSpeed
                ),
                Rotation2d.fromRadians(
                        twistVel * swerveDrive.swerveDriveConfiguration.attainableMaxRotationalVelocityRadiansPerSecond
                )
        );
    }

    public Pose2d getTargetPose(IDriveHID controller) {
        double xVel = controller.getRobotX();
        double yVel = controller.getRobotY();
        double twistVel = controller.getRobotTwist();

        if (driveMode != null) {
            xVel = driveMode.getX(xVel);
            yVel = driveMode.getY(yVel);
            twistVel = driveMode.getTwist(twistVel);
        }

        return new Pose2d(
                new Translation2d(
                        xVel * swerveDrive.swerveDriveConfiguration.maxSpeed,
                        yVel * swerveDrive.swerveDriveConfiguration.maxSpeed
                ),
                Rotation2d.fromRadians(
                        twistVel * swerveDrive.swerveDriveConfiguration.attainableMaxRotationalVelocityRadiansPerSecond
                )
        );
    }

    public void stop() {
        swerveDrive.setModuleStates(new SwerveModuleState[]{
                new SwerveModuleState(0, Rotation2d.fromDegrees(0)),
                new SwerveModuleState(0, Rotation2d.fromDegrees(0)),
                new SwerveModuleState(0, Rotation2d.fromDegrees(0)),
                new SwerveModuleState(0, Rotation2d.fromDegrees(0)),
        }, false);
    }

    /**
     * Gets the current field-relative velocity (x, y and omega) of the robot
     *
     * @return A ChassisSpeeds object of the current field-relative velocity
     */
    public ChassisSpeeds getFieldVelocity() {
        return swerveDrive.getFieldVelocity();
    }

    /**
     * Gets the current velocity (x, y and omega) of the robot
     *
     * @return A {@link ChassisSpeeds} object of the current velocity
     */
    public ChassisSpeeds getRobotVelocity() {
        return swerveDrive.getRobotVelocity();
    }

    /**
     * Get the {@link SwerveController} in the swerve drive.
     *
     * @return {@link SwerveController} from the {@link SwerveDrive}.
     */
    public SwerveController getSwerveController() {
        return swerveDrive.swerveController;
    }

    /**
     * Get the {@link SwerveDriveConfiguration} object.
     *
     * @return The {@link SwerveDriveConfiguration} fpr the current drive.
     */
    public SwerveDriveConfiguration getSwerveDriveConfiguration() {
        return swerveDrive.swerveDriveConfiguration;
    }

    /**
     * Lock the swerve drive to prevent it from moving.
     */
    public void lock() {
        swerveDrive.lockPose();
    }

    /**
     * Gets the current pitch angle of the robot, as reported by the imu.
     *
     * @return The heading as a {@link Rotation2d} angle
     */
    public Rotation2d getPitch() {
        return swerveDrive.getPitch();
    }

    /**
     * Factory to fetch the PathPlanner command to follow the defined path.
     *
     * @param path             Path planner path to specify.
     * @param constraints      {@link PathConstraints} for {@link PathPlanner#loadPathGroup} function
     *                         limiting velocity and acceleration.
     * @param eventMap         {@link HashMap} of commands corresponding to path planner events given as
     *                         strings.
     * @param translation      The {@link PIDConstants} for the translation of the robot while following the path.
     * @param rotation         The {@link PIDConstants} for the rotation of the robot while following the path.
     * @param useAllianceColor Automatically transform the path based on alliance color.
     * @return PathPlanner command to follow the given path.
     */
    public Command createAutoCommand(String path, PathConstraints constraints, Map<String, Command> eventMap,
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

    public Command createLockCommand() {
        return Robot.swerveDrive.runOnce(swerveDrive::lockPose);
    }

    public Command createZeroGyroCommand() {
        return Commands.runOnce(swerveDrive::zeroGyro);
    }

    public Command createResetEncoderCommand() {
        return Commands.runOnce(swerveDrive::resetEncoders);
    }
}
