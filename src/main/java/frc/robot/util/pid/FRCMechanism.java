package frc.robot.util.pid;

import com.revrobotics.*;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.util.log.AlertManager;
import frc.robot.util.math.GearRatio;
import frc.robot.util.motor.FRCSparkMax;

import java.util.Optional;
import java.util.function.Supplier;

import static com.revrobotics.CANSparkMax.ControlType.*;
import static com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushed;
import static com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushless;
import static edu.wpi.first.math.MathUtil.clamp;
import static frc.robot.util.math.ExtendedMath.inTolerance;

/**
 * This {@link FRCMechanism} is intended to make {@link CANSparkMax} PID control easier to
 * implement. It automatically takes care of setting target rotations, encoders, zeroing, etc. This PID system
 * is also designed to be operated manually using the <code>translateMotor</code> method.
 *
 * @author Eric Gold
 * @since 0.0.0
 */
public class FRCMechanism extends SubsystemBase {
    public static final double DISABLED = Double.MIN_VALUE;

    private final FRCSparkMax motor;
    private final String name;
    private final GearRatio ratio;
    private final SparkMaxPIDController controller;
    private final RelativeEncoder encoder;

    private Supplier<Boolean> limitBypassSupplier;
    private Supplier<Double> targetSupplier;


    private double targetRotation;
    private double maxSpeed;
    private double tolerance;
    private double maxRotation;
    private double minRotation;
    private double lastTarget;

    private boolean teleopMode;
    private boolean dashEnabled;
    private boolean pidEnabled;

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
     * Sets the forward limit of the {@link FRCMechanism}.
     * @param rotation The gear-ratio adjusted {@link Rotation2d}.
     * @return The current {@link FRCMechanism} instance.
     */
    public FRCMechanism setForwardLimit(Rotation2d rotation) {
        this.maxRotation = rotation.getRotations();
        return this;
    }

    /**
     * Sets the reverse limit of the {@link FRCMechanism}.
     * @param rotation The gear-ratio adjusted {@link Rotation2d}.
     * @return The current {@link FRCMechanism} instance.
     */
    public FRCMechanism setReverseLimit(Rotation2d rotation) {
        this.minRotation = rotation.getRotations();
        return this;
    }

    /**
     * Sets the limit-bypass {@link Supplier} where the limit will not be enforced upon true.
     * @param supplier The {@link Supplier} to use.
     * @return         The current {@link FRCMechanism} instance.
     */
    public FRCMechanism setLimitBypassSupplier(Supplier<Boolean> supplier) {
        this.limitBypassSupplier = supplier;
        return this;
    }

    /**
     * Sets the target {@link Supplier} where the {@link FRCMechanism} will try to achieve.
     * @param supplier The {@link Supplier} to use.
     * @return         The current {@link FRCMechanism} instance.
     */
    public FRCMechanism setTargetSupplier(Supplier<Double> supplier) {
        this.targetSupplier = supplier;
        return this;
    }

    /**
     * Controls the ability for the {@link FRCMechanism} to perform Dashboard logging.
     * @param status Desired Dashboard logging status.
     * @return The {@link FRCMechanism} instance.
     */
    public FRCMechanism setDashboard(boolean status) {
        this.dashEnabled = statu;
        return this;
    }

    /** @return The reverse limit, or {@link Optional#empty()} is in-applicable.*/
    public Optional<Rotation2d> getReverseLimit() {
        return minRotation == DISABLED ? Optional.empty() : Optional.of(Rotation2d.fromRotations(minRotation));
    }
    /** @return The forward limit, or {@link Optional#empty()} if in-applicable. */
    public Optional<Rotation2d> getForwardLimit() {
        return maxRotation == DISABLED ? Optional.empty() : Optional.of(Rotation2d.fromRotations(maxRotation));
    }

    /** @return The {@link Supplier} which bypasses the soft-limit-switch upon enabling. */
    public Optional<Supplier<Boolean>> getLimitBypassSupplier() { return Optional.of(limitBypassSupplier); }

    /** @return The current {@link Encoder} position of the {@link CANSparkMax} motor. */
    public double getRotation() { return getAdjustedPosition(); }

    /** @return The {@link Supplier} used to determine the target. */
    public Supplier<Double> getTargetSupplier() { return this.targetSupplier; }

    /** @return The current Target {@link Encoder} position of the {@link CANSparkMax} motor. */
    public double getTargetRotation() { return targetRotation; }

    /** @return The {@link GearRatio} of the {@link FRCMechanism}. */
    public GearRatio getGearRatio() { return this.ratio; }

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
     * @return {@link FRCMechanism}
     */
    public FRCMechanism setTolerance(double rotations) {
        this.tolerance = rotations;
        return this;
    }

    /**
     * Resets the {@link Encoder} used for measuring position.
     */
    public void resetEncoder() {
        setAdjustedPosition(0);
        targetRotation = 0;
    }

    /**
     * Sets the Maximum Speed for the {@link PIDController}. This will prevent the system from operating more than
     * plus/minus the specified <code>speed</code>
     *
     * @param speed The <code>speed</code> from -1.0 to +1.0 to use.
     * @return {@link FRCMechanism}
     */
    public FRCMechanism setMaxSpeed(double speed) {
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
            if (power > 0 && getAdjustedPosition() >= forwardLimit) {
                return 0;
            } else {
                return power;
            }
        } else if (reverseLimit != Double.MIN_VALUE) {
            if (power < 0 && getAdjustedPosition() <= reverseLimit) {
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

    /**
     * Sets the PID values for the {@link FRCMechanism}.
     * @param p Proportional Value
     * @param i Integral Value
     * @param d Derivative Value
     * @return The current {@link FRCMechanism} instance.
     */
    public FRCMechanism setPID(double p, double i, double d) {
        controller.setP(p);
        controller.setI(i);
        controller.setD(d);
        return this;
    }

    /**
     * @return If the {@link Encoder} is within the tolerance of the Target. Useful for conditions
     * depending on the Motor being moved to a certain position before proceeding.
     */
    public boolean atTarget() {
        return inTolerance(getRotation(), getTargetRotation(), tolerance);
    }

    /**
     * Constructs a new {@link FRCMechanism} with the following parameters.
     * @param name  The name of the {@link FRCMechanism}.
     * @param motor The {@link FRCSparkMax} used to control the {@link FRCMechanism}.
     * @param ratio The final {@link GearRatio} used to drive the {@link FRCSparkMax}.
     * @param kP    The Proportional Value for PID-control.
     * @param kI    The Integral Value for PID-control.
     * @param kD    The Derivative Value for PID-control.
     * @param kFF   The Feed-Forward Value for PID-control.
     * @see #setPID(double, double, double)
     */
    public FRCMechanism(String name, FRCSparkMax motor, GearRatio ratio,
                        double kP, double kI, double kD, double kFF) {
        this.name = name;
        this.motor = motor;
        this.ratio = ratio;
        this.controller = motor.getPIDController();
        this.encoder = motor.getEncoder();
        this.limitBypassSupplier = null;

        this.dashEnabled = false;
        this.teleopMode = false;
        this.pidEnabled = false;
        this.maxSpeed = 1;
        this.tolerance = 0.5;

        setPID(kP, kI, kD);
        controller.setFF(kFF);

        targetRotation = encoder.getPosition();
        AlertManager.warnOnFail(motor.enableVoltageCompensation(12));
    }

    /**
     * Constructs a new {@link FRCMechanism} with the following parameters.
     * <p></p>
     * <b>NOTE:</b> Upon creation, all PID values will be zero! As a result, the mechanism will
     * not move until proper values are set.
     * @param name  The name of the {@link FRCMechanism}.
     * @param motor The {@link FRCSparkMax} used to control the {@link FRCMechanism}.
     * @param ratio The final {@link GearRatio} used to drive the {@link FRCSparkMax}.
     */
    public FRCMechanism(String name, FRCSparkMax motor, GearRatio ratio) {
        this(name, motor, ratio, 0, 0, 0, 0);
    }

    public Command resetEncoderCommand() { return this.runOnce(this::resetEncoder); }

    @Override
    public void periodic() {
        if (lastTarget == Double.MAX_VALUE) {
            lastTarget = targetSupplier.get();
        }

        if (!teleopMode && !atTarget())
            controller.setReference(getTargetRotation(), kPosition);
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