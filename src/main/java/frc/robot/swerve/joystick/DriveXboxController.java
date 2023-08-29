package frc.robot.swerve.joystick;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.util.math.AngularVelocity;
import frc.robot.util.math.Velocity;

import static frc.robot.util.math.ExtendedMath.deadband;

public class DriveXboxController extends CommandXboxController implements DriveController {
    private final DriveHIDConfiguration config;

    public DriveXboxController(int port, DriveHIDConfiguration config) {
        super(port);
        this.config = config;
    }

    @Override
    public double getLeftX() {
        double val = deadband(super.getLeftX());
        val = config.getThrottleMap().getX(val);

        if (val == 0)
            return 0;

        if (config.isTCSEnabled()) {
            SlewRateLimiter limiter = config.getThrottleMap().getRateLimiter();
            if (limiter != null)
                val = limiter.calculate(val);
        }

        return (config.isSideAxisInverted()) ? -val : val;
    }
    @Override
    public double getLeftY() {
        double val = deadband(super.getLeftY());
        val = config.getThrottleMap().getY(val);

        if (val == 0)
            return 0;

        if (config.isTCSEnabled()) {
            SlewRateLimiter limiter = config.getThrottleMap().getRateLimiter();
            if (limiter != null)
                val = limiter.calculate(val);
        }

        return (config.isForwardAxisInverted()) ? -val : val;
    }

    @Override
    public double getRightX() {
        double val = deadband(super.getRightX());
        val = config.getThrottleMap().getTwist(val);

        if (val == 0)
            return 0;

        if (config.isTCSEnabled()) {
            SlewRateLimiter limiter = config.getThrottleMap().getRateLimiter();
            if (limiter != null)
                val = limiter.calculate(val);
        }

        return (config.isTwistAxisInverted()) ? -val : val;
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
