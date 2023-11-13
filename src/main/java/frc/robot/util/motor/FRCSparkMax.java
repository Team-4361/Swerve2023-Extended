package frc.robot.util.motor;

import com.revrobotics.CANSparkMax;
import com.revrobotics.REVLibError;
import com.revrobotics.jni.CANSparkMaxJNI;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.system.plant.DCMotor;
import frc.robot.util.log.AlertManager;
import frc.robot.util.loop.Looper;
import frc.robot.util.measurement.AngularVelocity;
import frc.robot.util.measurement.Temperature;

import java.time.Duration;

/**
 * This class enables a safe interaction with {@link CANSparkMax} motors; temperature control and watchdogs
 * are both used to prevent dangerous situations.
 *
 * @author Eric Gold
 * @since 0.0.0
 */
public class FRCSparkMax extends CANSparkMax {
    /** The {@link Temperature} in which the Driver/Operator will be warned, but with no action taken. */
    public static Temperature DEFAULT_WARN_TEMP = Temperature.fromC(60);

    /** The {@link Temperature} in which the Motor will be temporarily disabled. */
    public static Temperature DEFAULT_CUTOFF_TEMP = Temperature.fromC(100);

    /** The {@link Looper} used for simulation, and debugging if applicable. */
    private static final Looper periodicLooper = new Looper()
            .setInterval(Duration.ofMillis(20)); // run sim every 2 seconds.

    /** The {@link Looper} used for temperature checking. */
    private static final Looper tempLooper = new Looper()
            .setInterval(Duration.ofSeconds(5)); // check temp every 5 seconds.

    ///////////////////////////////////////////////////////////////////////////////////////////////

    private Temperature warnTemp = DEFAULT_WARN_TEMP;
    private Temperature cutoffTemp = DEFAULT_CUTOFF_TEMP;

    /**
     * Create a new object to control a SPARK MAX motor Controller
     *
     * @param deviceId      The device ID.
     * @param brushType     The motor type connected to the controller. Brushless motor wires must be connected
     *                      to their matching colors and the hall sensor must be plugged in. Brushed motors must be
     *                      connected to the Red and Black terminals only.
     * @param motorType     The make/model of the connected Motor; used for simulation purposes.
     */
    public FRCSparkMax(int deviceId, MotorType brushType, DCMotor motorType) {
        super(deviceId, brushType);
        AlertManager.warnOnFail(setSimFreeSpeed(AngularVelocity
                .fromRPS(motorType.freeSpeedRadPerSec)
                .toRPM()
        ), "Sim free speed fail.");

        AlertManager.warnOnFail(setSimStallTorque(motorType.stallTorqueNewtonMeters), "Sim torque fail.");

        // Add the simulation loop (called every 20ms)
        /*
        if (getMotorType() == MotorType.kBrushless) {
            loop.addSimPeriodic(() -> {
                // Make sure the periodic cycle is only ran every 20ms.
                final long dtMs = System.currentTimeMillis() - lastSimUpdate;

                if (dtMs < 20)
                    return;

                final RelativeEncoder encoder = getEncoder();
                final double position = encoder.getPosition();
                final double velocity = encoder.getVelocity();
                final double positionConversionFactor = encoder.getPositionConversionFactor();

                encoder.setPosition(position + velocity * dtMs / 60000.0 * positionConversionFactor);

                lastSimUpdate = System.currentTimeMillis();
            });
        }

        // Add the temperature control loop (called every 2 seconds)
        loop.addPeriodic(() -> {

        });

         */
    }

    /**
     * Create a new object to control a SPARK MAX motor Controller. Simulation type defaulted to NEO.
     *
     * @param deviceId      The device ID.
     * @param brushType     The motor type connected to the controller. Brushless motor wires must be connected
     *                      to their matching colors and the hall sensor must be plugged in. Brushed motors must be
     *                      connected to the Red and Black terminals only.
     */
    public FRCSparkMax(int deviceId, MotorType brushType) {
        this(deviceId, brushType, DCMotor.getNEO(1));
    }

    /**
     * Sets the {@link Temperature} in which the Driver/Operator will be warned, but with no action taken.
     * @param temp The desired {@link Temperature}
     */
    public void setWarnTemp(Temperature temp) { this.warnTemp = temp; }

    /**
     * Sets the {@link Temperature} in which the Motor will be temporarily disabled.
     * @param temp The desired {@link Temperature}
     */
    public void setCutoffTemp(Temperature temp) { this.cutoffTemp = temp; }

    /** @return The {@link Temperature} of the motor. */
    public Temperature getTemperature() { return Temperature.fromC(getMotorTemperature()); }

    /** @return The {@link Temperature} in which the Driver/Operator will be warned, but with no action taken. */
    public Temperature getWarnTemp() { return warnTemp; }

    /** The {@link Temperature} in which the Motor will be temporarily disabled. */
    public Temperature getCutoffTemp() { return cutoffTemp; }

    /**
     * Set the free speed of the motor being simulated.
     *
     * @param freeSpeed The free speed (RPM) of the motor connected to {@link FRCSparkMax}
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
     * @param stallTorque The stall torque (Nm) of the motor connected to {@link FRCSparkMax}
     * @return {@link REVLibError#kOk} if successful
     */
    public REVLibError setSimStallTorque(double stallTorque) {
        throwIfClosed();
        return REVLibError.fromInt(
                CANSparkMaxJNI.c_SparkMax_SetSimStallTorque(sparkMaxHandle, (float) stallTorque));
    }

    @Override
    public void set(double speed) {
        super.set(MathUtil.clamp(speed, -1, 1));
    }
}
