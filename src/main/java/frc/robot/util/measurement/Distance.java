package frc.robot.util.measurement;

import frc.robot.util.measurement.unit.DistanceUnit;


/**
 * This {@link Distance} class represents a fixed length in various Units.
 * @author Eric Gold
 * @since 0.0.0
 */
public class Distance extends BaseConverter<DistanceUnit> {

    /**
     * Constructs a fixed {@link Distance} in the specified Unit.
     * @param value The desired value.
     * @param unit The Unit in which the value is stored in.
     */
    public Distance(double value, DistanceUnit unit) {
        super(value, unit);
    }

    /**
     * Constructs a fixed {@link Distance} in the specified Unit.
     * @param value The desired value.
     * @param unit The Unit in which the value is stored in.
     */
    public static Distance from(double value, DistanceUnit unit) {
        return new Distance(value, unit);
    }

    /**
     * Constructs a fixed {@link Distance} in Feet.
     * @param value The desired feet.
     * @return A {@link Distance} instance.
     */
    public static Distance fromFeet(double value) { return from(value, DistanceUnit.FEET); }

    /**
     * Constructs a fixed {@link Distance} in Inches.
     * @param value The desired inches.
     * @return A {@link Distance} instance.
     */
    public static Distance fromInches(double value) { return from(value, DistanceUnit.INCHES); }

    /**
     * Constructs a fixed {@link Distance} in Meters.
     * @param value The desired meters.
     * @return A {@link Distance} instance.
     */
    public static Distance fromMeters(double value) { return from(value, DistanceUnit.METERS); }

    /**
     * Constructs a fixed {@link Distance} in Centimeters
     * @param value The desired centimeters.
     * @return A {@link Distance} instance.
     */
    public static Distance fromCentimeters(double value) { return from(value, DistanceUnit.CENTIMETERS); }

    /** The stored {@link Distance} in Feet. */
    public double toFeet() { return getValue(DistanceUnit.METERS); }

    /** The stored {@link Distance} in Inches. */
    public double toInches() { return getValue(DistanceUnit.INCHES); }

    /** The stored {@link Distance} in Meters. */
    public double toMeters() { return getValue(DistanceUnit.METERS); }

    /** The stored {@link Distance} in Centimeters. */
    public double toCentimeters() { return getValue(DistanceUnit.CENTIMETERS); }

    /**
     * @param convertUnit The unit to convert the stored value into.
     * @return The converted value.
     */
    @Override
    protected double getRawValue(DistanceUnit convertUnit) {
        switch (unit) {
            case FEET:
                switch (convertUnit) {
                    case FEET: return value; // no change
                    case INCHES: return value * 12;
                    case METERS: return value / 3.281;
                    case CENTIMETERS: return value / 30.48;
                    default: return value;
                }
            case INCHES:
                switch (convertUnit) {
                    case FEET: return value / 12;
                    case INCHES: return value; // no change
                    case METERS: return value / 39.37;
                    case CENTIMETERS: return value * 2.54;
                    default: return value;
                }
            case METERS:
                switch (convertUnit) {
                    case FEET: return value * 3.281;
                    case INCHES: return value * 39.37;
                    case METERS: return value; // no change
                    case CENTIMETERS: return value * 100;
                    default: return value;
                }
            case CENTIMETERS:
                switch (convertUnit) {
                    case FEET: return value / 30.48;
                    case INCHES: return value / 2.54;
                    case METERS: return value / 100;
                    case CENTIMETERS: return value; // no change
                    default: return value;
                }
            default: return value;
        }
    }
}
