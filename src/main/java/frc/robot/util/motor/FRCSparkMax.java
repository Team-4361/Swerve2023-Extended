package frc.robot.util.motor;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.REVLibError;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.jni.CANSparkMaxJNI;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Temperature;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.util.log.Alert;
import frc.robot.util.log.AlertCondition;
import frc.robot.util.log.AlertManager;
import frc.robot.util.log.AlertType;
import frc.robot.util.loop.Looper;
import frc.robot.util.loop.LooperManager;

import java.time.Duration;

import static edu.wpi.first.units.Units.Celsius;
import static frc.robot.Constants.AlertConfig.GROUP_NAME;
import static frc.robot.Constants.LooperConfig.LOW_PRIORITY_NAME;
import static frc.robot.Constants.LooperConfig.PERIODIC_NAME;

/**
 * This class enables a safe interaction with {@link CANSparkMax} motors; temperature control and watchdogs
 * are both used to prevent dangerous situations.
 *
 * @author Eric Gold
 * @since 0.0.0
 */
public class FRCSparkMax extends CANSparkMax {
    /** The {@link Temperature} in which the Motor will be temporarily disabled. */
    public static final Measure<Temperature> DEFAULT_CUTOFF_TEMP = Celsius.of(60);

    ///////////////////////////////////////////////////////////////////////////////////////////////

    private final Alert overTemp;
    private double cutoffTempC, maxSpeed;
    private long lastSimUpdate;

    /**
     * Sets the maximum allowed speed for the {@link FRCSparkMax} motor.
     * @param maxSpeed The maximum <b>POSITIVE</b> speed (0.0 to +1.0)
     * @return The current {@link FRCSparkMax} instance.
     */
    public FRCSparkMax setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
        return this;
    }

    /*
    public FRCSparkMax setMaxRotation(double limit) {
        this.maxRotation = limit;
        return this;
    }

    public FRCSparkMax setMinRotation(double limit) {
        this.minRotation = limit;
        return this;
    }
     */

    /**
     * Create a new object to control a SPARK MAX motor Controller
     *
     * @param deviceId The device ID.
     * @param type     The motor type connected to the controller. Brushless motor wires must be connected to their
     *                 matching colors and the hall sensor must be plugged in. Brushed motors must be connected to the
     *                 Red and Black terminals only.
     */
    public FRCSparkMax(int deviceId, MotorType type) {
        super(deviceId, type);

        this.maxSpeed = 1;
        this.cutoffTempC = DEFAULT_CUTOFF_TEMP.in(Celsius);
        this.lastSimUpdate = System.currentTimeMillis();
        this.overTemp = new Alert(
                "Motor " + deviceId + " over-temperature.",
                AlertType.ERROR, // serious condition; it can cause permanent damage!
                new AlertCondition(() -> getMotorTemperature() >= cutoffTempC) // enable condition
                        .setEnableDelay(Duration.ofSeconds(5))  // allow brief periods of over-temp
                        .setDisableDelay(Duration.ofSeconds(0)) // disable instantly.
                        .setAutoDisable(true));
        overTemp.addInit(this::stopMotor);

        // Add the over-temperature alert.
        AlertManager.getGroup(GROUP_NAME).addAlert(overTemp);

        // If the Robot is using simulation-mode, run a periodic cycle.
        if (RobotBase.isSimulation() && type == MotorType.kBrushless) {
            LooperManager.getLoop(PERIODIC_NAME).addSimPeriodic(() -> {
                final long dtMs = System.currentTimeMillis() - lastSimUpdate;
                if (dtMs < 20) return;

                // Simulate a quadratic acceleration curve of a typical motor?
                final double position = getEncoder().getPosition();
                final double rpm = Units.radiansPerSecondToRotationsPerMinute(5676 * get());
                getEncoder().setPosition(position + rpm * dtMs / 60000.0);
            });
        }
    }

    /**
     * Set the free speed of the motor being simulated.
     *
     * @param freeSpeed the free speed (RPM) of the motor connected to spark max
     * @return {@link REVLibError#kOk} if successful
     */
    public REVLibError setSimFreeSpeed(final double freeSpeed) {
        throwIfClosed();
        return REVLibError.fromInt(
                CANSparkMaxJNI.c_SparkMax_SetSimFreeSpeed(sparkMaxHandle, (float)freeSpeed));
    }

    /**
     * Set the stall torque of the motor being simulated.
     *
     * @param stallTorque The stall torque (N m) of the motor connected to sparkmax
     * @return {@link REVLibError#kOk} if successful
     */
    public REVLibError setSimStallTorque(final double stallTorque) {
        throwIfClosed();
        return REVLibError.fromInt(
                CANSparkMaxJNI.c_SparkMax_SetSimStallTorque(sparkMaxHandle, (float) stallTorque));
    }

    /**
     * Get if the internal SparkMAX has been closed.
     * @return true if already closed, false if not.
     * @see com.revrobotics.CANSparkMaxLowLevel#isClosed
     */
    public boolean isClosed() {
        return isClosed.get();
    }

    /**
     * Internal Rev method, made public through overriding {@link CANSparkMaxLowLevel#throwIfClosed()}
     */
    @Override
    public void throwIfClosed() {
        super.throwIfClosed();
    }

    @Override
    public void set(final double speed) {
        if (overTemp.isEnabled()) {
            super.set(0); // do not enable if over-temp!
            getPIDController().setReference(0, ControlType.kDutyCycle);
        } else {
            super.set(MathUtil.clamp(speed, -maxSpeed, maxSpeed));
        }
    }

    public void set(final ControlType controlType, final double value) {
        if (overTemp.isEnabled())
            getPIDController().setReference(0, ControlType.kDutyCycle);
        else
            getPIDController().setReference(value, controlType);
    }

    /**
     * Sets the {@link Temperature} in which the Motor will be temporarily disabled.
     * @param tempC The desired {@link Temperature} in Celsius.
     */
    public void setCutoffTemp(double tempC) { cutoffTempC = tempC; }

    /** The {@link Temperature} in which the Motor will be temporarily disabled. */
    public double getCutoffTemp() { return cutoffTempC; }

    public double getMaxSpeed() { return this.maxSpeed; }

    /*
    public double getMaxRotation() { return this.maxRotation; }

    public double getMinRotation() { return this.minRotation; }
    */
}
