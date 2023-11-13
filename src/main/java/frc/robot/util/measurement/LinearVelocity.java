package frc.robot.util.measurement;

import frc.robot.util.measurement.unit.LinearVelocityUnit;

/**
 * This {@link LinearVelocity} class represents a fixed Linear Velocity in various Units.
 * @author Eric Gold
 * @since 0.0.0
 */
public class LinearVelocity extends BaseConverter<LinearVelocityUnit> {
    /**
     * Constructs a fixed {@link LinearVelocity} in the specified Unit.
     * @param value The desired value.
     * @param unit The Unit in which the value is stored in.
     */
    public LinearVelocity(double value, LinearVelocityUnit unit) {
        super(value, unit);
    }

    /**
     * Constructs a fixed {@link LinearVelocity} in KPH.
     * @param value The desired KPH.
     * @return A {@link LinearVelocity} instance.
     */
    public static LinearVelocity fromKPH(double value) { return from(value, LinearVelocityUnit.KPH); }

    /**
     * Constructs a fixed {@link LinearVelocity} in MPH.
     * @param value The desired MPH.
     * @return A {@link LinearVelocity} instance.
     */
    public static LinearVelocity fromMPH(double value) { return from(value, LinearVelocityUnit.MPH); }

    /**
     * Constructs a fixed {@link LinearVelocity} in MPS^2.
     * @param value The desired MPS^2.
     * @return A {@link LinearVelocity} instance.
     */
    public static LinearVelocity fromMPS2(double value) { return from(value, LinearVelocityUnit.MPS2); }

    /** @return The stored {@link LinearVelocity} in KPH. */
    public double toKPH() { return getValue(LinearVelocityUnit.KPH); }

    /** @return The stored {@link LinearVelocity} in MPH. */
    public double toMPH() { return getValue(LinearVelocityUnit.MPH); }

    /** @return The stored {@link LinearVelocity} in MPS^2. */
    public double toMPS2() { return getValue(LinearVelocityUnit.MPS2); }


    /**
     * @param convertUnit The unit to convert the stored value into.
     * @return The converted value.
     */
    @Override
    protected double getRawValue(LinearVelocityUnit convertUnit) {
        switch (unit) {
            case KPH: {
                switch (convertUnit) {
                    case KPH: return value;
                    case MPH: return value / 1.609;
                    case MPS2: return value / 3.60;
                }
            }
            case MPH: {
                switch (convertUnit) {
                    case KPH: return value * 1.609;
                    case MPH: return value;
                    case MPS2: return value / 2.237;
                }
            }
            case MPS2: {
                switch (convertUnit) {
                    case KPH: return value * 3.60;
                    case MPH: return value * 2.237;
                    case MPS2: return value;
                }
            }
            default: return value;
        }
    }

    /**
     * Constructs a fixed {@link LinearVelocity} in the specified Unit.
     * @param value The desired value.
     * @param unit The Unit in which the value is stored in.
     */
    public static LinearVelocity from(double value, LinearVelocityUnit unit) {
        return new LinearVelocity(value, unit);
    }
}
