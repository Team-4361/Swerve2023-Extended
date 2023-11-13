package frc.robot.util.measurement;

import frc.robot.util.measurement.unit.TemperatureUnit;

/**
 * This {@link Temperature} class represents a Temperature in various units.
 *
 * @author Eric Gold
 * @since 0.0.0
 */
public class Temperature extends BaseConverter<TemperatureUnit> {
    /**
     * Constructs a new {@link Temperature} in the desired unit.
     * @param value The desired value.
     * @param unit The Unit in which the value is stored in.
     */
    public Temperature(double value, TemperatureUnit unit) {
        super(value, unit);
    }

    /**
     * Constructs a new {@link Temperature} in the desired unit.
     * @param value The desired value.
     * @param unit The Unit in which the value is stored in.
     */
    public static Temperature from(double value, TemperatureUnit unit) {
        return new Temperature(value, unit);
    }

    /**
     * Constructs a new {@link Temperature} in Celsius.
     * @param value The desired value.
     * @return A new {@link Temperature} object.
     */
    public static Temperature fromC(double value) { return new Temperature(value, TemperatureUnit.CELSIUS); }

    /**
     * Constructs a new {@link Temperature} in Fahrenheit.
     * @param value The desired value.
     * @return A new {@link Temperature} object.
     */
    public static Temperature fromF(double value) { return new Temperature(value, TemperatureUnit.FAHRENHEIT); }

    /**
     * Constructs a new {@link Temperature} in Kelvin.
     * @param value The desired value.
     * @return A new {@link Temperature} object.
     */
    public static Temperature fromK(double value) { return new Temperature(value, TemperatureUnit.KELVIN); }

    /** @return The stored {@link Temperature} in Celsius. */
    public double toC() { return getValue(TemperatureUnit.CELSIUS); }

    /** @return The stored {@link Temperature} in Fahrenheit. */
    public double toF() { return getValue(TemperatureUnit.FAHRENHEIT); }

    /** @return The stored {@link Temperature} in Kelvin. */
    public double toK() { return getValue(TemperatureUnit.KELVIN); }

    /**
     * @param convertUnit The unit to convert the stored value into.
     * @return The converted value.
     */
    @Override
    protected double getRawValue(TemperatureUnit convertUnit) {
        switch (unit) {
            case KELVIN: {
                switch (convertUnit) {
                    case KELVIN: return value;
                    case CELSIUS: return value - 273.15;
                    case FAHRENHEIT: return (value - 273.15) * (9/5.0) + 32;
                }
            }
            case CELSIUS: {
                switch (convertUnit) {
                    case KELVIN: return value + 273.15;
                    case CELSIUS: return value;
                    case FAHRENHEIT: return (value * (9/5.0)) + 32;
                }
            }
            case FAHRENHEIT: {
                switch (convertUnit) {
                    case KELVIN: return (value - 32) * (5/9.0) + 273.15;
                    case CELSIUS: return (value - 32) * (5/9.0);
                    case FAHRENHEIT: return value;
                }
            }
            default: return value;
        }
    }
}
