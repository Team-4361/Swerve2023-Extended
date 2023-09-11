package frc.robot.util.joystick;

public interface IDriveHID {
    double DEFAULT_DEADBAND = 0.05;

    double getRobotX();
    double getRobotY();
    double getRobotTwist();
    double getDeadband();
    void setDeadband(double deadband);

    boolean isJoyXInverted();
    boolean isJoyYInverted();
    boolean isJoyTwistInverted();
}
