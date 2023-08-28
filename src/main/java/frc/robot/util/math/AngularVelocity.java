package frc.robot.util.math;

import edu.wpi.first.math.MathUtil;

public class AngularVelocity implements GetterValue {
    private double radiansPerSecondValue;

    private AngularVelocityUnit unit;

    public AngularVelocity(double value, AngularVelocityUnit unit) {
        this.radiansPerSecondValue = value;
        this.unit = unit;
    }

    public AngularVelocity setVelocity(double value, AngularVelocityUnit unit) {
        this.radiansPerSecondValue = convert(value, unit, AngularVelocityUnit.RAD_S);
        this.unit = unit;
        return this;
    }

    public double toMotorPower(AngularVelocity maxVelocity) {
        return toRadPerSec() / maxVelocity.toRadPerSec();
    }

    public double toValue(AngularVelocityUnit unit) {
        return convert(radiansPerSecondValue, AngularVelocityUnit.RAD_S, unit);
    }

    @Override
    public double toValue() {
        return convert(radiansPerSecondValue, AngularVelocityUnit.RAD_S, this.unit);
    }

    public double toRadPerSec() { return toValue(AngularVelocityUnit.RAD_S); }
    public double toRPM() { return toValue(AngularVelocityUnit.RPM); }

    public static AngularVelocity fromValue(double value, AngularVelocityUnit unit) {
        return new AngularVelocity(value, unit);
    }

    public static AngularVelocity fromMotorPower(double value, AngularVelocity maxVelocity) {
        return AngularVelocity.fromRadPerSec(MathUtil.clamp(value, -1, 1) * maxVelocity.toRadPerSec());
    }

    public static AngularVelocity fromRadPerSec(double value) { return fromValue(value, AngularVelocityUnit.RAD_S); }
    public static AngularVelocity fromRPM(double value) { return fromValue(value, AngularVelocityUnit.RPM); }


    public static double convert(double value, AngularVelocityUnit oldUnit, AngularVelocityUnit newUnit) {
        switch (oldUnit) {
            case RPM: {
                switch (newUnit) {
                    case RPM: return value;
                    case RAD_S: return value * 0.1047198;
                }
            }
            case RAD_S: {
                switch (newUnit) {
                    case RPM: return value * 9.5492968;
                    case RAD_S: return value;
                }
            }
            default: return value;
        }
    }
}
