package frc.robot.util.measurement;

import frc.robot.util.measurement.unit.AngularVelocityUnit;

/**
 * This {@link AngularVelocity} class represents a fixed Angular Velocity in various Units.
 * @author Eric Gold
 * @since 0.0.0
 */
public class AngularVelocity extends BaseConverter<AngularVelocityUnit> {
    /**
     * Constructs a fixed {@link AngularVelocity} in the specified Unit.
     * @param value The desired value.
     * @param unit The Unit in which the value is stored in.
     */
    public AngularVelocity(double value, AngularVelocityUnit unit) {
        super(value, unit);
    }

    /**
     * @param convertUnit The unit to convert the stored value into.
     * @return The converted value.
     */
    @Override
    protected double getRawValue(AngularVelocityUnit convertUnit) {
        switch (unit) {
            case RPM: {
                switch (convertUnit) {
                    case RPM: return value;
                    case RAD_PER_SEC: return value * 0.1047198;
                }
            }
            case RAD_PER_SEC: {
                switch (convertUnit) {
                    case RPM: return Math.round(value * 9.5492968);
                    case RAD_PER_SEC: return value;
                }
            }
            default: return value;
        }
    }

    /**
     * Constructs a fixed {@link AngularVelocity} in the specified Unit.
     * @param value The desired value.
     * @param unit The Unit in which the value is stored in.
     */
    public static AngularVelocity from(double value, AngularVelocityUnit unit) {
        return new AngularVelocity(value, unit);
    }

    /**
     * Constructs a fixed {@link AngularVelocity} in RPM.
     * @param value The desired RPM.
     * @return A {@link AngularVelocity} instance.
     */
    public static AngularVelocity fromRPM(double value) { return from(value, AngularVelocityUnit.RPM); }

    /**
     * Constructs a fixed {@link AngularVelocity} in RAD/s.
     * @param value The desired RAD/s.
     * @return A {@link AngularVelocity} instance.
     */
    public static AngularVelocity fromRPS(double value) { return from(value, AngularVelocityUnit.RAD_PER_SEC); }

    /** @return The stored {@link AngularVelocity} in RPM. */
    public double toRPM() { return getValue(AngularVelocityUnit.RPM); }

    /** @return The stored {@link AngularVelocity} in RAD/s */
    public double toRPS() { return getValue(AngularVelocityUnit.RAD_PER_SEC); }
}
