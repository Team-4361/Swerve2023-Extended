package frc.robot.util.pid;

import com.revrobotics.CANSparkMax;
import frc.robot.util.math.DistanceUnit;
import frc.robot.util.math.PeakMotorDistance;

public class SparkMaxDistancePIDSubsystem extends SparkMaxPIDSubsystem {
    private final PeakMotorDistance maximumDistance;

    @Override
    public double getRotation() {
        return maximumDistance.rotationToDistance(super.getRotation());
    }

    @Override
    public double getTargetRotation() {
        return maximumDistance.rotationToDistance(super.getTargetRotation());
    }

    @Override
    public void setTarget(double distance) {
        super.setTarget(maximumDistance.distanceToRotation(distance));
    }

    public SparkMaxDistancePIDSubsystem(String name, PeakMotorDistance maxDistance, CANSparkMax motor, double kP, double kI, double kD) {
        super(name, motor, kP, kI, kD);
        this.maximumDistance = maxDistance;
        setForwardLimit(maximumDistance.getRotation());
    }

    public SparkMaxDistancePIDSubsystem(String name, PeakMotorDistance maxDistance, int motorID, double kP, double kI, double kD) {
        super(name, motorID, kP, kI, kD);
        this.maximumDistance = maxDistance;
        setForwardLimit(maximumDistance.getRotation());
    }

    public SparkMaxDistancePIDSubsystem(String name, PeakMotorDistance maxDistance, int motorID) {
        super(name, motorID);
        this.maximumDistance = maxDistance;
        setForwardLimit(maximumDistance.getRotation());
    }
}
