package frc.robot.util.math;

import edu.wpi.first.math.geometry.Rotation2d;

public class GearRatio {
    private final double ratio;

    public GearRatio(double ratio) {
        this.ratio = ratio;
    }

    public double angleToMotorRotations(Rotation2d angle) {
        return (ratio / 360) * angle.getDegrees();
    }

    public Rotation2d motorRotationsToAngle(double rotations) {
        return Rotation2d.fromDegrees((360 / ratio) * rotations);
    }

    public Rotation2d motorRotationsToAngle(double rotations, boolean max360) {
        double deg = (360 / ratio) * rotations;
        if (max360) 
            return Rotation2d.fromDegrees(deg % 360);
        else 
            return Rotation2d.fromDegrees(deg);
        
    }

    public Velocity getWheelVelocity(double motorRPM, Distance wheelDiameter) {
        double radius = (wheelDiameter.toMeters() / 2);
        double adjustedRPM = motorRotationsToAngle(motorRPM).getRotations();

        // convert motor rotations to degrees
        return Velocity.fromMPS(radius * (Math.PI * 2) * (adjustedRPM / 60));
    }

    public double getMotorRPM(Velocity velocity, Distance wheelDiameter) {
        double radius = (wheelDiameter.toMeters() / 2);

        double motorRPM = (60 / (Math.PI * 2) * radius) * velocity.toMPS();
        return angleToMotorRotations(Rotation2d.fromRotations(motorRPM))
    }

    public Distance getWheelDistance(double motorRotations, Distance wheelDiameter) {
        double radius = (wheelDiameter.toMeters() / 2);
        double adjustedRotations = motorRotationsToAngle(motorRotations).getRotations();

        // convert motor rotations to degrees
        return Distance.fromMeters(radius * (Math.PI * 2) * adjustedRotations);
    }


    public static GearRatio fromRatio(double ratio) {
        return new GearRatio(ratio);
    }

    public static double angleToMotorRotations(Rotation2d angle, double ratio) {
        return (ratio / 360) * angle.getDegrees();
    }

    public static Rotation2d motorRotationsToAngle(double rotations, double ratio) {
        return Rotation2d.fromDegrees((360 / ratio) * rotations);
    }

    public static Velocity getWheelVelocity(double motorRotations, double wheelRadius, double ratio) {
        return Velocity.fromValue((2 * (Math.PI * wheelRadius)) * ((motorRotations * ratio) / 60), VelocityUnit.MPS);
    }
}
