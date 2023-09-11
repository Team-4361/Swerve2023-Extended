package frc.robot.util.joystick;

import edu.wpi.first.wpilibj2.command.button.CommandPS4Controller;
import frc.robot.util.math.ExtendedMath;

public class DrivePS4Controller extends CommandPS4Controller implements IDriveHID {
    private final boolean joyXInverted;
    private final boolean joyYInverted;
    private final boolean joyTwistInverted;
    private double deadband;

    /**
     * Constructs a {@link DrivePS4Controller}
     * @param port The USB port ID the Xbox Controller is connected to.
     * @param joyXInverted If the <b>PS4 Left X-Axis</b> is inverted from: (- left, + right)
     * @param joyYInverted If the <b>PS4 Left Y-Axis</b> is inverted from: (- up, + down)
     * @param joyTwistInverted If the <b>PS4 Right X-Axis</b> is inverted from: (- left, + right)
     * @param deadband The minimum value the Xbox Controller will recognize.
     */
    public DrivePS4Controller(int port, boolean joyXInverted, boolean joyYInverted, boolean joyTwistInverted, double deadband) {
        super(port);
        this.joyXInverted = joyXInverted;
        this.joyYInverted = joyYInverted;
        this.joyTwistInverted = joyTwistInverted;
        this.deadband = deadband;
    }

    /**
     * Constructs a {@link DrivePS4Controller} with all axes non-inverted.
     * @param port The USB port ID the PS4 Controller is connected to.
     * @param deadband The minimum value the PS4 Controller will recognize.
     */
    public DrivePS4Controller(int port, double deadband) {
        super(port);
        this.joyXInverted = false;
        this.joyYInverted = false;
        this.joyTwistInverted = false;
        this.deadband = deadband;
    }

    /**
     * Constructs a {@link DrivePS4Controller} with all axes inverted, and the default deadband.
     * @param port The USB port ID the PS4 Controller is connected to.
     */
    public DrivePS4Controller(int port) {
        super(port);
        this.joyXInverted = false;
        this.joyYInverted = false;
        this.joyTwistInverted = false;
        this.deadband = DEFAULT_DEADBAND;
    }

    /**
     * @return The X-axis (-1.0 to +1.0) using the robot-coordinate system. (+X forward, +Y left)
     */
    @Override
    public double getRobotX() {
        return joyYInverted ? ExtendedMath.deadband(getLeftY(), deadband) : ExtendedMath.deadband(-getLeftY(), deadband);
    }

    /**
     * @return The Y-axis (-1.0 to +1.0) using the robot-coordinate system. (+X forward, +Y left)
     */
    @Override
    public double getRobotY() {
        return joyXInverted ? ExtendedMath.deadband(getLeftX(), deadband) : ExtendedMath.deadband(-getLeftX(), deadband);
    }

    /**
     * @return The Twist-axis (-1.0 to +1.0) using the robot-coordinate system. (+X forward, +Y left)
     */
    @Override
    public double getRobotTwist() {
        return joyTwistInverted ? ExtendedMath.deadband(getRightX(), deadband) : ExtendedMath.deadband(-getRightX(), deadband);
    }

    @Override
    public double getDeadband() {
        return deadband;
    }

    @Override
    public void setDeadband(double deadband) {
        this.deadband = deadband;
    }

    @Override
    public boolean isJoyXInverted() {
        return joyXInverted;
    }

    @Override
    public boolean isJoyYInverted() {
        return joyYInverted;
    }

    @Override
    public boolean isJoyTwistInverted() {
        return joyTwistInverted;
    }
}
