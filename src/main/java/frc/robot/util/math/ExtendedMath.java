package frc.robot.util.math;

public class ExtendedMath {
    /**
     * Checks if the actual value is within a specified tolerance of the expected value
     * @param expected The value to be expected.
     * @param actual The actual value.
     * @param tolerance The maximum error or tolerance that the value can be offset to still be true.
     * @return True/false depending on tolerance.
     */
    public static boolean inTolerance(double expected, double actual, double tolerance) {
        return Math.abs(expected - actual) <= tolerance;
    }

    public static double min(double... values) {
        if (values == null || values.length == 0)
            return 0;

        double output = values[0];

        for (double value : values) {
            if (value < output)
                output = value;
        }

        return output;
    }

    public static double min(GetterValue... values) {
        if (values == null || values.length == 0)
            return 0;

        double output = values[0].toValue();

        for (GetterValue value : values) {
            double mps = value.toValue();
            if (mps < output)
                output = mps;
        }

        return output;
    }

    public static SwerveVelocities calculateMaxSpeeds(GearRatio driveRatio, GearRatio turnRatio, Distance wheelDiameter,
                                                                                double driveFreeSpeedRPM, double turnFreeSpeedRPM) {
        if (driveRatio == null || turnRatio == null)
            return null;

        Velocity driveSpeed = driveRatio.getWheelVelocity(driveFreeSpeedRPM, wheelDiameter);
        AngularVelocity turnSpeed = AngularVelocity.fromRPM(
                turnRatio.motorRotationsToAngle(turnFreeSpeedRPM).getRotations()
        );

        return new SwerveVelocities(driveSpeed, driveSpeed, turnSpeed);
    }
}
