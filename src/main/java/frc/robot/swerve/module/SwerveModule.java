package frc.robot.swerve.module;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.util.math.AngularVelocity;
import frc.robot.util.math.Distance;
import frc.robot.util.math.Velocity;

/**
 * A {@link SwerveModule} is composed of two motors and two encoders:
 * a drive motor/encoder and a turn motor/encoder. The turn motor is
 * responsible for controlling the direction the drive motor faces, essentially
 * allowing the robot to move in any direction.
 */
public class SwerveModule {
    private final SwerveModuleConfiguration config;
    private SparkMaxPIDController driveController;
    private SparkMaxPIDController turnController;

    /**
     * Creates a new {@link SwerveModule} instance, using the specified parameters.
     */
    public SwerveModule(SwerveModuleConfiguration conf) {
        config = conf;
        driveController = conf.getDriveMotor().getPIDController();
        turnController = conf.getTurnMotor().getPIDController();
    }

    public Velocity getVelocity() {
        return config.getDriveRatio().getWheelVelocity(
                config.getDriveEncoder().getVelocity(),
                config.getWheelDiameter()
        );
    }

    public Velocity getMaxVelocity() { return config.getMaxVelocity(); }
    public AngularVelocity getMaxTurnVelocity() { return config.getMaxTurnVelocity(); }
    public SwerveModuleConfiguration getConfig() { return config; }

    private Rotation2d getTurnAngleNoOffset() {
        /*
        return config
                .getTurnRatio()
                .motorRotationsToAngle(config.getTurnEncoder().get(), true);

         */
        return Rotation2d.fromRotations(config.getTurnEncoder().get());
    }

    public Rotation2d getTurnAngle() {
        return getTurnAngleNoOffset().plus(config.getOffset());
    }

    public void setState(SwerveModuleState state, boolean isClosedLoop) {
        state = SwerveModuleState.optimize(state, getTurnAngle());

        double turnPower = MathUtil.clamp(config.getTurnController().calculate(
                getTurnAngle().getDegrees(),
                state.angle.getDegrees()
        ), -1, 1);

        double drivePower;
        if (isClosedLoop) {

            // calculate the required RPM for MPS
            //drivePower = MathUtil.clamp(config.getDriveController().calculate(
            //        getVelocity().toMPS(),
            //        state.speedMetersPerSecond
            //), -1, 1);
            driveController.setReference(Velocity.fromMPS(state.speedMetersPerSecond).to
        } else {
            drivePower = Velocity.fromMPS(state.speedMetersPerSecond)
                    .toMotorPower(config.getMaxVelocity());
        }

        config.getDriveMotor().set(drivePower);
        config.getTurnMotor().set(turnPower);
    }

    /**
     * Get the {@link SwerveModuleState} based on the drive motor's velocity
     * (meters/sec) and the turn encoder's angle.
     *
     * @return a new {@link SwerveModuleState}, representing the module's
     * current state, based on the module's drive motor velocity (m/s)
     * and the turn encoder's angle.
     */
    public SwerveModuleState getState() {
        return new SwerveModuleState(
                getVelocity().toMPS(),
                getTurnAngle()
        );
    }

    /**
     * Get the {@link SwerveModulePosition} based on the drive motor's
     * distance travelled (in meters), and turn encoder's angle. This
     * is required for to work correctly.
     *
     * @return A {@link SwerveModulePosition}, representing the module's
     * current position, based on the module's drive motor distance and
     * the turn encoder's angle.
     */
    public SwerveModulePosition getPosition() {
        return new SwerveModulePosition(
                getDistance().toMeters(),
                getTurnAngle()
        );
    }

    public void update() {
        String prefix = config.getModuleSide().toString();
        String driveVelocity = prefix + ": mph";
        String drivePower = prefix + ": pow";
        String turnPower = prefix + ": turn pow";
        String turnPosition = prefix + ": turn deg";
        String distanceDriven = prefix + ": distance";

        SmartDashboard.putNumber(driveVelocity, getVelocity().toMPH());
        SmartDashboard.putNumber(drivePower, config.getDriveMotor().get());
        SmartDashboard.putNumber(turnPower, config.getTurnMotor().get());
        SmartDashboard.putNumber(turnPosition, getTurnAngle().getDegrees());

        SmartDashboard.putNumber(distanceDriven, getDistance().toFeet());
    }

    public Distance getDistance() {
        return config.getDriveRatio().getWheelDistance(
                config.getDriveEncoder().getPosition(),
                config.getWheelDiameter()
        );
    }

    public void reset() {
        config.getDriveEncoder().setPosition(0);
    }
}