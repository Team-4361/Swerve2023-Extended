package frc.robot.util.motor;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.util.Units;

/**
 * This {@link MotorIOSim} class is designed to function with a <b>SIMULATED</b> {@link CANSparkMax}.
 *
 * @author Eric Gold
 * @since 0.0.0
 */
public class MotorIOSim implements MotorIO {
    private final MotorType brushType;
    private final DCMotor motorType;
    private final int id;

    private long lastSimUpdate;
    private boolean isInverted;
    private double appliedPower;
    private double currentRPM;

    /**
     * Constructs a new {@link MotorIOReal} using the specified parameters.
     */
    public MotorIOSim(int id, MotorType type, DCMotor motorType) {
        this.id = id;
        this.brushType = type;
        this.motorType = motorType;
        this.lastSimUpdate = System.currentTimeMillis();
        this.isInverted = false;
        this.appliedPower = 0.0;
        this.currentRPM = 0;
    }

    /**
     * Updates the required inputs.
     * @param inputs The {@link MotorIOInputs} to use.
     */
    @Override
    public void updateInputs(MotorIOInputs inputs) {
        if (brushType == MotorType.kBrushless) {
            final long dtMs = System.currentTimeMillis() - lastSimUpdate;
            if (dtMs < 20) return;

            // Simulate a quadratic acceleration curve of a typical motor?
            final double position = inputs.rotations;
            final double desiredRPM = Units.radiansPerSecondToRotationsPerMinute(
                    motorType.freeSpeedRadPerSec * appliedPower
            );

            inputs.velocityRPM = desiredRPM;
            inputs.rotations = position + currentRPM * dtMs / 60000.0;
        }

        inputs.inverted = isInverted;
        inputs.id = this.id;
        inputs.temperatureC = 40;
        inputs.amperes = 0;
        inputs.motorType = brushType;
        inputs.appliedPower = appliedPower;

        lastSimUpdate = System.currentTimeMillis();
    }

    /**
     * Sets the output power of the Motor.
     *
     * @param power A power level from <b>-1.0 to +1.0</b>
     */
    @Override
    public void set(double power) {
        appliedPower = MathUtil.clamp(power, -1, 1);
    }

    /**
     * Sets the inversion status of the Motor.
     *
     * @param inverted The value to use.
     */
    @Override
    public void setInverted(boolean inverted) {
        this.isInverted = inverted;
    }
}
