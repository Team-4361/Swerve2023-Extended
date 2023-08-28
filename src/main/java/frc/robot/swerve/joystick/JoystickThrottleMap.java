package frc.robot.swerve.joystick;

import edu.wpi.first.math.filter.SlewRateLimiter;

public interface JoystickThrottleMap {
    double DEFAULT_LIMIT = 1.5;

    double getX(double xVel);
    double getY(double yVel);
    double getTwist(double twistVel);

    SlewRateLimiter getRateLimiter();
}
