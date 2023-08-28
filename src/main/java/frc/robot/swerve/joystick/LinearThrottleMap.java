package frc.robot.swerve.joystick;

import edu.wpi.first.math.filter.SlewRateLimiter;

public class LinearThrottleMap implements JoystickThrottleMap {
    private final SlewRateLimiter limiter = new SlewRateLimiter(DEFAULT_LIMIT);

    @Override public double getX(double xVel) { return xVel; }
    @Override public double getY(double yVel) { return yVel; }
    @Override public double getTwist(double twistVel) { return twistVel; }
    @Override public SlewRateLimiter getRateLimiter() { return limiter; }
    @Override public String toString() { return "Linear"; }
}
