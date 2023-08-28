package frc.robot.util.pid;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import java.util.function.Supplier;

import static com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushed;
import static com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushless;
import static edu.wpi.first.math.MathUtil.clamp;

/**
 * This {@link SparkMaxPIDSubsystem} is intended to make {@link CANSparkMax} PID control easier to
 * implement. It automatically takes care of setting target rotations, encoders, zeroing, etc. This PID system
 * is also designed to be operated manually using the <code>translateMotor</code> method.
 *
 * @author Eric Gold (ericg2)
 */
public class SparkMaxPIDSubsystem extends SubsystemBase {
    public static final PIDController DEFAULT_CONTROLLER = new PIDController(
            0.01, 0, 0
    );

    private final CANSparkMax motor;
    private final PIDController controller;
    private final RelativeEncoder encoder;
    private final String name;

    private boolean dashEnabled = true;

    private Supplier<Double> presetSupplier;
    private Supplier<Boolean> pidEnabledSupplier;
    private Supplier<Boolean> limitBypassSupplier;

    private double targetRotation, maxSpeed, tolerance;
    private double forwardLimit = Double.MAX_VALUE;
    private double reverseLimit = Double.MIN_VALUE;
    private double lastTarget = Double.MAX_VALUE;

    private boolean teleopMode;

    /**
     * Sets the Target Rotation that the {@link Encoder} should be set to. While teleoperation mode is disabled,
     * the motor will always spin to match the target.
     *
     * @param rotation The amount of rotations to set the Target for.
     * @see #translateMotor(double)
     */
    public void setTarget(double rotation) {
        this.targetRotation = getLimitAdjustedTarget(rotation);
    }

    /**
     * Sets the <b>minimum</b> allowed value which the {@link Encoder} can reach. Any lower value
     * will prevent the motor from retracting any further without a bypass enabled.
     *
     * @param limit The minimum allowed value for retraction, unit needs to match {@link #getRotation()}
     * @return The {@link SparkMaxPIDSubsystem} instance.
     */
    public SparkMaxPIDSubsystem setReverseLimit(double limit) {
        this.reverseLimit = limit;
        return this;
    }

    /**
     * Sets the <b>maximum</b> allowed value which the {@link Encoder} can reach. Any higher value
     * will prevent the motor from extending any further without a bypass enabled.
     *
     * @param limit The minimum allowed value for extension, unit needs to match {@link #getRotation()}
     * @return The {@link SparkMaxPIDSubsystem} instance.
     */
    public SparkMaxPIDSubsystem setForwardLimit(double limit) {
        this.forwardLimit = limit;
        return this;
    }

    /**
     * Sets the {@link Supplier} used for determining the limit bypass switch. If enabled, no
     * soft-limit will be applicable.
     *
     * @param supplier A boolean {@link Supplier} for limit bypass determination.
     * @return The {@link SparkMaxPIDSubsystem} instance.
     */
    public SparkMaxPIDSubsystem setLimitBypassSupplier(Supplier<Boolean> supplier) {
        this.limitBypassSupplier = supplier;
        return this;
    }

    /** @return The minimum allowed reverse value. Unit matches {@link #getRotation()} */
    public double getReverseLimit() { return this.reverseLimit; }

    /** @return The maximum allowed forward value. Unit matches {@link #getRotation()} */
    public double getForwardLimit() { return this.forwardLimit; }

    /** @return The limit bypass {@link Supplier} used for determining the validity of the soft-limit. */
    public Supplier<Boolean> getLimitBypassSupplier() { return this.limitBypassSupplier; }

    /**
     * Controls the option for enabling Dashboard control of presets.
     * @param dashEnabled A {@link Boolean} determining Dashboard control. Default is false.
     * @return The {@link SparkMaxPIDSubsystem} instance.
     */
    public SparkMaxPIDSubsystem enableDashboard(boolean dashEnabled) {
        this.dashEnabled = dashEnabled;
        return this;
    }

    public SparkMaxPIDSubsystem setPresetMap(PresetList list, Supplier<Double> presetSupplier) {
        this.presetSupplier = presetSupplier;
        list.addListener((value) -> updateTarget());
        return this;
    }

    public SparkMaxPIDSubsystem setPresetMap(PresetMap list, Supplier<Double> presetSupplier) {
        this.presetSupplier = presetSupplier;

        list.addListener((value) -> updateTarget());
        return this;
    }

    public SparkMaxPIDSubsystem setContInput(double start, double end) {
        controller.enableContinuousInput(start, end);
        return this;
    }


    /** @return The current {@link Encoder} position of the {@link CANSparkMax} motor. */
    public double getRotation() { return encoder.getPosition(); }

    public Supplier<Double> getPresetSupplier() { return presetSupplier; }

    /** @return The current Target {@link Encoder} position of the {@link CANSparkMax} motor. */
    public double getTargetRotation() { return targetRotation; }

    /**
     * Manually translates the motor using a given <code>speed</code>. While <code>speed</code> is not zero, the
     * PID control is disabled, allowing manual rotation to occur. The Target Rotation is set to the current {@link Encoder}
     * reading during non-zero operation.
     *
     * @param power A motor power from -1.0 to +1.0 to spin the motor.
     */
    public void translateMotor(double power) {
        if (DriverStation.isTeleop())
        {
            if (power == 0 && teleopMode) {
                // Set the target angle to the current rotations to freeze the value and prevent the PIDController from
                // automatically adjusting to the previous value.
                setTarget(getRotation());
                teleopMode = false;
            }
            if (power != 0 && !teleopMode)
                teleopMode = true;
    
            motor.set(getLimitAdjustedPower(power));
        }
       
    }

    /**
     * Sets the Tolerance for the {@link PIDController}. This will prevent any encoder inaccuracies from stalling
     * the motor when the target is reached.
     *
     * @param rotations The amount of <b>rotations</b> for Tolerance.
     * @return {@link SparkMaxPIDSubsystem}
     */
    public SparkMaxPIDSubsystem setTolerance(double rotations) {
        this.tolerance = rotations;
        return this;
    }

    /**
     * Resets the {@link Encoder} used for measuring position.
     */
    public void resetEncoder() {
        assert encoder != null;
        encoder.setPosition(0);
        targetRotation = 0;
    }

    /**
     * Sets the Maximum Speed for the {@link PIDController}. This will prevent the system from operating more than
     * plus/minus the specified <code>speed</code>
     *
     * @param speed The <code>speed</code> from -1.0 to +1.0 to use.
     * @return {@link SparkMaxPIDSubsystem}
     */
    public SparkMaxPIDSubsystem setMaxSpeed(double speed) {
        this.maxSpeed = speed;
        return this;
    }

    private double getLimitAdjustedTarget(double angle) {
        // Do not perform any calculatiosn if the limit bypass supplier is true.
        if (limitBypassSupplier.get()) return angle;

        if (forwardLimit != Double.MAX_VALUE) {
            if (angle > forwardLimit) {
                angle = forwardLimit-0.1;
            }
        }
        if (reverseLimit != Double.MIN_VALUE) {
            if (angle < reverseLimit) {
                angle = reverseLimit+0.1;
            }
        }

        return angle;
    }

    private double getLimitAdjustedPower(double power) {
        if (power == 0) return 0;

        // Do not perform any calculations if the limit bypass supplier is true.
        if (limitBypassSupplier.get()) return power;

        if (forwardLimit != Double.MAX_VALUE) {
            if (power > 0 && encoder.getPosition() >= forwardLimit) {
                return 0;
            } else {
                return power;
            }
        } else if (reverseLimit != Double.MIN_VALUE) {
            if (power < 0 && encoder.getPosition() <= reverseLimit) {
                return 0;
            } else {
                return power;
            }
        } else {
            // At the stage, no limit has been set. Do not perform calculations and return the initial power.
            return power;
        }
    }

    /* The tolerance used for the {@link PIDController}. */
    public double getTolerance() {
        return tolerance;
    }

    /** @return The maximum speed of the {@link PIDController}. */
    public double getMaxSpeed() {
        return maxSpeed;
    }

    public SparkMaxPIDSubsystem setPID(double p, double i, double d) {
        controller.setPID(p, i, d);
        return this;
    }

    /**
     * Checks if the actual value is within a specified tolerance of the expected value
     * @param expected The value to be expected.
     * @param actual The actual value.
     * @param tolerance The maximum error or tolerance that the value can be offset to still be true.
     * @return True/false depending on tolerance.
     */
    private static boolean inTolerance(double expected, double actual, double tolerance) {
        return Math.abs(expected - actual) <= tolerance;
    }

    /**
     * @return If the {@link Encoder} is within the tolerance of the Target. Useful for conditions
     * depending on the Motor being moved to a certain position before proceeding.
     */
    public boolean atTarget() {
        return inTolerance(getRotation(), getTargetRotation(), tolerance);
    }

    public SparkMaxPIDSubsystem invert(boolean inverted) {
        motor.setInverted(inverted);
        return this;
    }


    public SparkMaxPIDSubsystem(String name, CANSparkMax motor, PIDController controller) {
        assert motor.getMotorType() != kBrushed;

        this.controller = controller;
        this.motor = motor;
        this.name = name;
        this.teleopMode = false;
        this.pidEnabledSupplier = () -> true;
        this.limitBypassSupplier = () -> false;
        this.maxSpeed = 1;
        this.tolerance = 0.5;
        this.encoder = motor.getEncoder();
        this.targetRotation = encoder.getPosition();

        motor.enableVoltageCompensation(12);
    }

    public SparkMaxPIDSubsystem(String name, CANSparkMax motor, double kP, double kI, double kD) {
        this(name, motor, new PIDController(kP, kI, kD));
    }

    public SparkMaxPIDSubsystem(String name, int motorID, double kP, double kI, double kD) {
        this(name, new CANSparkMax(motorID, kBrushless), kP, kI, kD);
    }

    public SparkMaxPIDSubsystem(String name, int motorID) {
        this(name, new CANSparkMax(motorID, kBrushless), DEFAULT_CONTROLLER);
    }

    public SparkMaxPIDSubsystem setPIDControlSupplier(Supplier<Boolean> supplier) {
        pidEnabledSupplier = supplier;
        return this;
    }

    public void updateTarget() {
        setTarget(presetSupplier.get());
        lastTarget = presetSupplier.get();
    }

    public Command resetEncoderCommand() { return this.runOnce(this::resetEncoder); }

    @Override
    public void periodic() {
        if (lastTarget == Double.MAX_VALUE) {
            lastTarget = presetSupplier.get();
        }

        if (!teleopMode && !atTarget() && pidEnabledSupplier.get())
            motor.set(getLimitAdjustedPower(clamp(controller.calculate(getRotation(), getTargetRotation()), -maxSpeed, maxSpeed)));

        if (dashEnabled) {
            SmartDashboard.putNumber(name + " Rotation", getRotation());
            SmartDashboard.putNumber(name + " Target Rotation", getTargetRotation());
            SmartDashboard.putBoolean(name + " At Target", atTarget());
        }

    
        double suppliedTarget = presetSupplier.get();
        if (lastTarget != presetSupplier.get()) {
            setTarget(suppliedTarget);
            lastTarget = suppliedTarget;
        }
    }
}