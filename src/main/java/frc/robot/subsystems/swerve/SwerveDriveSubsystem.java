package frc.robot.subsystems.swerve;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.util.joystick.DriveMode;
import frc.robot.util.joystick.IDriveHID;
import frc.robot.util.joystick.IDriveMode;
import frc.robot.util.math.Distance;
import frc.robot.util.swerve.SwerveAHRS;
import frc.robot.util.swerve.SwerveChassis;
import frc.robot.util.swerve.SwerveModule;
import frc.robot.util.swerve.SwerveOdometry;

import java.util.HashMap;

import static frc.robot.Constants.Global.TEST_MODE;

/**
 * This {@link SwerveDriveSubsystem} is designed to be used for controlling the {@link SwerveChassis}, and utilizing
 * an {@link AHRS} gyroscope to provide the field-relating driving a robot needs. This is also useful for debugging
 * purposes (or for simple autonomous) as it allows driving in a specific direction.
 */
public class SwerveDriveSubsystem extends SubsystemBase {
    private final SwerveAHRS gyro;
    private final SwerveChassis swerveChassis;
    private final SwerveOdometry odometry;
    private Rotation2d robotHeading;

    private IDriveMode driveMode;

    private boolean fieldOriented = false;
    private boolean closedLoop = false;

    public void setFieldOriented(boolean fieldOriented) { this.fieldOriented = fieldOriented; }
    public void setClosedLoop(boolean closedLoop) { this.closedLoop = closedLoop; }
    public void setDriveMode(IDriveMode mode) { this.driveMode = mode; }

    public boolean isFieldOriented() { return this.fieldOriented; }
    public boolean isClosedLoop() { return this.closedLoop; }
    public IDriveMode getDriveMode() { return this.driveMode; }

    public Pose2d getPose() {
        return odometry.getPose();
    }

    public Command lockWheelCommand() {
        return Commands.run(() -> {
            swerveChassis.setStates(
                    new SwerveModuleState[]{
                            new SwerveModuleState(0, new Rotation2d(Math.PI/2)),
                            new SwerveModuleState(0, new Rotation2d(Math.PI/2)),
                            new SwerveModuleState(0, new Rotation2d(Math.PI/2)),
                            new SwerveModuleState(0, new Rotation2d(Math.PI/2))
                    }
            );
        });
    }

    public Command toggleFieldOrientedCommand() {
        return runOnce(() -> fieldOriented = !fieldOriented).andThen(resetGyroCommand());
    }

    public Command toggleClosedLoopCommand() {
        return runOnce(() -> closedLoop = !closedLoop);
    }

    public Command resetGyroCommand() {
        return this.runOnce(this::resetPosition);
    }

    /** Initializes a new {@link SwerveDriveSubsystem}, and resets the Gyroscope. */
    public SwerveDriveSubsystem(SwerveModule fl, SwerveModule fr, SwerveModule bl, SwerveModule br, Distance sideLength) {
        swerveChassis = new SwerveChassis(fl, fr, bl, br, sideLength);
        gyro = new SwerveAHRS(SPI.Port.kMXP);
        robotHeading = new Rotation2d(0);

        odometry = new SwerveOdometry(
                swerveChassis,
                this::getRobotHeading,
                swerveChassis::getSwerveModulePositions,
                new Pose2d()
        );

        gyro.reset();
        gyro.calibrate();

        driveMode = DriveMode.SMOOTH_MAP;

        resetPosition();
    }

    public SwerveAHRS getGyro() { return this.gyro; }

    /**
     * @return A {@link Rotation2d} containing the current rotation of the robot
     */
    public Rotation2d getRobotHeading() {
        return robotHeading;
    }

    @Override
    public void periodic() {
        // Update the robot speed and other information.
        robotHeading = new Rotation2d(gyro.getRotation2d().getRadians());

        if (odometry.shouldUpdate())
            odometry.update();

        SmartDashboard.putNumber("Robot Angle", robotHeading.getDegrees()%360);
        SmartDashboard.putNumber("Gyro Pitch", gyro.getRoll());

        if (TEST_MODE) {
            SmartDashboard.putString("Robot Position", odometry.getPose().toString());
            SmartDashboard.putBoolean("Gyro Calibrating", gyro.isCalibrating());
        }
    }

    /** @return A {@link HashMap} containing {@link SwerveModuleState} of the robot. */
    public HashMap<String, SwerveModuleState> getSwerveModuleStates() {
        return swerveChassis.getSwerveModuleStates();
    }

    /**
     * Manually drives the robot in a specific direction, using raw {@link ChassisSpeeds}. This is not recommended
     * because it can override the desired driving mode (robot-relative/field-relative) which autoDrive will
     * compensate for. Held <b>indefinitely</b> until this method is recalled again.
     *
     * @param speeds The {@link ChassisSpeeds} to drive the robot with.
     * @see ChassisSpeeds#fromFieldRelativeSpeeds(double, double, double, Rotation2d)
     */
    public void drive(ChassisSpeeds speeds) {
        swerveChassis.drive(speeds);
    }

    /**
     * Drives the Robot using specific speeds, which is converted to field-relative or robot-relative
     * automatically. Unlike {@link #drive(ChassisSpeeds)}, it will compensate for the angle of the Robot.
     *
     * @param vX X-direction m/s (+ forward, - reverse)
     * @param vY Y-direction m/s (+ left, - right)
     * @param omega Yaw rad/s (+ left, - right)
     */
    public void drive(double vX, double vY, double omega) {
        this.drive(ChassisSpeeds.fromFieldRelativeSpeeds(vX, vY, omega, fieldOriented ? odometry.getPose().getRotation() : new Rotation2d(0)));
    }

    public void drive(IDriveHID leftStick, IDriveHID rightStick) {
        this.drive(
                driveMode.getX(leftStick.getRobotX()),
                driveMode.getY(leftStick.getRobotY()),
                driveMode.getTwist(rightStick.getRobotTwist())
        );
    }

    public void drive(IDriveHID stick) {
        this.drive(stick.getRobotX(), stick.getRobotY(), stick.getRobotTwist());
    }

    public void driveForward() { drive(ChassisSpeeds.fromFieldRelativeSpeeds(0.8, 0, 0, Rotation2d.fromDegrees(0))); }
    public void driveBack() { drive(ChassisSpeeds.fromFieldRelativeSpeeds(-0.5, 0, 0, Rotation2d.fromDegrees(0))); }
    public void driveLeft() { drive(ChassisSpeeds.fromFieldRelativeSpeeds(0, 0.8, 0, Rotation2d.fromDegrees(0))); }
    public void driveRight() { drive(ChassisSpeeds.fromFieldRelativeSpeeds(0, -0.8, 0, Rotation2d.fromDegrees(0))); }
    public void stop() { drive(ChassisSpeeds.fromFieldRelativeSpeeds(0, 0, 0, Rotation2d.fromDegrees(0))); }

    public static double deadzone(double value, double deadzone) {
        return Math.abs(value) > deadzone ? value : 0;
    }

    /**
     * Resets the Odometry, which will offset the gyro angle and cause it to become zero while
     * being referenced, essentially resetting the gyroscope.
     */
    public void resetPosition() {
        odometry.resetOdometry();
    }

    public void resetPositionPose(Pose2d pose) {
        odometry.resetOdometry(pose);
    }

    /** @return The currently used {@link SwerveChassis} */
    public SwerveChassis getSwerveChassis(){
        return swerveChassis;
    }
}