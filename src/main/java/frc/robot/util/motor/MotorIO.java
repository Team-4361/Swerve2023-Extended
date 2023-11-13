package frc.robot.util.motor;

import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.util.log.VerbosityLevel;
import frc.robot.util.measurement.AngularVelocity;
import frc.robot.util.measurement.Distance;
import frc.robot.util.measurement.Temperature;
import org.littletonrobotics.junction.inputs.LoggableInputs;

/**
 * This {@link MotorIO} interface is designed to contain the bare-minimum Logged
 * methods and functionalities; this includes {@link Temperature},
 * {@link Distance} travelled, and {@link AngularVelocity} rotation.
 */
public interface MotorIO {
    /**
     * This {@link MotorIOInputs} class provides the required data.
     */
    class MotorIOInputs {
        /** The applied power of the Motor (-1.0 to +1.0) */
        public double appliedPower;

        public Distance distance;
        public AngularVelocity velocity;
        public
    }
}
