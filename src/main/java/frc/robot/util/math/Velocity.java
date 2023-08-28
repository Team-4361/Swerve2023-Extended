package frc.robot.util.math;

import edu.wpi.first.math.MathUtil;

public class Velocity implements GetterValue {
    private double metersPerSecondValue;
    private VelocityUnit unit;

    public Velocity(double value, VelocityUnit unit) {
        this.metersPerSecondValue = value;
        this.unit = unit;
    }

    public Velocity setVelocity(double value, VelocityUnit unit) {
        this.metersPerSecondValue = convert(value, unit, VelocityUnit.MPS);
        this.unit = unit;
        return this;
    }

    public double toMotorPower(Velocity maxSpeed) {
        return toMPS() / maxSpeed.toMPS();
    }

    public VelocityUnit getSpeedUnit() {
        return this.unit;
    }

    public double toValue(VelocityUnit unit) {
        return convert(metersPerSecondValue, VelocityUnit.MPS, unit);
    }

    @Override
    public double toValue() {
        return convert(metersPerSecondValue, VelocityUnit.MPS, this.unit);
    }

    public double toMPH() { return toValue(VelocityUnit.MPH); }
    public double toMPS() { return toValue(VelocityUnit.MPS); }
    public double toKPH() { return toValue(VelocityUnit.KPH); }

    public static Velocity fromValue(double value, VelocityUnit unit) {
        return new Velocity(value, unit);
    }

    public static Velocity fromMotorPower(double value, Velocity maxSpeed) {
        return Velocity.fromMPS(MathUtil.clamp(value, -1, 1) * maxSpeed.toMPS());
    }

    public static Velocity fromMPH(double value) { return fromValue(value, VelocityUnit.MPH); }
    public static Velocity fromMPS(double value) { return fromValue(value, VelocityUnit.MPS); }
    public static Velocity fromKPH(double value) { return fromValue(value, VelocityUnit.KPH); }

    public static double convert(double value, VelocityUnit oldUnit, VelocityUnit newUnit) {
        switch (oldUnit) {
            case KPH: {
                switch (newUnit) {
                    case KPH: return value;
                    case MPH: return value / 1.609;
                    case MPS: return value / 3.60;
                }
            }
            case MPH: {
                switch (newUnit) {
                    case KPH: return value * 1.609;
                    case MPH: return value;
                    case MPS: return value / 2.237;
                }
            }
            case MPS: {
                switch (newUnit) {
                    case KPH: return value * 3.60;
                    case MPH: return value * 2.237;
                    case MPS: return value;
                }
            }
            default: return value;
        }
    }
}
