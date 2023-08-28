package frc.robot.swerve.joystick;

import frc.robot.util.math.AngularVelocity;
import frc.robot.util.math.Velocity;

public interface DriveController {
    Velocity getForwardVelocity(Velocity max);
    Velocity getSideVelocity(Velocity max);
    AngularVelocity getTwistVelocity(AngularVelocity max);
}
