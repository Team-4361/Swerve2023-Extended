package frc.robot.util.joystick;

import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandPS4Controller;
import frc.robot.util.math.ExtendedMath;

public class DriveJoystick extends CommandJoystick implements IDriveHID {
    private final boolean joyXInverted;
    private final boolean joyYInverted;
    private final boolean joyTwistInverted;
    private double deadband;

    /**
     * Constructs a {@link DriveJoystick}
     * @param port The USB port ID the Joystick is connected to.
     * @param joyXInverted If the <b>Joystick X-Axis</b> is inverted from: (- left, + right)
     * @param joyYInverted If the <b>Joystick Y-Axis</b> is inverted from: (- up, + down)
     * @param joyTwistInverted If the <b>Joystick Twist-Axis</b> is inverted from: (- left, + right)
     * @param deadband The minimum value the Joystick will recognize.
     */
    public DriveJoystick(int port,
                         boolean joyXInverted,
                         boolean joyYInverted,
                         boolean joyTwistInverted,
                         double deadband) {
        super(port);
        this.joyXInverted = joyXInverted;
        this.joyYInverted = joyYInverted;
        this.joyTwistInverted = joyTwistInverted;
        this.deadband = deadband;
    }

    /**
     * Constructs a {@link DriveJoystick} with all axes non-inverted.
     * @param port The USB port ID the Joystick is connected to.
     * @param deadband The minimum value the Joystick will recognize.
     */
    public DriveJoystick(int port, double deadband) {
        super(port);
        this.joyXInverted = false;
        this.joyYInverted = false;
        this.joyTwistInverted = false;
        this.deadband = deadband;
    }

    /**
     * Constructs a {@link DriveJoystick} with all axes inverted, and the default deadband.
     * @param port The USB port ID the Joystick is connected to.
     */
    public DriveJoystick(int port) {
        super(port);
        this.joyXInverted = false;
        this.joyYInverted = false;
        this.joyTwistInverted = false;
        this.deadband = DEFAULT_DEADBAND;
    }

    /** @return The X-axis (-1.0 to +1.0) using the robot-coordinate system. (+X forward, +Y left) */
    @Override
    public double getRobotX() {
        return joyYInverted ? ExtendedMath.deadband(getY(), deadband) : ExtendedMath.deadband(-getY(), deadband);
    }

    /** @return The Y-axis (-1.0 to +1.0) using the robot-coordinate system. (+X forward, +Y left) */
    @Override
    public double getRobotY() {
        return joyXInverted ? ExtendedMath.deadband(getX(), deadband) : ExtendedMath.deadband(-getX(), deadband);
    }

    /** @return The Twist-axis (-1.0 to +1.0) using the robot-coordinate system. (+X forward, +Y left) */
    @Override
    public double getRobotTwist() {
        return joyTwistInverted ? ExtendedMath.deadband(getTwist(), deadband) : ExtendedMath.deadband(-getTwist(), deadband);
    }

    @Override public double getDeadband() { return deadband; }
    @Override public void setDeadband(double deadband) { this.deadband = deadband; }

    @Override public boolean isJoyXInverted() {
        return joyXInverted;
    }

    @Override public boolean isJoyYInverted() {
        return joyYInverted;
    }

    @Override public boolean isJoyTwistInverted() {
        return joyTwistInverted;
    }
}
