package frc.robot.swerve.joystick;

public class DriveHIDConfiguration {
    private final JoystickThrottleMap throttleMap;
    private final boolean tcsEnabled;

    private final boolean sideAxisInverted;
    private final boolean forwardAxisInverted;
    private final boolean twistAxisInverted;

    public JoystickThrottleMap getThrottleMap() { return this.throttleMap; }
    public boolean isTCSEnabled() { return this.tcsEnabled; }
    public boolean isForwardAxisInverted() { return this.forwardAxisInverted; }
    public boolean isSideAxisInverted() { return this.sideAxisInverted; }
    public boolean isTwistAxisInverted() { return this.twistAxisInverted; }

    public DriveHIDConfiguration(JoystickThrottleMap throttleMap,
                                 boolean tcsEnabled,
                                 boolean forwardAxisInverted,
                                 boolean sideAxisInverted,
                                 boolean twistAxisInverted) {
        this.throttleMap = throttleMap;
        this.tcsEnabled = tcsEnabled;
        this.sideAxisInverted = sideAxisInverted;
        this.forwardAxisInverted = forwardAxisInverted;
        this.twistAxisInverted = twistAxisInverted;
    }
}
