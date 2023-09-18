package frc.robot.util.swerve;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SerialPort;

public class SwerveAHRS extends AHRS {
    private float rollOffset = 0;
    private float pitchOffset = 0;

    /**
     * Constructs the AHRS class using SPI communication and the default update rate.
     * <p>
     * This constructor should be used if communicating via SPI.
     * <p>
     *
     * @param spi_port_id SPI port to use.
     */
    public SwerveAHRS(SPI.Port spi_port_id) {
        super(spi_port_id);
    }

    /**
     * Constructs the AHRS class using I2C communication and the default update rate.
     * <p>
     * This constructor should be used if communicating via I2C.
     * <p>
     *
     * @param i2c_port_id I2C port to use
     */
    public SwerveAHRS(I2C.Port i2c_port_id) {
        super(i2c_port_id);
    }

    /**
     * Constructs the AHRS class using serial communication and the default update rate,
     * and returning processed (rather than raw) data.
     * <p>
     * This constructor should be used if communicating via either
     * TTL UART or USB Serial interface.
     * <p>
     *
     * @param serial_port_id SerialPort to use
     */
    public SwerveAHRS(SerialPort.Port serial_port_id) {
        super(serial_port_id);
    }

    /**
     * Returns the current pitch value (in degrees, from -180 to 180)
     * reported by the sensor.  Pitch is a measure of rotation around
     * the X Axis.
     *
     * @return The current pitch value in degrees (-180 to 180).
     */
    @Override
    public float getPitch() {
        return super.getPitch() - pitchOffset;
    }

    /**
     * Returns the current roll value (in degrees, from -180 to 180)
     * reported by the sensor.  Roll is a measure of rotation around
     * the X Axis.
     *
     * @return The current roll value in degrees (-180 to 180).
     */
    @Override
    public float getRoll() {
        return super.getRoll() - rollOffset;
    }

    /**
     * Reset the Yaw gyro.
     * <p>
     * Resets the Gyro Z (Yaw) axis to a heading of zero. This can be used if
     * there is significant drift in the gyro and it needs to be recalibrated
     * after it has been running.
     */
    @Override
    public void reset() {
        super.reset();
        rollOffset = getRoll();
        pitchOffset = getPitch();
    }
}
