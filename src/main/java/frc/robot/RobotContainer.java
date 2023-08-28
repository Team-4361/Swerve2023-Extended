package frc.robot;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.swerve.joystick.DriveJoystick;
import frc.robot.swerve.joystick.SmoothThrottleMap;

public class RobotContainer {
    private static final SmoothThrottleMap throttleMap = new SmoothThrottleMap();

    public static DriveJoystick xyStick = new DriveJoystick(0, true, true, true, throttleMap, true);
    public static DriveJoystick zStick = new DriveJoystick(1, true, true, true, throttleMap, true);
    public static CommandXboxController xbox = new CommandXboxController(2);

    public RobotContainer() {
    }

}
