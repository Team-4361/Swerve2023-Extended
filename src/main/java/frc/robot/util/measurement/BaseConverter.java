package frc.robot.util.measurement;

import frc.robot.util.math.ExtendedMath;

/**
 * A template class for Unit Conversion.
 * @param <U> The unit class type to allow.
 * @author Eric Gold
 * @since 0.0.0
 */
public abstract class BaseConverter<U extends Enum<U>> {
    protected U unit;
    protected double value;

    /**
     * Sets the value of the Type.
     * @param value The desired value.
     * @param unit The Unit in which the value is stored in.
     */
    public void set(double value, U unit) {
        this.value = value;
        this.unit = unit;
    }

    /** @return The Unit in which the value is stored in. */
    public U getUnit() { return this.unit; }

    /** @return The decimal value in the {@link #getUnit()} unit.*/
    public double getRawValue() { return getRawValue(unit); }

    /**
     * Constructs a new {@link BaseConverter}, with a value and stored unit.
     * @param value The desired value.
     * @param unit The unit in which the value is stored in.
     */
    public BaseConverter(double value, U unit) {
        this.unit = unit;
        this.value = value;
    }

    /**
     * @param convertUnit The unit to convert the stored value into.
     * @return The un-rounded converted value.
     */
    protected abstract double getRawValue(U convertUnit);

    /**
     * @param convertUnit The unit to convert the stored value into.
     * @return The un-rounded converted value.
     */
    public double getValue(U convertUnit) {
        return ExtendedMath.round(getRawValue(convertUnit), 2);
    }

    @Override
    public String toString() {
        return getRawValue() + " " + getUnit().name();
    }
}
