package frc.robot.util.swerve;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.ControlType;
import com.revrobotics.RelativeEncoder;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.motorcontrol.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import frc.robot.util.math.Distance;
import frc.robot.util.math.GearRatio;
import frc.robot.util.math.Velocity;

import java.util.function.Supplier;
import static frc.robot.Constants.Global.TEST_MODE;

/**
 * A {@link SwerveModule} is composed of two motors and two encoders:
 * a drive motor/encoder and a turn motor/encoder. The turn motor is
 * responsible for controlling the direction the drive motor faces, essentially
 * allowing the robot to move in any direction.
 */
public class SwerveModule {
    private final CANSparkMax driveMotor;
    private final CANSparkMax turnMotor;
    private final RelativeEncoder driveEncoder;
    private final DutyCycleEncoder rotationPWMEncoder;
    private final SwerveModuleConfig config;
    private final Rotation2d offset;
    private final String name;

    private final double errorFactor;
    private final PIDController turnController = new PIDController(
            0.5,
            0.0,
            0.0,
            0.02
    );

    private final PIDController driveController = new PIDController(
            0.5,
            0.0,
            0.0,
            0.02
    );

    /**
     * Creates a new {@link SwerveModule} instance, using the specified parameters.
     *
     * @param driveMotorId       The Motor ID used for driving the wheel.
     * @param turnMotorId        The Motor ID used for turning the wheel.
     * @param digitalEncoderPort The {@link DigitalInput} ID used for the Encoder.
     * @param offset             The offset to use for driving the wheel.
     * @param errorFactor        The maximum error factor that is acceptable.
     */
    public SwerveModule(String name,
                        int driveMotorId,
                        int turnMotorId,
                        int digitalEncoderPort,
                        SwerveModuleConfig config,
                        Rotation2d offset,
                        double errorFactor) {
        this.driveMotor = new CANSparkMax(driveMotorId, MotorType.kBrushless);
        this.turnMotor = new CANSparkMax(turnMotorId, MotorType.kBrushless);
        this.rotationPWMEncoder = new DutyCycleEncoder(digitalEncoderPort);
        this.driveEncoder = driveMotor.getEncoder();

        this.offset = offset;
        this.config = config;
        this.errorFactor = errorFactor;
        this.name = name;
    }

    /**
     * @return The current {@link Velocity} of the {@link SwerveModule}
     */
    private Velocity getVelocity() {
        // rpm -> rps -> mps
        return config.getDriveRatio().getWheelVelocity(driveEncoder.getVelocity(), config.getWheelDiameter());
    }

    public Rotation2d getTurnAngle() {
        return config.getTurnRatio().motorRotationsToAngle(
                rotationPWMEncoder.get() * 2 * Math.PI
        ).plus(offset);
    }

    public void setState(SwerveModuleState state, boolean isClosedLoop) {
        state = SwerveModuleState.optimize(state, getTurnAngle());

        double turnPower = turnController.calculate(
                getTurnAngle().getRadians(),
                state.angle.getRadians()
        );

        if (isClosedLoop) {
            driveMotor.getPIDController().setReference(
                    config.getDriveRatio().getMotorRPM(
                            Velocity.fromMPS(state.speedMetersPerSecond),
                            config.getWheelDiameter()
                    ),
                    CANSparkMax.ControlType.kVelocity
            );
            //drivePower = driveController.calculate(getVelocity().toMPS(), state.speedMetersPerSecond);
        } else {
            driveMotor.set(Velocity.fromMPS(state.speedMetersPerSecond * errorFactor)
                    .toMotorPower(config.getMaxVelocity()));
        }
        turnMotor.set(turnPower);
    }

    public void setState(SwerveModuleState state) { setState(state, true); }

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
     * is required for {@link SwerveOdometry} to work correctly.
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

    public double getRPM() {
        return driveEncoder.getVelocity();
    }

    public void updateDashboard(String prefix) {
        String driveVelocity = prefix + ": rpm";
        String drivePower = prefix + ": pow";
        String turnPower = prefix + ": turn pow";
        String turnPosition = prefix + ": turn rad";

        if (TEST_MODE) {
            SmartDashboard.putNumber(driveVelocity, getRPM());
            SmartDashboard.putNumber(turnPower, turnMotor.get());
            SmartDashboard.putNumber(turnPosition, getTurnAngle().getDegrees());
            SmartDashboard.putNumber(drivePower, driveMotor.get());
            SmartDashboard.putNumber(prefix + " drive encoder: ", driveEncoder.getPosition());
        }
    }

    /**
     * get the elapsed distance, in rotations.
     *
     * @return the elapsed distance, in rotations
     */
    public double getRotations() {
        return driveEncoder.getPosition();
    }

    /**
     * @return The total amount of meters the individual {@link SwerveModule} has travelled.
     */
    public Distance getDistance() {
        return Distance.fromMeters(driveEncoder.getPosition());
    }

    public void resetDriveEncoder() {
        driveEncoder.setPosition(0);
    }
}