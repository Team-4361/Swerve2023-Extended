package frc.robot.swerve.joystick;

import edu.wpi.first.math.filter.SlewRateLimiter;

public class SmoothThrottleMap implements JoystickThrottleMap {
    private final SlewRateLimiter limiter = new SlewRateLimiter(DEFAULT_LIMIT);

    @Override public double getX(double xVel) { return Math.pow(xVel, 3); }
    @Override public double getY(double yVel) { return Math.pow(yVel, 3); }
    @Override public double getTwist(double twistVel) { return Math.pow(twistVel, 3); }
    @Override public SlewRateLimiter getRateLimiter() { return limiter; }
    @Override public String toString() { return "Smooth"; }
}
