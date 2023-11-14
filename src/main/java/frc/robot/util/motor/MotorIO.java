package frc.robot.util.motor;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.math.geometry.Rotation2d;

import edu.wpi.first.units.Distance;
import edu.wpi.first.units.Temperature;
import edu.wpi.first.units.Velocity;
import org.littletonrobotics.junction.AutoLog;
import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.inputs.LoggableInputs;

/**
 * This {@link MotorIO} interface is designed to contain the bare-minimum Logged
 * methods and functionalities; this includes {@link Temperature},
 * {@link Distance} travelled, and {@link Velocity}
 *
 * @author Eric Gold
 * @since 0.0.0
 */
public interface MotorIO {
    /**
     * This {@link MotorIOInputs} class provides the required data which can <b>ONLY</b> be accessed through
     * <b>REAL</b> unique data -- such as simulation, "real", or replay.
     *
     * @author Eric Gold
     * @since 0.0.0
     */

    class MotorIOInputs implements LoggableInputs {

        private static final String POWER_STR = "Power";
        private static final String ROTATION_STR = "Rotations";
        private static final String VELOCITY_STR = "Velocity";
        private static final String TEMP_STR = "Temperature";
        private static final String AMP_STR = "Amperes";
        private static final String INVERTED_STR = "Inverted";
        private static final String TYPE_STR = "Motor Type";
        private static final String ID_STR = "ID";

        /** The applied power of the Motor (-1.0 to +1.0) */
        public double appliedPower;

        /** The travelled {@link Rotation2d} of the Motor. */
        public double rotations;

        /** The Velocity RPM of the Motor. */
        public double velocityRPM;

        /** The Temperature of the Motor in Celsius. */
        public double temperatureC;

        /** The output amperes of the Motor. */
        public double amperes;

        /** The inversion status of the Motor. */
        public boolean inverted;

        /** The {@link MotorType} of the Motor. */
        public MotorType motorType;

        /** The identifier of the Motor. */
        public long id;

        /** Constructs a new {@link MotorIOInputs} instance. */
        public MotorIOInputs() {
            appliedPower = 0;
            rotations = 0;
            velocityRPM = 0;
            temperatureC = 0;
            amperes = 0;
            inverted = false;
            motorType = null;
            id = 0;
        }

        private String parseHeader(String name) {
            return name;
        }

        /**
         * Updates a LogTable with the data to log.
         * @param table The {@link LogTable} to use.
         */
        @Override
        public void toLog(LogTable table) {
            table.put(parseHeader(POWER_STR), appliedPower);
            table.put(parseHeader(ROTATION_STR), rotations);
            table.put(parseHeader(VELOCITY_STR), velocityRPM);
            table.put(parseHeader(AMP_STR), amperes);
            table.put(parseHeader(INVERTED_STR), inverted);
            table.put(parseHeader(TYPE_STR), motorType.value);
            table.put(parseHeader(ID_STR), id);
        }

        /**
         * Updates data based on a LogTable.
         * @param table The {@link LogTable} to use.
         */
        @Override
        public void fromLog(LogTable table) {
            appliedPower = table.get(parseHeader(POWER_STR)).getDouble();
            rotations    = table.get(parseHeader(ROTATION_STR)).getDouble();
            velocityRPM  = table.get(parseHeader(VELOCITY_STR)).getDouble();
            amperes      = table.get(parseHeader(AMP_STR)).getDouble();
            inverted     = table.get(parseHeader(INVERTED_STR)).getBoolean();
            motorType    = MotorType.fromId((int)table.get(parseHeader(TYPE_STR)).getInteger());
            id           = table.get(parseHeader(ID_STR)).getInteger();
        }
    }

    ///////////////////////////////////////////////// Add all SETTER methods below!

    /**
     * Updates the required inputs.
     * @param inputs The {@link MotorIOInputs} to use.
     */
    void updateInputs(MotorIOInputs inputs);

    /**
     * Sets the output power of the Motor.
     * @param power A power level from <b>-1.0 to +1.0</b>
     */
    void set(double power);

    /**
     * Sets the inversion status of the Motor.
     * @param inverted The value to use.
     */
    void setInverted(boolean inverted);
}
