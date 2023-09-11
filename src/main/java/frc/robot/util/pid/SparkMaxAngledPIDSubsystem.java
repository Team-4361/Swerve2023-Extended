package frc.robot.util.pid;

import com.revrobotics.CANSparkMax;
import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.util.math.GearRatio;

public class SparkMaxAngledPIDSubsystem extends SparkMaxPIDSubsystem {
    private final GearRatio gearRatio;

    @Override
    public double getRotation() {
        return gearRatio.motorRotationsToAngle(super.getRotation()).getDegrees();
    }

    @Override
    public double getTargetRotation() {
        return gearRatio.motorRotationsToAngle(super.getTargetRotation()).getDegrees();
    }

    @Override
    public void setTolerance(double angle) {
        super.setTolerance(angle);
    }

    @Override
    public void setTarget(double angle) {
        super.setTarget(gearRatio.angleToMotorRotations(Rotation2d.fromDegrees(angle)));
    }

    public SparkMaxAngledPIDSubsystem(String name, GearRatio ratio, CANSparkMax motor, double kP, double kI, double kD) {
        super(name, motor, kP, kI, kD);
        this.gearRatio = ratio;
    }

    public SparkMaxAngledPIDSubsystem(String name, GearRatio ratio, int motorID, double kP, double kI, double kD) {
        super(name, motorID, kP, kI, kD);
        this.gearRatio = ratio;
    }

    public SparkMaxAngledPIDSubsystem(String name, GearRatio ratio, int motorID) {
        super(name, motorID);
        this.gearRatio = ratio;
    }
}
