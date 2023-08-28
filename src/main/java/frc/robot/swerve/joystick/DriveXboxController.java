package frc.robot.swerve.joystick;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.util.math.AngularVelocity;
import frc.robot.util.math.Velocity;

public class DriveXboxController extends CommandXboxController implements DriveController {
    private JoystickThrottleMap throttleMap;
    private boolean tcsEnabled;

    private boolean sideAxisInverted;
    private boolean forwardAxisInverted;
    private boolean twistAxisInverted;

    public DriveXboxController setThrottleMap(JoystickThrottleMap map) {
        this.throttleMap = map;
        return this;
    }

    public DriveXboxController setTCSEnabled(boolean tcsEnabled) {
        this.tcsEnabled = tcsEnabled;
        return this;
    }

    public DriveXboxController setForwardAxisInverted(boolean inverted) {
        this.forwardAxisInverted = inverted;
        return this;
    }

    public DriveXboxController setSideAxisInverted(boolean inverted) {
        this.sideAxisInverted = inverted;
        return this;
    }

    public DriveXboxController setTwistAxisInverted(boolean inverted) {
        this.twistAxisInverted = inverted;
        return this;
    }

    public JoystickThrottleMap getThrottleMap() { return this.throttleMap; }
    public boolean isTCSEnabled() { return this.tcsEnabled; }
    public boolean isForwardAxisInverted() { return this.forwardAxisInverted; }
    public boolean isSideAxisInverted() { return this.sideAxisInverted; }
    public boolean isTwistAxisInverted() { return this.twistAxisInverted; }

    public DriveXboxController(int port, boolean forwardInverted, boolean sideInverted, boolean twistInverted, JoystickThrottleMap throttleMap, boolean tcsEnabled) {
        super(port);
        this.forwardAxisInverted = forwardInverted;
        this.sideAxisInverted = sideInverted;
        this.twistAxisInverted = twistInverted;
        this.throttleMap = throttleMap;
        this.tcsEnabled = tcsEnabled;
    }

    public DriveXboxController(int port) {
        this(port, true, true, true, new LinearThrottleMap(), true);
    }

    public double deadband(double value) {
        return (Math.abs(value) <= 0.05) ? 0 : value;
    }

    @Override
    public double getLeftX() {
        double val = deadband(super.getLeftX());
        val = throttleMap.getX(val);

        if (val == 0)
            return 0;

        if (tcsEnabled) {
            SlewRateLimiter limiter = throttleMap.getRateLimiter();
            if (limiter != null)
                val = limiter.calculate(val);
        }

        return (sideAxisInverted) ? -val : val;
    }
    @Override
    public double getLeftY() {
        double val = deadband(super.getLeftY());
        val = throttleMap.getY(val);

        if (val == 0)
            return 0;

        if (tcsEnabled) {
            SlewRateLimiter limiter = throttleMap.getRateLimiter();
            if (limiter != null)
                val = limiter.calculate(val);
        }

        return (forwardAxisInverted) ? -val : val;
    }

    @Override
    public double getRightX() {
        double val = deadband(super.getRightX());
        val = throttleMap.getTwist(val);

        if (val == 0)
            return 0;

        if (tcsEnabled) {
            SlewRateLimiter limiter = throttleMap.getRateLimiter();
            if (limiter != null)
                val = limiter.calculate(val);
        }

        return (twistAxisInverted) ? -val : val;
    }

    @Override
    public Velocity getForwardVelocity(Velocity max) {
        return Velocity.fromMotorPower(getLeftY(), max);
    }

    @Override
    public Velocity getSideVelocity(Velocity max) {
        return Velocity.fromMotorPower(getLeftX(), max);
    }

    @Override
    public AngularVelocity getTwistVelocity(AngularVelocity max) {
        return AngularVelocity.fromMotorPower(getRightX(), max);
    }
}
