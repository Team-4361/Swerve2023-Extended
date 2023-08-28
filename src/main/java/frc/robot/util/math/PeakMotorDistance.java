package frc.robot.util.math;

public class PeakMotorDistance {
    private final Distance maxDistance;
    private final double maxRotation;

    public PeakMotorDistance(Distance distance, double rotation) {
        assert rotation != 0;
        this.maxDistance = distance;
        this.maxRotation = rotation;
    }

    public Distance getDistance() {
        return maxDistance;
    }

    public double getRotation() {
        return maxRotation;
    }

    /** @return rotation to distance based on unit */
    public double rotationToDistance(double currentRotation) {
        return (currentRotation / maxRotation) * maxDistance.toValue();
    }

    public double distanceToRotation(double currentDistance) {
        return (currentDistance / maxDistance.toValue()) * maxRotation;
    }
}