package frc.robot.util.swerve;

import frc.robot.util.math.Distance;
import frc.robot.util.math.GearRatio;
import frc.robot.util.math.Velocity;

public class SwerveModuleConfig {
    private final GearRatio driveRatio;
    private final GearRatio turnRatio;
    private final Distance wheelDiameter;
    private final Velocity maxVelocity;

    public GearRatio getDriveRatio() { return this.driveRatio; }
    public GearRatio getTurnRatio() { return this.turnRatio; }
    public Distance getWheelDiameter() { return this.wheelDiameter; }
    public Velocity getMaxVelocity() { return this.maxVelocity; }

    public SwerveModuleConfig(Velocity maxVelocity, GearRatio driveRatio, GearRatio turnRatio, Distance wheelDiameter) {
        this.maxVelocity = maxVelocity;
        this.driveRatio = driveRatio;
        this.turnRatio = turnRatio;
        this.wheelDiameter = wheelDiameter;
    }
}
