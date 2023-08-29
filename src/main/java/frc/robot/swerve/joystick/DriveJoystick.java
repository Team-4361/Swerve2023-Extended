package frc.robot.swerve.joystick;


import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import frc.robot.util.math.AngularVelocity;
import frc.robot.util.math.Velocity;

public class DriveJoystick extends CommandJoystick implements DriveController {
    private DriveHIDConfiguration config;


    public DriveJoystick(int port, DriveHIDConfiguration config) {
        super(port);
        this.config = config;
    }

    @Override
    public double getX() {
        double val = super.getX();
        val = config.getThrottleMap().getX(val);

        if (config.isTCSEnabled()) {
            SlewRateLimiter limiter = config.getThrottleMap().getRateLimiter();
            if (limiter != null)
                val = limiter.calculate(val);
        }

        return (config.isSideAxisInverted()) ? -val : val;
    }

    @Override
    public double getY() {
        double val = super.getY();
        val = config.getThrottleMap().getY(val);

        if (config.isTCSEnabled()) {
            SlewRateLimiter limiter = config.getThrottleMap().getRateLimiter();
            if (limiter != null)
                val = limiter.calculate(val);
        }

        return (config.isForwardAxisInverted()) ? -val : val;
    }

    @Override
    public double getTwist() {
        double val = super.getTwist();
        val = config.getThrottleMap().getTwist(val);

        if (config.isTCSEnabled()) {
            SlewRateLimiter limiter = config.getThrottleMap().getRateLimiter();
            if (limiter != null)
                val = limiter.calculate(val);
        }

        return (config.isTwistAxisInverted()) ? -val : val;
    }

    @Override
    public Velocity getForwardVelocity(Velocity max) {
        return Velocity.fromMotorPower(getY(), max);
    }

    @Override
    public Velocity getSideVelocity(Velocity max) {
        return Velocity.fromMotorPower(getX(), max);
    }

    @Override
    public AngularVelocity getTwistVelocity(AngularVelocity max) {
        return AngularVelocity.fromMotorPower(getTwist(), max);
    }
}
