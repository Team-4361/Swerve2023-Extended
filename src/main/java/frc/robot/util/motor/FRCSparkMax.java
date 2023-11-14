package frc.robot.util.motor;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.MutableMeasure;
import edu.wpi.first.units.Temperature;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.util.loop.Looper;
import frc.robot.util.motor.MotorIO.MotorIOInputs;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.inputs.LoggedSystemStats;

import java.time.Duration;

import static edu.wpi.first.units.Units.Celsius;

/**
 * This class enables a safe interaction with {@link CANSparkMax} motors; temperature control and watchdogs
 * are both used to prevent dangerous situations.
 *
 * @author Eric Gold
 * @since 0.0.0
 */
public class FRCSparkMax extends SubsystemBase {
    /** The {@link Temperature} in which the Driver/Operator will be warned, but with no action taken. */
    public static Measure<Temperature> DEFAULT_WARN_TEMP = Celsius.of(40);

    /** The {@link Temperature} in which the Motor will be temporarily disabled. */
    public static Measure<Temperature> DEFAULT_CUTOFF_TEMP = Celsius.of(40);


    /** The {@link Looper} used for temperature checking. */
    private static final Looper tempLooper = new Looper()
            .setInterval(Duration.ofSeconds(5)); // check temp every 5 seconds.

    ///////////////////////////////////////////////////////////////////////////////////////////////

    // Allow mutable for performance reasons.
    private final MutableMeasure<Temperature> warnTemp = DEFAULT_WARN_TEMP.mutableCopy();
    private final MutableMeasure<Temperature> cutoffTemp = DEFAULT_CUTOFF_TEMP.mutableCopy();

    private final MotorIO io;
    private MotorIOInputs inputs;

    /**
     * This method is called periodically by the {@link CommandScheduler}. Useful for updating subsystem-specific state
     * that you don't want to offload to a {@link Command}. Teams should try to be consistent within their own codebases
     * about which responsibilities will be handled by Commands, and which will be handled here.
     */
    @Override
    public void periodic() {
        io.updateInputs(inputs);
        Logger.getInstance().processInputs("Motor " + getID(), inputs);
    }

    public double get() { return inputs.appliedPower; }
    public double getRotations() { return inputs.rotations; }
    public double getVelocity() { return inputs.velocityRPM; }
    public double getTemperatureC() { return inputs.temperatureC; }
    public double getCurrent() { return inputs.amperes; }
    public boolean isInverted() { return inputs.inverted; }
    public MotorType getMotorType() { return inputs.motorType; }
    public long getID() { return inputs.id; }

    public void set(double power) { io.set(power); }
    public void setInverted(boolean inverted) { io.setInverted(inverted); }

    /**
     * Create a new object to control a SPARK MAX motor Controller
     *
     * @param io  The {@link MotorIO} instance used for hardware-communication.
     */
    public FRCSparkMax(MotorIO io) {
        this.io = io;
        this.inputs = new MotorIOInputs();

        // Add the temperature control loop (called every 2 seconds)
        tempLooper.addPeriodic(() -> {
            if (inputs.temperatureC >= warnTemp.in(Celsius)) {
                // TODO: implement!
            }
        });
    }

    /**
     * Sets the {@link Temperature} in which the Driver/Operator will be warned, but with no action taken.
     * @param temp The desired {@link Temperature}
     */
    public void setWarnTemp(Measure<Temperature> temp) { this.warnTemp.mut_replace(temp); }

    /**
     * Sets the {@link Temperature} in which the Motor will be temporarily disabled.
     * @param temp The desired {@link Temperature}
     */
    public void setCutoffTemp(Measure<Temperature> temp) { this.cutoffTemp.mut_replace(temp); }

    /** @return The {@link Temperature} in which the Driver/Operator will be warned, but with no action taken. */
    public final Measure<Temperature> getWarnTemp() { return warnTemp; }

    /** The {@link Temperature} in which the Motor will be temporarily disabled. */
    public final Measure<Temperature> getCutoffTemp() { return cutoffTemp; }
}
