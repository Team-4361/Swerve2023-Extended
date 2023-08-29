package frc.robot.swerve.chassis;


import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.commands.PPSwerveControllerCommand;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.swerve.joystick.DriveJoystick;
import frc.robot.swerve.joystick.DriveXboxController;
import frc.robot.util.math.AngularVelocity;
import frc.robot.util.math.ExtendedMath;
import frc.robot.util.math.SwerveVelocities;
import frc.robot.util.math.Velocity;
import frc.robot.swerve.module.SwerveModule;

public class SwerveDriveSubsystem extends SubsystemBase {
    private final SwerveDriveKinematics kinematics;
    private final SwerveDriveOdometry odometry;

    private final SwerveChassisConfiguration config;

    private Pose2d robotPose;

    // These offset values are required to keep the gyroscope value at zero upon boot, even
    // when the playing field is not perfectly level.
    private double pitchOffset = 0.0;
    private double rollOffset = 0.0;

    private boolean fieldOriented = true;
    private boolean closedLoop = true;

    public SwerveDriveSubsystem setFieldOriented(boolean fieldOriented) {
        this.fieldOriented = fieldOriented;
        return this;
    }

    public SwerveDriveSubsystem setClosedLoop(boolean closedLoop) {
        this.closedLoop = closedLoop;
        return this;
    }

    public boolean isFieldOriented() { return this.fieldOriented; }
    public boolean isClosedLoop() { return this.closedLoop; }

    public Command toggleFieldOrientedCommand() { return Commands.runOnce(() -> fieldOriented = !fieldOriented); }
    public Command toggleClosedLoopCommand() { return Commands.runOnce(() -> closedLoop = !closedLoop); }
    public Command resetGyroCommand() { return Commands.runOnce(this::resetGyro); }
    public Command resetPositionCommand() { return Commands.runOnce(this::resetPosition); }

    public SwerveDriveSubsystem(SwerveChassisConfiguration config) {
        double sideLengthMeters = config.getSideLength().toMeters();

        // +X = Forward | +Y = Left
        this.kinematics = new SwerveDriveKinematics(
                new Translation2d(sideLengthMeters / 2, sideLengthMeters / 2), // FL
                new Translation2d(sideLengthMeters / 2, -sideLengthMeters / 2), // FR
                new Translation2d(-sideLengthMeters / 2, sideLengthMeters / 2), // BL
                new Translation2d(-sideLengthMeters / 2, -sideLengthMeters / 2) // BR
        );

        this.config = config;

        // Perform the initial calibration on the gyroscope for the best result.
        config.getGyro().calibrate();

        long endTime = System.currentTimeMillis() + (30)*1000; // 30 sec
        while (System.currentTimeMillis() <= endTime && config.getGyro().isCalibrating())
            Thread.onSpinWait();

        this.odometry = new SwerveDriveOdometry(
                kinematics,
                getHeading(),
                getModulePositions(),
                new Pose2d()
        );

        this.robotPose = new Pose2d();
    }

    @Override
    public void periodic() {
        config.getFrontLeft().update();
        config.getFrontRight().update();
        config.getBackLeft().update();
        config.getBackRight().update();

        robotPose = odometry.update(getHeading(), getModulePositions());

        SmartDashboard.putString("Robot Pose", robotPose.toString());
        SmartDashboard.putNumber("Robot Heading", getHeading().getDegrees());
    }

    public Rotation2d getHeading() {
        double degrees = config
                .getGyro()
                .getRotation2d()
                .getDegrees();

        if (config.isGyroInverted())
            return Rotation2d.fromDegrees(degrees % 360).unaryMinus();
        else
            return Rotation2d.fromDegrees(degrees % 360);
    }

    public Velocity getMaxVelocity() {
        if (config == null)
            return Velocity.fromMPS(0); // may be called too soon

        return Velocity.fromMPS(
                ExtendedMath.min(
                    config.getFrontLeft().getMaxVelocity(),
                    config.getFrontRight().getMaxVelocity(),
                    config.getBackLeft().getMaxVelocity(),
                    config.getBackRight().getMaxVelocity()
                )
        );
    }

    public AngularVelocity getMaxTurnVelocity() {
        return AngularVelocity.fromRadPerSec(
                ExtendedMath.min(
                    config.getFrontLeft().getMaxTurnVelocity(),
                    config.getFrontRight().getMaxTurnVelocity(),
                    config.getBackLeft().getMaxTurnVelocity(),
                    config.getBackRight().getMaxTurnVelocity()
                )
        );
    }

    public SwerveModule getFrontLeft() { return config.getFrontLeft(); }
    public SwerveModule getFrontRight() { return config.getFrontRight(); }
    public SwerveModule getBackLeft() { return config.getBackLeft(); }
    public SwerveModule getBackRight() { return config.getBackRight(); }
    public SwerveDriveKinematics getKinematics() { return kinematics; }

    public void setStates(SwerveModuleState[] states) { setStates(states, true); }
    public Rotation2d getPitch() { return Rotation2d.fromDegrees(config.getGyro().getPitch() - pitchOffset); }
    public Rotation2d getRoll() { return Rotation2d.fromDegrees(config.getGyro().getRoll() - rollOffset); }
    public Pose2d getRobotPose() { return this.robotPose; }

    public SwerveModulePosition[] getModulePositions() {
        return new SwerveModulePosition[] {
                getFrontLeft().getPosition(),
                getFrontRight().getPosition(),
                getBackLeft().getPosition(),
                getBackRight().getPosition()
        };
    }

    public void setStates(SwerveModuleState[] states, boolean isClosedLoop) {
        SwerveDriveKinematics.desaturateWheelSpeeds(states, getMaxVelocity().toMPS());

        config.getFrontLeft().setState(states[0], isClosedLoop);
        config.getFrontRight().setState(states[1], isClosedLoop);
        config.getBackLeft().setState(states[2], isClosedLoop);
        config.getBackRight().setState(states[3], isClosedLoop);
    }
    public void drive(SwerveVelocities velocities){
        drive(velocities, fieldOriented, closedLoop);
    }
    public void drive(SwerveVelocities velocities, boolean fieldRelative, boolean isClosedLoop) {
        double xVel = velocities.getForwardVelocity().toMPS();
        double yVel = velocities.getSideVelocity().toMPS();
        double omegaVel = velocities.getTwistVelocity().toRadPerSec();

        ChassisSpeeds velocity = fieldRelative ?
                ChassisSpeeds.fromFieldRelativeSpeeds(xVel, yVel, omegaVel, getHeading())
                : new ChassisSpeeds(xVel, yVel, omegaVel);

        SmartDashboard.putNumber("Desired X-Vel", xVel);
        SmartDashboard.putNumber("Desired Y-Vel", yVel);
        SmartDashboard.putNumber("Desired Twist Vel", omegaVel);

        setStates(kinematics.toSwerveModuleStates(velocity), isClosedLoop);
    }

    public void drive(DriveXboxController xbox){
        drive(xbox, fieldOriented, closedLoop);
    }
    public void drive(DriveXboxController xbox, boolean fieldRelative, boolean isClosedLoop) {
        drive(SwerveVelocities.fromVelocities(
                xbox.getForwardVelocity(getMaxVelocity()),
                xbox.getSideVelocity(getMaxVelocity()),
                xbox.getTwistVelocity(getMaxTurnVelocity())
        ), fieldRelative, isClosedLoop);
    }
    public void drive(DriveJoystick xystick, DriveJoystick zstick){
        drive(xystick, zstick, fieldOriented, closedLoop);
    }
    public void drive(DriveJoystick xyStick, DriveJoystick zStick, boolean fieldRelative, boolean isClosedLoop) {
        drive(SwerveVelocities.fromVelocities(
                xyStick.getForwardVelocity(getMaxVelocity()),
                xyStick.getSideVelocity(getMaxVelocity()),
                zStick.getTwistVelocity(getMaxTurnVelocity())
        ), fieldRelative, isClosedLoop);
    }

    public void drive(ChassisSpeeds speeds, boolean isClosedLoop) {
        setStates(kinematics.toSwerveModuleStates(speeds), isClosedLoop);
    }

    /**
     * Resets the Gyroscope and Position values.
     * @see #resetGyro()
     * @see #resetPosition()
     */
    public void reset() {
        resetGyro();
        resetPosition();
    }

    /**  Resets the Gyroscope, and adjusts the Pitch, Roll, and Yaw offsets. */
    public void resetGyro() {
        config.getGyro().reset();

        pitchOffset = config.getGyro().getPitch();
        rollOffset = config.getGyro().getRoll();
    }

    /** Resets the Distance Position for all Swerve Modules. */
    public void resetPosition() {
        config.getFrontLeft().reset();
        config.getFrontRight().reset();
        config.getBackLeft().reset();
        config.getBackRight().reset();

        odometry.resetPosition(getHeading(), getModulePositions(), new Pose2d());
    }

    public Command followTrajectoryCommand(PathPlannerTrajectory trajectory) {
        return new PPSwerveControllerCommand(
                trajectory,
                this::getRobotPose,
                kinematics,
                new PIDController(0, 0, 0),
                new PIDController(0, 0, 0),
                new PIDController(0, 0, 0),
                this::setStates,
                true,
                this
        );
    }
}