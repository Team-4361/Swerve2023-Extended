package frc.robot.util.motor;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;
import edu.wpi.first.math.MathUtil;

/**
 * This {@link MotorIOReal} class is designed to function with a <b>REAL</b> {@link CANSparkMax}.
 *
 * @author Eric Gold
 * @since 0.0.0
 */
public class MotorIOReal implements MotorIO {
    private final CANSparkMax motor;
    private final RelativeEncoder encoder;
    private final MotorType motorType;
    private final int id;

    /**
     * Constructs a new {@link MotorIOReal} using the specified parameters.
     */
    public MotorIOReal(int id, MotorType type) {
        this.id = id;
        this.motor = new CANSparkMax(id, type);
        this.motorType = type;

        if (type == MotorType.kBrushless) {
            this.encoder = motor.getEncoder();
        } else {
            this.encoder = null;
        }
    }

    /**
     * Updates the required inputs.
     * @param inputs The {@link MotorIOInputs} to use.
     */
    @Override
    public void updateInputs(MotorIOInputs inputs) {
        if (motorType == MotorType.kBrushless) {
            inputs.rotations = encoder.getPosition();
        }

        inputs.amperes = motor.getOutputCurrent();
        inputs.temperatureC = motor.getMotorTemperature();
        inputs.inverted = motor.getInverted();
        inputs.appliedPower = motor.get();
        inputs.velocityRPM = encoder.getVelocity();
        inputs.motorType = motorType;
        inputs.id = id;
    }

    /**
     * Sets the output power of the Motor.
     *
     * @param power A power level from <b>-1.0 to +1.0</b>
     */
    @Override
    public void set(double power) {
        motor.set(MathUtil.clamp(power, -1, 1));
    }

    /**
     * Sets the inversion status of the Motor.
     *
     * @param inverted The value to use.
     */
    @Override
    public void setInverted(boolean inverted) {
        motor.setInverted(inverted);
    }
}
