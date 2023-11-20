package frc.robot.util.motor;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.REVLibError;
import com.revrobotics.jni.CANSparkMaxJNI;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Temperature;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import frc.robot.util.log.Alert;
import frc.robot.util.log.AlertCondition;
import frc.robot.util.log.AlertManager;
import frc.robot.util.log.AlertType;

import java.time.Duration;

import static edu.wpi.first.units.Units.Celsius;
import static frc.robot.Constants.AlertConfig.GROUP_NAME;

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

    private double cutoffTempC;

    /**
     * Create a new object to control a SPARK MAX motor Controller
     *
     * @param deviceId The device ID.
     * @param type     The motor type connected to the controller. Brushless motor wires must be connected to their
     *                 matching colors and the hall sensor must be plugged in. Brushed motors must be connected to the
     *                 Red and Black terminals only.
     */
    public FRCSparkMax(int deviceId, MotorType type, DCMotor model) {
        super(deviceId, type);

        // Add the temperature cut-off alert.
        AlertManager
                .getGroup(GROUP_NAME)
                .addAlert(new Alert(
                        "Motor " + deviceId + " over-temperature.",
                        AlertType.ERROR, // serious condition; it can cause permanent damage!
                        new AlertCondition(() -> getMotorTemperature() >= cutoffTempC) // enable condition
                                .setEnableDelay(Duration.ofSeconds(5))  // allow brief periods of over-temp
                                .setDisableDelay(Duration.ofSeconds(0)) // disable instantly.
                                .setAutoDisable(true)                   // allow disabling.
                )
        );

        // If simulation: set required values.
        if (RobotBase.isSimulation()) {
            new DCMotorSim()
            setSimFreeSpeed(model.getCurrent()
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
        super.set(MathUtil.clamp(speed, -1, 1));
    }

    public void set(final ControlType controlType, final double value) {
        this.getPIDController().setReference(value, controlType);
    }

    /**
     * Sets the {@link Temperature} in which the Motor will be temporarily disabled.
     * @param tempC The desired {@link Temperature} in Celsius.
     */
    public void setCutoffTemp(double tempC) { cutoffTempC = tempC; }

    /** The {@link Temperature} in which the Motor will be temporarily disabled. */
    public double getCutoffTemp() { return cutoffTempC; }
}
